package com.example.finalproject.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE userName =(:name) ")
    User findByName(String name);

    @Query("SELECT * FROM user WHERE userName LIKE :name AND "
            + "password LIKE :pwd LIMIT 1")
    User findByNamePwd(String name, String pwd);

    @Query("UPDATE user SET password=(:new_password) WHERE userName=(:name)")
    void updatePassword(String new_password,String name);

    @Query("UPDATE user SET user_city=(:new_city) WHERE userName=(:name)")
    void updateCity(String new_city,String name);

    @Query("UPDATE user SET user_show_day=(:new_show_day) WHERE userName=(:name)")
    void updateShowDay(String new_show_day,String name);

    @Insert
    void insertOne(User user);

    @Delete
    void delete(User user);
}
