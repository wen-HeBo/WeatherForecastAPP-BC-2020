package com.example.finalproject.DataBase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @NonNull
    @PrimaryKey
    private String userName;

    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name="user_city")
    private String user_city;
    @ColumnInfo(name="user_show_day")
    private String user_show_day;

    public User(String userName,String password){
        this.userName=userName;
        this.password=password;
    }

    public String getUserName(){
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setUserName(String userName){
        this.userName=userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_city() {
        return user_city;
    }

    public void setUser_city(String user_city) {
        this.user_city = user_city;
    }

    public String getUser_show_day() {
        return user_show_day;
    }

    public void setUser_show_day(String user_show_day) {
        this.user_show_day = user_show_day;
    }

    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
}

