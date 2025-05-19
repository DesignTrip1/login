package com.example.design;


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


public class SignupActivity extends AppCompatActivity {
    TextView back;
    EditText name,id,pw,pw2,email,birthyear,birthdate,birthday;
    Button pwcheck, submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //뒤로 가기 버튼
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed() );
        myDBHelper myHelper;
        myHelper = new myDBHelper(this);

        //기입 항목
        name = findViewById(R.id.signName);
        id=findViewById(R.id.signID);
        pw=findViewById(R.id.signPW);
        pw2=findViewById(R.id.signPW2);
        email=findViewById(R.id.signmail);
        birthyear=findViewById(R.id.signBirth);
        birthdate=findViewById(R.id.signBirth2);
        birthday=findViewById(R.id.signBirth3);

        //비밀번호 확인 버튼
        pwcheck = findViewById(R.id.pwcheckbutton);
        pwcheck.setOnClickListener(v -> {
            if(pw.getText().toString().equals(pw2.getText().toString())){
                pwcheck.setText("일치");
            }else{
                Toast.makeText(SignupActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
            }
        });
        submit = findViewById(R.id.signupbutton);
        submit.setOnClickListener(v -> {
        //회원가입 완료 버튼
        submit = findViewById(R.id.signupbutton);
        String userId = id.getText().toString().trim();
        String password = pw.getText().toString().trim();
        String nameStr = name.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String birth = birthyear.getText().toString() + birthdate.getText().toString() + birthday.getText().toString();

        if (userId.isEmpty() || password.isEmpty() || nameStr.isEmpty() || emailStr.isEmpty() || birth.length() != 8) {
            Toast.makeText(this, "모든 항목을 올바르게 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = myHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("USERID", userId);
            values.put("PASSWORD", password);
            values.put("Name", nameStr);
            values.put("gNumber", 0);
            values.put("Email", emailStr);
            values.put("Birth", birth);

            long result = db.insert("UserTBL", null, values);

            if (result == -1) {
                Toast.makeText(this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "회원가입 중 오류 발생", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    });

    }
    public static class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "DesignTDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS UserTBL (" +
                    "USERID TEXT PRIMARY KEY, " +
                    "PASSWORD TEXT, " +
                    "Name TEXT, " +
                    "gNumber INTEGER, " +
                    "Email TEXT, " +
                    "Birth TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS UserTBL");
            onCreate(db);
        }
    }
}