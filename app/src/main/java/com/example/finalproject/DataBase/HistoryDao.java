package com.example.finalproject.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM history WHERE weather_date IN (:dates) AND history_city= (:city)")
    List<History> selectByDate(String[] dates, String city);

    @Query("SELECT * FROM history")
    List<History> selectAll();

    @Insert
    void insertAll(History... histories);

    @Delete
    void deleteAll(History... histories);
}