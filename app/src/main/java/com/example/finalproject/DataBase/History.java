package com.example.finalproject.DataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class History {
    @PrimaryKey
    private int history_id;

    @ColumnInfo(name="history_city")
    private String history_city;
    @ColumnInfo(name="max_tem")
    private String max_tem;
    @ColumnInfo(name="min_tem")
    private String min_tem;
    @ColumnInfo(name="weather_info")
    private String weather_info;
    @ColumnInfo(name="weather_date")
    private String weather_date;


    public String getWeather_date() {
        return weather_date;
    }

    public void setWeather_date(String weather_date) {
        this.weather_date = weather_date;
    }

    public int getHistory_id() {
        return history_id;
    }

    public String getHistory_city() {
        return history_city;
    }

    public String getMax_tem() {
        return max_tem;
    }

    public String getMin_tem() {
        return min_tem;
    }

    public String getWeather_info() {
        return weather_info;
    }

    public void setHistory_city(String history_city) {
        this.history_city = history_city;
    }


    public void setHistory_id(int history_id) {
        this.history_id = history_id;
    }

    public void setMax_tem(String max_tem) {
        this.max_tem = max_tem;
    }

    public void setMin_tem(String min_tem) {
        this.min_tem = min_tem;
    }

    public void setWeather_info(String weather_info) {
        this.weather_info = weather_info;
    }
}

