package com.example.finalproject.ui.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.finalproject.R;
import com.google.android.material.tabs.TabLayout;

public class WeatherFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_weather, container, false);
        //获取控件
        TabLayout tab_main = root.findViewById(R.id.tab_main);
        ViewPager viewPager = root.findViewById(R.id.viewPager);
        //tab头
        tab_main.setTabMode(TabLayout.MODE_FIXED);
        tab_main.addTab(tab_main.newTab().setText("今日"));
        tab_main.addTab(tab_main.newTab().setText("推荐"));
        //绑定Adapater
        viewPager.setAdapter(new FragmentPagersAdapter(getChildFragmentManager()));
        tab_main.setupWithViewPager(viewPager);
        return root;
    }
    //Fragment
    public class FragmentPagersAdapter extends FragmentPagerAdapter{
        private String[] listTitle = {"今日","推荐"};


        public FragmentPagersAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment f;
            switch (position){
                case 0:
                    f=new TodayFragment();
                    return f;
                case 1:
                    f=new RecommendFragment();
                    return f;
            }
            return null;
        }

        @Override
        public int getCount() {
            return listTitle.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return listTitle[position];
        }
    }
}