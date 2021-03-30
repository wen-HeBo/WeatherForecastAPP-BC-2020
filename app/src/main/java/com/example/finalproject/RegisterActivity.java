package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalproject.DataBase.AppDatabase;
import com.example.finalproject.DataBase.User;
import com.example.finalproject.DataBase.UserDao;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name")
                .addMigrations()
                .allowMainThreadQueries()
                .build();
        UserDao userDao = db.userDao();

        final EditText et_register_username = findViewById(R.id.et_register_username);
        final EditText et_register_pwd1 = findViewById(R.id.et_register_pwd1);
        final EditText et_register_pwd2 = findViewById(R.id.et_register_pwd2);
        Button bt_register_register = findViewById(R.id.bt_register_register);
        bt_register_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et_register_username.getText().toString().equals("")){
                    if(!et_register_pwd1.getText().toString().equals("")){
                        if(!et_register_pwd2.getText().toString().equals("")){
                            if(et_register_pwd1.getText().toString().equals(et_register_pwd2.getText().toString())){
                                User user1 = userDao.findByName(et_register_username.getText().toString());
                                if(user1 == null){
                                    //Toast.makeText(RegisterActivity.this, "ss", Toast.LENGTH_SHORT).show();
                                    User user = new User(et_register_username.getText().toString(),et_register_pwd1.getText().toString());
                                    userDao.insertOne(user);
                                    Toast.makeText(getApplicationContext(),"注册成功！",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(RegisterActivity.this, et_register_username.getText().toString()+"用户已存在", Toast.LENGTH_SHORT).show();
                                    et_register_pwd1.setText("");
                                    et_register_pwd2.setText("");
                                    et_register_username.setText("");
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "两次密码输入不相等", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "请再次输入密码", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}