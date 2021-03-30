package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalproject.DataBase.AppDatabase;
import com.example.finalproject.DataBase.History;
import com.example.finalproject.DataBase.User;
import com.example.finalproject.DataBase.UserDao;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences share = getSharedPreferences("Login", Context.MODE_PRIVATE);
        Boolean isLogin=share.getBoolean("LoginBool",false);
        if (!isLogin) {

            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name")
                    .addMigrations()
                    .allowMainThreadQueries()
                    .build();
            UserDao userDao = db.userDao();

            final EditText et_login_username=findViewById(R.id.et_login_username);
            final EditText et_login_pwd=findViewById(R.id.et_login_pwd);


            //登录
            Button bt_login_submit = findViewById(R.id.bt_login_submit);
            bt_login_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!et_login_username.getText().toString().equals("")){
                        if(!et_login_pwd.getText().toString().equals("")){
                            User user = userDao.findByName(et_login_username.getText().toString());
                            if (user == null){
                                Toast.makeText(getApplicationContext(),et_login_username.getText().toString()+"用户不存在",Toast.LENGTH_LONG).show();
                                et_login_username.setText("");
                                et_login_pwd.setText("");
                            }
                            else{
                                user = userDao.findByNamePwd(et_login_username.getText().toString(),et_login_pwd.getText().toString());
                                if (user==null){
                                    Toast.makeText(getApplicationContext(),"密码输入错误",Toast.LENGTH_LONG).show();
                                    et_login_pwd.setText("");
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_LONG);
                                    Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                                    intent.putExtra("user_account",user.getUserName());
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"用户名不能为空",Toast.LENGTH_LONG).show();
                    }
                }
            });

            //注册
            Button bt_login_register = findViewById(R.id.bt_login_register);
            bt_login_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else{
            String strAccount = share.getString("Account", "");
            Intent intent = new Intent(MainActivity.this,MainActivity2.class);
            intent.putExtra("user_account",strAccount);
            startActivity(intent);
            finish();
        }

    }


}