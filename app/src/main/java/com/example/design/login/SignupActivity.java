package com.example.design.login;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    TextView back;
    EditText name, id, pw, pw2, email, birthyear, birthdate, birthday;
    Button pwcheck, submit;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firestore = FirebaseFirestore.getInstance();

        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        name = findViewById(R.id.signName);
        id = findViewById(R.id.signID);
        pw = findViewById(R.id.signPW);
        pw2 = findViewById(R.id.signPW2);
        email = findViewById(R.id.signmail);
        birthyear = findViewById(R.id.signBirth);
        birthdate = findViewById(R.id.signBirth2);
        birthday = findViewById(R.id.signBirth3);

        pwcheck = findViewById(R.id.pwcheckbutton);
        pwcheck.setOnClickListener(v -> {
            if (pw.getText().toString().equals(pw2.getText().toString())) {
                pwcheck.setText("일치");
            } else {
                Toast.makeText(SignupActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
            }
        });

        submit = findViewById(R.id.signupbutton);
        submit.setOnClickListener(v -> {
            String userId = id.getText().toString().trim();
            String password = pw.getText().toString().trim();
            String nameStr = name.getText().toString().trim();
            String emailStr = email.getText().toString().trim();
            String birth = birthyear.getText().toString() + birthdate.getText().toString() + birthday.getText().toString();

            if (userId.isEmpty() || password.isEmpty() || nameStr.isEmpty() || emailStr.isEmpty() || birth.length() != 8) {
                Toast.makeText(this, "모든 항목을 올바르게 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 사용자 정보를 Firestore에 저장
            Map<String, Object> user = new HashMap<>();
            user.put("password", password);
            user.put("name", nameStr);
            user.put("email", emailStr);
            user.put("birth", birth);
            user.put("group", null); // 초기에는 그룹 없음

            firestore.collection("users")
                    .document(userId)
                    .set(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show();

                        // userId 저장 (예: SharedPreferences)
                        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                .edit()
                                .putString("userId", userId)
                                .apply();

                        // 로그인 화면으로 이동
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "회원가입 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

    }
}
