package com.example.design.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.design.MainActivity;
import com.example.design.databinding.ActivityLoginBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";

    private SharedPreferences sharedPreferences;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        firestore = FirebaseFirestore.getInstance();

        // 자동 로그인 체크
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        String savedUserId = sharedPreferences.getString(KEY_USER_ID, null);

        if (isLoggedIn && savedUserId != null) {
            // 로그인 상태 유지 시 바로 메인 화면으로 이동
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginbutton.setOnClickListener(v -> {
            String userId = binding.editID.getText().toString().trim();
            String password = binding.ediPassword.getText().toString().trim();

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "ID와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            firestore.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String dbPassword = documentSnapshot.getString("password");
                            if (password.equals(dbPassword)) {
                                Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                                // 로그인 성공 시 SharedPreferences에 저장
                                saveLoginInfo(userId);

                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "로그인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        binding.signin.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }

    // 로그인 상태 및 userId 저장 메서드
    private void saveLoginInfo(String userId) {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_ID, userId)
                .apply();
    }

    // 저장된 userId 가져오는 메서드 (필요할 때 호출)
    public String getSavedUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    // 로그아웃 시 호출하는 메서드 (예시)
    public void logout() {
        sharedPreferences.edit()
                .clear()
                .apply();
        // 로그아웃 후 로그인 화면으로 이동
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
