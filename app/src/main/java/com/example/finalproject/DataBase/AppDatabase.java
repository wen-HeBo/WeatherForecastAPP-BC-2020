package com.example.finalproject.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class,History.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract HistoryDao historyDao();
}

