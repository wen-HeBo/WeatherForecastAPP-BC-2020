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

    private String weatherCountry = "http://guolin.tech/api/china/";//中国的省份城市api
    private String weatherProvince, weatherCity;//获取到的省份id和城市id
    private ArrayList<Province> provinceList;//中国的省份集合
    private ArrayList<City> cityList;//具体省份的城市集合

    private String weatherUrl = "https://free-api.heweather.net/s6/weather/now?location=%s&key=%s";//和风天气 免费的api接口
    private String cityId;//具体城市天气id，如湛江，"weather_id"=CN101281001

    //注册和风天气开发者https://dev.heweather.com/
    //创建的apk 的 用户id 以及 该apk的key（包名要一致）
    private String userName = "HE2012221100111020";
    private String key = "11c6fd0ab88f43659458307e38af9efe";//自己申请的key

    String province, city;//接收输入的省份和城市收用的字符串
    private String TAG = "TAG";

    //title中的控件
    private TextView titleText;
    private Button manage_city_btn;
    //now_weather中的控件
    private TextView nowTemperatureTV;
    private TextView nowDayWeatherQltyTV;
    private TextView nowToady;
    private TextView nowMinMaxTemperature;
    //dailyWeather中的控件的声明
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
    //weather_index中的控件的声明
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

//      使用 SDK 时，需提前进行账户初始化（全局执行一次即可）
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
//      个人开发者、企业开发者、普通用户等所有使用免费数据的用户需要切换到免费服务域名 即 https://free-api.heweather.net/s6/sdk/
        HeConfig.switchToDevService();

        AppDatabase db = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, "database-name")
                .addMigrations()
                .allowMainThreadQueries()
                .build();
        User user=db.userDao().findByName(getActivity().getIntent().getStringExtra("user_account"));
        city=user.getUser_city();
        if (city.equals("杭州")){
            province="浙江";
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
                db.userDao().updateCity("杭州",getActivity().getIntent().getStringExtra("user_account"));
                db.userDao().updateShowDay("7",getActivity().getIntent().getStringExtra("user_account"));
//
//                insterS(db,51,"杭州","7","0","2020-12-16","晴");
//                insterS(db,52,"杭州","8","3","2020-12-17","多云");
//                insterS(db,53,"杭州","4","2","2020-12-18","小雨");
//                insterS(db,54,"杭州","8","0","2020-12-19","阴");
//                insterS(db,55,"杭州","10","0","2020-12-20","多云");
//                insterS(db,46,"杭州","12","2","2020-12-21","晴");
//                insterS(db,47,"杭州","6","2","2020-12-22","晴");
//                insterS(db,48,"杭州","10","6","2020-12-23","阴");
//                insterS(db,49,"杭州","13","1","2020-12-24","晴");
//                insterS(db,50,"杭州","11","3","2020-12-25","晴");
//                insterS(db,61,"杭州","14","5","2020-12-26","多云");
//                insterS(db,62,"杭州","13","5","2020-12-27","多云");
//                insterS(db,63,"杭州","13","7","2020-12-28","小雨");
//                insterS(db,64,"杭州","12","0","2020-12-29","中雨");
//                insterS(db,65,"杭州","2","-4","2020-12-30","多云");

//                insterS(db,16,"北京","1","-9","2020-12-16","晴");
//                insterS(db,17,"北京","3","-6","2020-12-17","晴");
//                insterS(db,18,"北京","0","-9","2020-12-18","晴");
//                insterS(db,19,"北京","3","-6","2020-12-19","晴");
//                insterS(db,20,"北京","5","-7","2020-12-20","晴");
//                insterS(db,21,"北京","5","-8","2020-12-21","晴");
//                insterS(db,22,"北京","6","-6","2020-12-22","多云");
//                insterS(db,23,"北京","7","-5","2020-12-23","晴");
//                insterS(db,24,"北京","4","-7","2020-12-24","晴");
//                insterS(db,25,"北京","5","-7","2020-12-25","多云");
//                insterS(db,26,"北京","4","-6","2020-12-26","晴");
//                insterS(db,27,"北京","6","-6","2020-12-27","阴");
//                insterS(db,28,"北京","0","-7","2020-12-28","晴");
//                insterS(db,29,"北京","-5","-12","2020-12-29","晴");
//                insterS(db,30,"北京","-6","-12","2020-12-30","多云");
//
//                insterS(db,31,"上海","6","2","2020-12-16","多云");
//                insterS(db,32,"上海","8","4","2020-12-17","小雨");
//                insterS(db,33,"上海","7","4","2020-12-18","小雨");
//                insterS(db,34,"上海","8","1","2020-12-19","阴");
//                insterS(db,35,"上海","8","2","2020-12-20","多云");
//                insterS(db,36,"上海","9","3","2020-12-21","晴");
//                insterS(db,37,"上海","11","5","2020-12-22","晴");
//                insterS(db,38,"上海","12","6","2020-12-23","阴");
//                insterS(db,39,"上海","11","4","2020-12-24","多云");
//                insterS(db,40,"上海","11","5","2020-12-25","多云");
//                insterS(db,41,"上海","13","9","2020-12-26","多云");
//                insterS(db,42,"上海","13","5","2020-12-27","晴");
//                insterS(db,43,"上海","11","6","2020-12-28","多云");
//                insterS(db,44,"上海","6","-1","2020-12-29","小雨");
//                insterS(db,45,"上海","0","-5","2020-12-30","阴");
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
        provinceList = new ArrayList<Province>();//省份集合
        cityList = new ArrayList<City>();//具体省份的城市集合
        new Thread() {
            @Override
            public void run() {
                try {
                    //weatherCountry = "http://guolin.tech/api/china/"
                    URL url = new URL(weatherCountry);
                    //开启一个url的连接，用HttpURLConnection连接方式处理
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");//设置连接对象的请求数据的方式
                    connection.setConnectTimeout(3000);//设置连接对象的请求超时的时间

                    //将请求返回的数据流转换成字节输入流对象
                    InputStream is = connection.getInputStream();
                    //将字节输入流对象转换成字符输入流对象
                    InputStreamReader isr = new InputStreamReader(is);
                    //创建字符输入缓冲流对象
                    BufferedReader br = new BufferedReader(isr);
                    //Toast.makeText(getActivity(),"kaishi",Toast.LENGTH_SHORT).show();
                    StringBuffer sb = new StringBuffer();
                    String string;

                    //读文本
                    while ((string = br.readLine()) != null) {
                        sb.append(string);
                    }

                    String result = sb.toString();
                    Log.d("MainActivity", "" + result);
                    JSONArray provinceArray = new JSONArray(result);
                    for (int i = 0; i < provinceArray.length(); i++) {
                        JSONObject provinceInfo = provinceArray.getJSONObject(i);//获取每个省份信息
                        Province provinceBean = new Province();//创建省份实体类对象
                        Gson gson = new Gson();//创建Gson解析对象

                        //反序例化，将json数据转化为实体类对象的成员变量值
                        provinceBean = gson.fromJson(provinceInfo.toString(), Province.class);
                        //添加保存好的省份对象数据进入省份集合
                        provinceList.add(provinceBean);
                    }

                    for (Province pro : provinceList) {
                        //如果该省份为用户输入的省份
                        if (pro.getName().equals(province)) {
                            //则拼接链接
                            //如：北京 weatherProvince = "http://guolin.tech/api/china/1/"
                            weatherProvince = weatherCountry + pro.getId() + "/";
                        }
                    }

                    Log.d("WeatherProvince", "" + weatherProvince+province);
                    //如：北京 weatherProvince = "http://guolin.tech/api/china/1/"
                    url = new URL(weatherProvince);
                    connection = (HttpURLConnection) url.openConnection();//开启一个url的连接用HttpURLConnection连接方式处理
                    connection.setRequestMethod("GET");//设置连接对象的请求数据的方式
                    connection.setConnectTimeout(3000);//设置连接对象的请求超时的时间

                    //将请求返回的数据流转换成字节输入流对象
                    is = connection.getInputStream();
                    //将字节输入流对象转换成字符输入流对象
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    StringBuffer sb2 = new StringBuffer();
                    //读文本
                    while ((string = br.readLine()) != null) {
                        sb2.append(string);
                    }

                    String result2 = sb2.toString();
                    Log.d("MainActivity2", "" + result2);

                    JSONArray cityArray = new JSONArray(result2);
                    for (int i = 0; i < cityArray.length(); i++) {
                        JSONObject cityInfo = cityArray.getJSONObject(i);//获取具体省份城市信息
                        City cityBean = new City();//创建城市实体类对象
                        Gson gson = new Gson();//创建Gson解析对象
                        //反序例化，将json数据转化为实体类对象的成员变量值
                        cityBean = gson.fromJson(cityInfo.toString(), City.class);
                        //添加保存好的城市对象数据进入城市集合
                        cityList.add(cityBean);
                    }
                    for (City c : cityList) {
                        //如果该城市为用户输入的城市
                        if (c.getName().equals(city)) {
                            //则拼接链接如：北京 weatherCity = "http://guolin.tech/api/china/1/1/"
                            weatherCity = weatherProvince + c.getId() + "/";
                        }
                    }

                    Log.d("WeatherCity", ""+weatherCity+city);

                    //如：北京 weatherCity = "http://guolin.tech/api/china/1/1/"
                    url = new URL(weatherCity);
                    connection = (HttpURLConnection) url.openConnection();//开启一个url的连接用HttpURLConnection连接方式处理
                    connection.setRequestMethod("GET");//设置连接对象的请求数据的方式
                    connection.setConnectTimeout(3000);//设置连接对象的请求超时的时间
                    //将请求返回的数据流转换成字节输入流对象
                    is = connection.getInputStream();
                    //将字节输入流对象转换成字符输入流对象
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    StringBuffer sb3 = new StringBuffer();
                    //读文本
                    while ((string = br.readLine()) != null) {
                        sb3.append(string);
                    }
                    String result3 = sb3.toString();
                    Log.d("MainActivity3", "" + result3);
                    JSONArray jsonArray = new JSONArray(result3);
                    JSONObject cityIdInfo = jsonArray.getJSONObject(0);
                    cityId=cityIdInfo.getString("weather_id");
                    //拼接字符串
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
         * 空气质量
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
                    //在此查看返回数据失败的原因
                    String status = airNowBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });

        /**
         * 接下去几天的天气的显示
         * 时间：2017-4-19  天气状况描述：  天气状况图标    最低温-最高温
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
                    dailyTemperature1.setText(basic.get(0).getTempMin() + "º  " + basic.get(0).getTempMax() + "º");
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
                    dailyTemperature2.setText(basic.get(1).getTempMin() + "º  " + basic.get(1).getTempMax() + "º");

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
                    dailyTemperature3.setText(basic.get(2).getTempMin() + "º  " + basic.get(2).getTempMax() + "º");

                }else {
                    //在此查看返回数据失败的原因
                    String status = weatherDailyBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });


        /**
         * 实况天气
         * 实况天气即为当前时间点的天气状况以及温湿风压等气象指数，具体包含的数据：体感温度、
         * 实测温度、天气状况、风力、风速、风向、相对湿度、大气压强、降水量、能见度等。
         *
         * @param context  上下文
         * @param location 地址详解
         * @param lang     多语言，默认为简体中文，海外城市默认为英文
         * @param unit     单位选择，公制（m）或英制（i），默认为公制单位
         * @param listener 网络访问回调接口
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
                    //此时返回数据

                    Basic basic=weatherNowBean.getBasic();
                    titleText.setText(city);

                    WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                    nowTemperatureTV.setText(now.getTemp() + "℃");
                    if(air[0]!=null){
                        nowDayWeatherQltyTV.setText(now.getText() + "|空气" + air[0]);
                    }
                    else {
                        nowDayWeatherQltyTV.setText(now.getText() + "|空气");
                    }

                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("EEEE");
                    String tempToday = format.format(date) + "  今天";
                    nowToady.setText(tempToday);
                    nowMinMaxTemperature.setText("体感温度："+now.getFeelsLike() + "º  ");

                    weatherSendibleTemperatureTv.setText("体感温度" + now.getFeelsLike() + "");
                    weatherHumitidyTv.setText("湿度" +now.getHumidity() + "%");
                    weatherVisibilityTv.setText("能见度" + now.getVis() + "千米");
                    weatherRiskLevelTv.setText(now.getWindDir()+now.getWindScale() + "级");
                    weatherPrecipitationTv.setText("降水量" + now.getPrecip() + "mm");
                    weatherPressureTv.setText("气压" + now.getPressure() + "百帕");
                } else {
                    //在此查看返回数据失败的原因
                    String status = weatherNowBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });

    }

}