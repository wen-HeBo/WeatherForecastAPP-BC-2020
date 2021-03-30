package com.example.finalproject.ui.history;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.finalproject.DataBase.AppDatabase;
import com.example.finalproject.DataBase.History;
import com.example.finalproject.DataBase.User;
import com.example.finalproject.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class HistoryFragment extends Fragment {
    private View root;
    private LineChartView lcv;
    String[] dates=new String[10];//X轴的标注
    int[] max_tem= new int[10];//图表的数据点
    int[] min_tem= new int[10];//图表的数据点
    private final List<PointValue> maxPointValues = new ArrayList<>();
    private final List<PointValue> minPointValues = new ArrayList<>();
    private final List<AxisValue> mAxisXValues = new ArrayList<>();

    private int DAY_NUMBER;//显示天数
    private String city;//默认城市

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_history, container, false);

        AppDatabase db = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, "database-name")
                .addMigrations()
                .allowMainThreadQueries()
                .build();
            User user=db.userDao().findByName(getActivity().getIntent().getStringExtra("user_account"));
            DAY_NUMBER=Integer.parseInt(user.getUser_show_day());
            city=user.getUser_city();
            //从数据库中获取信息
            formdb(db);

        return root;
    }

    //从数据库中获取信息
    private void formdb(AppDatabase db){
        Date now_date=new Date();
        Calendar calendar=Calendar.getInstance();//得到日历
        calendar.setTime(now_date);//把现在的事件赋给日历
        calendar.add(Calendar.DAY_OF_MONTH,-DAY_NUMBER-1);

        for (int i =0;i<DAY_NUMBER;++i){
            calendar.add(Calendar.DAY_OF_MONTH,+1);
            Date get_date=calendar.getTime();//从日历获取时间
            //格式化
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String date = formatter.format(get_date);

            dates[i]=date;
            //Log.i("HISTORY",dates[i] );
        }

        List<History_weather_info> list_hwi=new ArrayList<History_weather_info>();
        //从数据库中搜索数据
        List<History> his=db.historyDao().selectByDate(dates,city);
        for (int i =0;i<his.size();++i){
            max_tem[i]=Integer.parseInt(his.get(i).getMax_tem());
            min_tem[i]=Integer.parseInt(his.get(i).getMin_tem());
            History_weather_info hwi=new History_weather_info();
            hwi.date=his.get(i).getWeather_date();
            hwi.info=his.get(i).getWeather_info();
            list_hwi.add(hwi);
                //Log.i("HISTORY", "ss"+hwi.date);
        }
       // Log.i("HISTORY", "ss");



            getAxisXLables();//获取x轴的标注
            getAxisPoints();//获取坐标点

            Message message = new Message();
            message.what = 1;
            message.obj=list_hwi;
            handler.sendMessage(message);
    }

    //设置x轴的显示
    private void getAxisXLables() {
        for (int i = 0; i < DAY_NUMBER; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(dates[i]));
        }
    }
    //每个点的显示
    private void getAxisPoints() {
        for (int i = 0; i < DAY_NUMBER; i++) {
            maxPointValues.add(new PointValue(i, max_tem[i]));
            minPointValues.add(new PointValue(i, min_tem[i]));
        }
    }
    //设置曲线样式
    private void initLineChart() {
        List<Line> lines = new ArrayList<Line>();
        Line max_line = new Line(maxPointValues).setColor(R.color.Red);  //折线的颜色
        max_line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状,这里是圆形
        max_line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        max_line.setFilled(false);//是否填充曲线的面积
        max_line.setHasLabels(false);//曲线的数据坐标是否加上备注
        max_line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        max_line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        max_line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(max_line);
        Line min_line = new Line(minPointValues).setColor(R.color.Blue);
        min_line.setShape(ValueShape.CIRCLE);
        min_line.setCubic(false);
        min_line.setFilled(false);
        min_line.setHasLabels(false);
        min_line.setHasLabelsOnlyForSelected(true);
        min_line.setHasLines(true);
        min_line.setHasPoints(true);
        lines.add(min_line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(7); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextColor(Color.GRAY);  //设置字体颜色
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY); //Y轴设置在左边



        //设置行为属性，支持缩放、滑动以及平移
        lcv.setInteractive(true);
        lcv.setZoomType(ZoomType.HORIZONTAL);
        lcv.setMaxZoom((float) 2);
        lcv.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lcv.setLineChartData(data);
        lcv.setVisibility(View.VISIBLE);
    }

    //更新UI
    @SuppressLint("HandlerLeak")
    private final Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    lcv=root.findViewById(R.id.lcv);
                    initLineChart();//初始化图表
                    List<History_weather_info> list_hwi = (List<History_weather_info>) msg.obj;

                    String[] data=new String[DAY_NUMBER];
                    for (int i=0;i<list_hwi.size();++i){
                        if (list_hwi.get(i).info.length()==1){
                            data[i]="日期："+list_hwi.get(i).date+"       天气:  "+list_hwi.get(i).info;
                        }
                        else {
                            Log.i("HISTORY", String.valueOf(list_hwi.get(i).info.length()));
                            data[i]="日期："+list_hwi.get(i).date+"       天气:"+list_hwi.get(i).info;
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(root.getContext(), R.layout.list_item1, data);
                    ListView lv =root.findViewById(R.id.lv);
                    lv.setAdapter(adapter);
                    setListViewHeightBasedOnChildren(lv);

                    //显示城市名
                    TextView city_tv=root.findViewById(R.id.city);
                    city_tv.setText(city);
            }
        }
    };

    public void setListViewHeightBasedOnChildren (ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


}
