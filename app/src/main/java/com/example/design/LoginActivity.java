package com.example.design;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.design.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    // SharedPreferences 관련 필드 선언
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // 자동 로그인 체크
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        if (isLoggedIn) {
            // 자동 로그인 상태면 바로 메인화면으로 이동
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 로그인 버튼 클릭 이벤트
        binding.loginbutton.setOnClickListener(v -> {
            String id = binding.editID.getText().toString();
            String pw = binding.ediPassword.getText().toString();

            myDBHelper myHelper;
            SQLiteDatabase sqlDB;
            myHelper = new myDBHelper(this);
            sqlDB =myHelper.getReadableDatabase();
            Cursor cursor = sqlDB.rawQuery("SELECT PASSWORD FROM UserTBL WHERE USERID = ?", new String[]{id});

            if (cursor.moveToFirst()) {
                String dbPw = cursor.getString(0);
                if (dbPw.equals(pw)) {
                    Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    // 로그인 성공 시 로그인 상태 저장
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_IS_LOGGED_IN, true);
                    editor.apply();

                    startActivity(new Intent(this, MainActivity.class));
                    finish(); // 로그인 화면 종료
                } else {
                    Toast.makeText(this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
            sqlDB.close();
        });

        // 회원가입 텍스트 클릭 이벤트
        binding.signin.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });
    }
    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context){
            super(context,"DesignTDB",null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL("CREATE TABLE IF NOT EXISTS UserTBL (" +
                    "USERID TEXT PRIMARY KEY, " +
                    "PASSWORD TEXT," +
                    "Name TEXT, " +
                    "gNumber INTEGER," +
                    "Email TEXT," +
                    "Birth TEXT);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("Drop Table If exists UserTBL");
            onCreate(db);
        }
    }
}
