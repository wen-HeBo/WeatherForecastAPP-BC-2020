package com.example.finalproject.ui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.finalproject.DataBase.AppDatabase;
import com.example.finalproject.DataBase.User;
import com.example.finalproject.R;

import java.util.List;

public class SettingFragment extends Fragment {

    private View root;
    private String account;//用户账号
    private String password;//密码
    private String city;//默认城市
    private int show_day;//历史天数

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_setting, container, false);
        AppDatabase db = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, "database-name")
                .addMigrations()
                .allowMainThreadQueries()
                .build();
        init(db);//初始化

        LinearLayout user_main = root.findViewById(R.id.user_main);
        LinearLayout user_message = root.findViewById(R.id.user_message);
        user_message.setVisibility(View.GONE);

        ListView lv = (ListView)root.findViewById(R.id.lv_message);
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(getContext(),R.array.lvItem,R.layout.list_item2);
        lv.setAdapter(adapter);
       // setListViewHeightBasedOnChildren(lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                user_main.setVisibility(View.GONE);
                user_message.setVisibility(View.VISIBLE);
            }
        });

        //修改密码
        Button password_sure=root.findViewById(R.id.password_sure);
        EditText old_pwd=root.findViewById(R.id.old_pwd);
        EditText change_password=root.findViewById(R.id.change_password);
        EditText change_password_again=root.findViewById(R.id.change_password_again);
        password_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (old_pwd.getText().toString().equals("")){
                    Toast.makeText(root.getContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                }
                else if (change_password.getText().toString().equals("")){
                    Toast.makeText(root.getContext(),"请输入新密码",Toast.LENGTH_SHORT).show();
                }
                else if (change_password_again.getText().toString().equals("")){
                    Toast.makeText(root.getContext(),"请再次输入新密码",Toast.LENGTH_SHORT).show();
                }
                else if(change_password.getText().toString().equals(change_password_again.getText().toString())){
                    if(db.userDao().findByNamePwd(account,old_pwd.getText().toString())!=null){
                        String new_password=change_password.getText().toString();
                        db.userDao().updatePassword(new_password,account);
                        user_main.setVisibility(View.VISIBLE);
                        user_message.setVisibility(View.GONE);
                        Toast.makeText(root.getContext(),"修改成功",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(root.getContext(),"原来密码不正确",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(root.getContext(),"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
                    change_password.getText().clear();
                    change_password_again.getText().clear();
                }

            }
        });

        //显示天数选择
        Spinner day_number_sp=root.findViewById(R.id.day_number_sp);
        day_number_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String new_show_day = (String)day_number_sp.getItemAtPosition(position);//从spinner中获取被选择的数据

                db.userDao().updateShowDay(new_show_day,account);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //默认城市选择
        Button select_city=root.findViewById(R.id.select_city);
        select_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView user_city=root.findViewById(R.id.user_city);
                PopupMenu pm=new PopupMenu(root.getContext(),select_city);
                pm.getMenuInflater().inflate(R.menu.select_city,pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case  R.id.beijing:
                                user_city.setText("北京");
                                db.userDao().updateCity("北京",getActivity().getIntent().getStringExtra("user_account"));
                                break;
                            case  R.id.shanghai:
                                user_city.setText("上海");
                                db.userDao().updateCity("上海",getActivity().getIntent().getStringExtra("user_account"));
                                break;
                            case  R.id.hangzhou:
                                user_city.setText("杭州");
                                db.userDao().updateCity("杭州",getActivity().getIntent().getStringExtra("user_account"));
                                break;
                        }
                        return false;
                    }
                });
                pm.show();
            }
        });

        //自动登录
        Switch isLogin=root.findViewById(R.id.isLogin);
        isLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 创建SharedPreferences对象用于储存帐号和密码,并将其私有化
                    SharedPreferences share = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
                    // 获取编辑器来存储数据到sharedpreferences中
                    SharedPreferences.Editor editor = share.edit();
                    editor.putString("Account", account);
                    editor.putString("Password",password);
                    editor.putBoolean("LoginBool", true);
                    // 将数据提交到sharedpreferences中
                    editor.apply();
                }else {
                    SharedPreferences share = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
                    share.edit().putBoolean("LoginBool", false).apply();
                }
            }
        });
        return root;
    }

    //初始化
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public void init(AppDatabase db){
        Intent i=getActivity().getIntent();
        //用户名
        TextView user_account=root.findViewById(R.id.user_account);
        user_account.setText(i.getStringExtra("user_account"));
        account=i.getStringExtra("user_account");
        //是否自动登录
        SharedPreferences share = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        boolean islog=share.getBoolean("LoginBool",false);
        Switch isLogin=root.findViewById(R.id.isLogin);
        isLogin.setChecked(islog);

        //获取默认城市和历史天数
        User user=db.userDao().findByName(getActivity().getIntent().getStringExtra("user_account"));
        //密码
        password=user.getPassword();
        //默认城市
        city=user.getUser_city();
        //历史显示天数
        show_day=Integer.parseInt(user.getUser_show_day());
        Message msg=new Message();
        msg.what=1;
        handler.sendMessage(msg);
    }
    //更新UI
    @SuppressLint("HandlerLeak")
    private final Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Spinner day_number_sp = root.findViewById(R.id.day_number_sp);
                String[] day_data = {"3", "4", "5", "6", "7"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_spinner_item, day_data);
                day_number_sp.setAdapter(adapter);
                //默认城市
                TextView user_city = root.findViewById(R.id.user_city);
                user_city.setText(city);
                //历史显示天数
                day_number_sp.setSelection(show_day - 3);
            }
        }
    };
}