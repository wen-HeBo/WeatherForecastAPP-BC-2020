package com.example.finalproject.ui.weather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import com.amap.api.maps2d.LocationSource;
import com.example.finalproject.DataBase.AppDatabase;
import com.example.finalproject.DataBase.History;
import com.example.finalproject.DataBase.User;
import com.example.finalproject.DataBase.UserDao;
import com.example.finalproject.R;
import com.example.finalproject.bean.City;
import com.example.finalproject.bean.Province;
import com.google.gson.Gson;
import com.qweather.sdk.bean.Basic;
import com.qweather.sdk.bean.IndicesBean;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.IndicesType;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class TodayFragment extends Fragment {

    private String weatherCountry = "http://guolin.tech/api/china/";//?????????????????????api
    private String weatherProvince, weatherCity;//??????????????????id?????????id
    private ArrayList<Province> provinceList;//?????????????????????
    private ArrayList<City> cityList;//???????????????????????????

    private String weatherUrl = "https://free-api.heweather.net/s6/weather/now?location=%s&key=%s";//???????????? ?????????api??????
    private String cityId;//??????????????????id???????????????"weather_id"=CN101281001

    //???????????????????????????https://dev.heweather.com/
    //?????????apk ??? ??????id ?????? ???apk???key?????????????????????
    private String userName = "HE2012221100111020";
    private String key = "11c6fd0ab88f43659458307e38af9efe";//???????????????key

    String province, city;//????????????????????????????????????????????????
    private String TAG = "TAG";

    //title????????????
    private TextView titleText;
    private Button manage_city_btn;
    //now_weather????????????
    private TextView nowTemperatureTV;
    private TextView nowDayWeatherQltyTV;
    private TextView nowToady;
    private TextView nowMinMaxTemperature;
    //dailyWeather?????????????????????
    private TextView dailyDate1;
    private TextView dailyWeather1;
    private ImageView dailyWeatherImage1;
    private TextView dailyTemperature1;
    private TextView dailyDate2;
    private TextView dailyWeather2;
    private ImageView dailyWeatherImage2;
    private TextView dailyTemperature2;
    private TextView dailyDate3;
    private TextView dailyWeather3;
    private ImageView dailyWeatherImage3;
    private TextView dailyTemperature3;
    //weather_index?????????????????????
    private TextView weatherSendibleTemperatureTv;
    private TextView weatherHumitidyTv;
    private TextView weatherVisibilityTv;
    private TextView weatherRiskLevelTv;
    private TextView weatherPrecipitationTv;
    private TextView weatherPressureTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_today, container, false);

//      ?????? SDK ??????????????????????????????????????????????????????????????????
        titleText = root.findViewById(R.id.weather_title_cityname);

        nowTemperatureTV=root.findViewById(R.id.now_temperature);
        nowDayWeatherQltyTV=root.findViewById(R.id.now_dayweather_qlty);
        nowToady = root.findViewById(R.id.now_today);
        nowMinMaxTemperature=root.findViewById(R.id.now_min_max_temperature);

        dailyDate1 = (TextView) root.findViewById(R.id.daily_date1);
        dailyWeather1 = (TextView) root.findViewById(R.id.daily_weather1);
        dailyWeatherImage1 = (ImageView) root.findViewById(R.id.daily_weather_image1);
        dailyTemperature1 = (TextView) root.findViewById(R.id.daily_temperature1);
        dailyDate2 = (TextView) root.findViewById(R.id.daily_date2);
        dailyWeather2 = (TextView) root.findViewById(R.id.daily_weather2);
        dailyWeatherImage2 = (ImageView) root.findViewById(R.id.daily_weather_image2);
        dailyTemperature2 = (TextView) root.findViewById(R.id.daily_temperature2);
        dailyDate3 = (TextView) root.findViewById(R.id.daily_date3);
        dailyWeather3 = (TextView) root.findViewById(R.id.daily_weather3);
        dailyWeatherImage3 = (ImageView) root.findViewById(R.id.daily_weather_image3);
        dailyTemperature3 = (TextView) root.findViewById(R.id.daily_temperature3);

        weatherSendibleTemperatureTv = (TextView) root.findViewById(R.id.weather_sendible_temperature_tv);
        weatherHumitidyTv = (TextView) root.findViewById(R.id.weather_humitidy_tv);
        weatherVisibilityTv = (TextView) root.findViewById(R.id.weather_visibility_tv);
        weatherRiskLevelTv = (TextView) root.findViewById(R.id.weather_risk_level_tv);
        weatherPrecipitationTv = (TextView) root.findViewById(R.id.weather_precipitation_tv);
        weatherPressureTv = (TextView) root.findViewById(R.id.weather_pressure_tv);


        HeConfig.init(userName, key);
//      ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? ??? https://free-api.heweather.net/s6/sdk/
        HeConfig.switchToDevService();

        AppDatabase db = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, "database-name")
                .addMigrations()
                .allowMainThreadQueries()
                .build();
        User user=db.userDao().findByName(getActivity().getIntent().getStringExtra("user_account"));
        city=user.getUser_city();
        if (city.equals("??????")){
            province="??????";
        }
        else {
            province=city;
        }
        titleText.setText(city);
        queryWeather();

        manage_city_btn = root.findViewById(R.id.manage_city_btn);
        manage_city_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase db = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, "database-name")
                        .addMigrations()
                        .allowMainThreadQueries()
                        .build();
                db.userDao().updateCity("??????",getActivity().getIntent().getStringExtra("user_account"));
                db.userDao().updateShowDay("7",getActivity().getIntent().getStringExtra("user_account"));
//
//                insterS(db,51,"??????","7","0","2020-12-16","???");
//                insterS(db,52,"??????","8","3","2020-12-17","??????");
//                insterS(db,53,"??????","4","2","2020-12-18","??????");
//                insterS(db,54,"??????","8","0","2020-12-19","???");
//                insterS(db,55,"??????","10","0","2020-12-20","??????");
//                insterS(db,46,"??????","12","2","2020-12-21","???");
//                insterS(db,47,"??????","6","2","2020-12-22","???");
//                insterS(db,48,"??????","10","6","2020-12-23","???");
//                insterS(db,49,"??????","13","1","2020-12-24","???");
//                insterS(db,50,"??????","11","3","2020-12-25","???");
//                insterS(db,61,"??????","14","5","2020-12-26","??????");
//                insterS(db,62,"??????","13","5","2020-12-27","??????");
//                insterS(db,63,"??????","13","7","2020-12-28","??????");
//                insterS(db,64,"??????","12","0","2020-12-29","??????");
//                insterS(db,65,"??????","2","-4","2020-12-30","??????");

//                insterS(db,16,"??????","1","-9","2020-12-16","???");
//                insterS(db,17,"??????","3","-6","2020-12-17","???");
//                insterS(db,18,"??????","0","-9","2020-12-18","???");
//                insterS(db,19,"??????","3","-6","2020-12-19","???");
//                insterS(db,20,"??????","5","-7","2020-12-20","???");
//                insterS(db,21,"??????","5","-8","2020-12-21","???");
//                insterS(db,22,"??????","6","-6","2020-12-22","??????");
//                insterS(db,23,"??????","7","-5","2020-12-23","???");
//                insterS(db,24,"??????","4","-7","2020-12-24","???");
//                insterS(db,25,"??????","5","-7","2020-12-25","??????");
//                insterS(db,26,"??????","4","-6","2020-12-26","???");
//                insterS(db,27,"??????","6","-6","2020-12-27","???");
//                insterS(db,28,"??????","0","-7","2020-12-28","???");
//                insterS(db,29,"??????","-5","-12","2020-12-29","???");
//                insterS(db,30,"??????","-6","-12","2020-12-30","??????");
//
//                insterS(db,31,"??????","6","2","2020-12-16","??????");
//                insterS(db,32,"??????","8","4","2020-12-17","??????");
//                insterS(db,33,"??????","7","4","2020-12-18","??????");
//                insterS(db,34,"??????","8","1","2020-12-19","???");
//                insterS(db,35,"??????","8","2","2020-12-20","??????");
//                insterS(db,36,"??????","9","3","2020-12-21","???");
//                insterS(db,37,"??????","11","5","2020-12-22","???");
//                insterS(db,38,"??????","12","6","2020-12-23","???");
//                insterS(db,39,"??????","11","4","2020-12-24","??????");
//                insterS(db,40,"??????","11","5","2020-12-25","??????");
//                insterS(db,41,"??????","13","9","2020-12-26","??????");
//                insterS(db,42,"??????","13","5","2020-12-27","???");
//                insterS(db,43,"??????","11","6","2020-12-28","??????");
//                insterS(db,44,"??????","6","-1","2020-12-29","??????");
//                insterS(db,45,"??????","0","-5","2020-12-30","???");
//                Toast.makeText(getContext(), "chg", Toast.LENGTH_SHORT).show();

            }

            private void insterS(AppDatabase db,int i, String ci, String max, String min, String date, String info) {
                History history = new History();
                history.setHistory_city(ci);
                history.setHistory_id(i);
                history.setMax_tem(max);
                history.setMin_tem(min);
                history.setWeather_date(date);
                history.setWeather_info(info);
                db.historyDao().insertAll(history);
            }
        });


        return root;
    }


    private void queryWeather() {
        provinceList = new ArrayList<Province>();//????????????
        cityList = new ArrayList<City>();//???????????????????????????
        new Thread() {
            @Override
            public void run() {
                try {
                    //weatherCountry = "http://guolin.tech/api/china/"
                    URL url = new URL(weatherCountry);
                    //????????????url???????????????HttpURLConnection??????????????????
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");//??????????????????????????????????????????
                    connection.setConnectTimeout(3000);//??????????????????????????????????????????

                    //?????????????????????????????????????????????????????????
                    InputStream is = connection.getInputStream();
                    //??????????????????????????????????????????????????????
                    InputStreamReader isr = new InputStreamReader(is);
                    //?????????????????????????????????
                    BufferedReader br = new BufferedReader(isr);
                    //Toast.makeText(getActivity(),"kaishi",Toast.LENGTH_SHORT).show();
                    StringBuffer sb = new StringBuffer();
                    String string;

                    //?????????
                    while ((string = br.readLine()) != null) {
                        sb.append(string);
                    }

                    String result = sb.toString();
                    Log.d("MainActivity", "" + result);
                    JSONArray provinceArray = new JSONArray(result);
                    for (int i = 0; i < provinceArray.length(); i++) {
                        JSONObject provinceInfo = provinceArray.getJSONObject(i);//????????????????????????
                        Province provinceBean = new Province();//???????????????????????????
                        Gson gson = new Gson();//??????Gson????????????

                        //??????????????????json????????????????????????????????????????????????
                        provinceBean = gson.fromJson(provinceInfo.toString(), Province.class);
                        //??????????????????????????????????????????????????????
                        provinceList.add(provinceBean);
                    }

                    for (Province pro : provinceList) {
                        //???????????????????????????????????????
                        if (pro.getName().equals(province)) {
                            //???????????????
                            //???????????? weatherProvince = "http://guolin.tech/api/china/1/"
                            weatherProvince = weatherCountry + pro.getId() + "/";
                        }
                    }

                    Log.d("WeatherProvince", "" + weatherProvince+province);
                    //???????????? weatherProvince = "http://guolin.tech/api/china/1/"
                    url = new URL(weatherProvince);
                    connection = (HttpURLConnection) url.openConnection();//????????????url????????????HttpURLConnection??????????????????
                    connection.setRequestMethod("GET");//??????????????????????????????????????????
                    connection.setConnectTimeout(3000);//??????????????????????????????????????????

                    //?????????????????????????????????????????????????????????
                    is = connection.getInputStream();
                    //??????????????????????????????????????????????????????
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    StringBuffer sb2 = new StringBuffer();
                    //?????????
                    while ((string = br.readLine()) != null) {
                        sb2.append(string);
                    }

                    String result2 = sb2.toString();
                    Log.d("MainActivity2", "" + result2);

                    JSONArray cityArray = new JSONArray(result2);
                    for (int i = 0; i < cityArray.length(); i++) {
                        JSONObject cityInfo = cityArray.getJSONObject(i);//??????????????????????????????
                        City cityBean = new City();//???????????????????????????
                        Gson gson = new Gson();//??????Gson????????????
                        //??????????????????json????????????????????????????????????????????????
                        cityBean = gson.fromJson(cityInfo.toString(), City.class);
                        //??????????????????????????????????????????????????????
                        cityList.add(cityBean);
                    }
                    for (City c : cityList) {
                        //???????????????????????????????????????
                        if (c.getName().equals(city)) {
                            //??????????????????????????? weatherCity = "http://guolin.tech/api/china/1/1/"
                            weatherCity = weatherProvince + c.getId() + "/";
                        }
                    }

                    Log.d("WeatherCity", ""+weatherCity+city);

                    //???????????? weatherCity = "http://guolin.tech/api/china/1/1/"
                    url = new URL(weatherCity);
                    connection = (HttpURLConnection) url.openConnection();//????????????url????????????HttpURLConnection??????????????????
                    connection.setRequestMethod("GET");//??????????????????????????????????????????
                    connection.setConnectTimeout(3000);//??????????????????????????????????????????
                    //?????????????????????????????????????????????????????????
                    is = connection.getInputStream();
                    //??????????????????????????????????????????????????????
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    StringBuffer sb3 = new StringBuffer();
                    //?????????
                    while ((string = br.readLine()) != null) {
                        sb3.append(string);
                    }
                    String result3 = sb3.toString();
                    Log.d("MainActivity3", "" + result3);
                    JSONArray jsonArray = new JSONArray(result3);
                    JSONObject cityIdInfo = jsonArray.getJSONObject(0);
                    cityId=cityIdInfo.getString("weather_id");
                    //???????????????
                    String weatherApi = String.format(weatherUrl, cityId, key);
                    Log.d("WeatherApi", "" + weatherApi);
                    if(getActivity()!=null){
                        queryWeather2();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void queryWeather2(){
        final String[] air = new String[1];
        /**
         * ????????????
         */
        QWeather.getAirNow(getActivity(), cityId, Lang.ZH_HANS , new QWeather.OnResultAirNowListener() {

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "Weather Air onError: ", throwable);
            }

            @Override
            public void onSuccess(AirNowBean airNowBean) {
                Log.i(TAG, " Weather Now onSuccess: " + new Gson().toJson(airNowBean));
                if ( Code.OK.getCode().equalsIgnoreCase(airNowBean.getCode()) ) {
                    AirNowBean.NowBean AQI=airNowBean.getNow();
                    if(AQI.getCategory().equals("") || AQI.getCategory()!=null){
                        air[0] =AQI.getCategory();
                    }
                }else {
                    //???????????????????????????????????????
                    String status = airNowBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });

        /**
         * ?????????????????????????????????
         * ?????????2017-4-19  ?????????????????????  ??????????????????    ?????????-?????????
         */
        QWeather.getWeather3D(getActivity(), cityId, new QWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "Weather 3D onError: ", throwable);
            }
            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                Log.i(TAG, " Weather Now onSuccess: " + new Gson().toJson(weatherDailyBean));
                if ( Code.OK.getCode().equalsIgnoreCase(weatherDailyBean.getCode()) ) {
                    List<WeatherDailyBean.DailyBean> basic = weatherDailyBean.getDaily();

                    dailyDate1.setText(basic.get(0).getFxDate());
                    dailyWeather1.setText(basic.get(0).getTextDay());
                    try {
                        int resId = 0;
                        try {
                            resId = (int) R.mipmap.class.getField("icon_"+basic.get(1).getIconDay()+"d").get(null);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(),resId);
                        dailyWeatherImage1.setImageBitmap(logoBitmap);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    dailyTemperature1.setText(basic.get(0).getTempMin() + "??  " + basic.get(0).getTempMax() + "??");
                    dailyDate2.setText(basic.get(1).getFxDate());
                    dailyWeather2.setText(basic.get(1).getTextDay());
                    try {
                        int resId2 = 0;
                        try {
                            resId2 = (int) R.mipmap.class.getField("icon_"+basic.get(1).getIconDay()+"d").get(null);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        Bitmap logoBitmap2 = BitmapFactory.decodeResource(getResources(),resId2);
                        dailyWeatherImage2.setImageBitmap(logoBitmap2);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    dailyTemperature2.setText(basic.get(1).getTempMin() + "??  " + basic.get(1).getTempMax() + "??");

                    dailyDate3.setText(basic.get(2).getFxDate());
                    dailyWeather3.setText(basic.get(2).getTextDay());
                    try {
                        int resId3 = 0;
                        try {
                            resId3 = (int) R.mipmap.class.getField("icon_"+basic.get(2).getIconDay()+"d").get(null);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        Bitmap logoBitmap3 = BitmapFactory.decodeResource(getResources(),resId3);
                        dailyWeatherImage3.setImageBitmap(logoBitmap3);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    dailyTemperature3.setText(basic.get(2).getTempMin() + "??  " + basic.get(2).getTempMax() + "??");

                }else {
                    //???????????????????????????????????????
                    String status = weatherDailyBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });


        /**
         * ????????????
         * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         *
         * @param context  ?????????
         * @param location ????????????
         * @param lang     ???????????????????????????????????????????????????????????????
         * @param unit     ????????????????????????m???????????????i???????????????????????????
         * @param listener ????????????????????????
         */
        QWeather.getWeatherNow(getActivity(), cityId, Lang.ZH_HANS , Unit.METRIC , new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "Weather Now onError: ", throwable);
            }
            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                Log.i(TAG, " Weather Now onSuccess: " + new Gson().toJson(weatherNowBean));
                if ( Code.OK.getCode().equalsIgnoreCase(weatherNowBean.getCode()) ){
                    //??????????????????

                    Basic basic=weatherNowBean.getBasic();
                    titleText.setText(city);

                    WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                    nowTemperatureTV.setText(now.getTemp() + "???");
                    if(air[0]!=null){
                        nowDayWeatherQltyTV.setText(now.getText() + "|??????" + air[0]);
                    }
                    else {
                        nowDayWeatherQltyTV.setText(now.getText() + "|??????");
                    }

                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("EEEE");
                    String tempToday = format.format(date) + "  ??????";
                    nowToady.setText(tempToday);
                    nowMinMaxTemperature.setText("???????????????"+now.getFeelsLike() + "??  ");

                    weatherSendibleTemperatureTv.setText("????????????" + now.getFeelsLike() + "");
                    weatherHumitidyTv.setText("??????" +now.getHumidity() + "%");
                    weatherVisibilityTv.setText("?????????" + now.getVis() + "??????");
                    weatherRiskLevelTv.setText(now.getWindDir()+now.getWindScale() + "???");
                    weatherPrecipitationTv.setText("?????????" + now.getPrecip() + "mm");
                    weatherPressureTv.setText("??????" + now.getPressure() + "??????");
                } else {
                    //???????????????????????????????????????
                    String status = weatherNowBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });

    }

}