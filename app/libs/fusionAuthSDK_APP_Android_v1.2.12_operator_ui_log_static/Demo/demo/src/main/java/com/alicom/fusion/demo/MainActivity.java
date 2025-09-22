package com.alicom.fusion.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alicom.fusion.auth.demo.R;
import com.alicom.fusion.demo.fragment.ActivitiesFragment;
import com.alicom.fusion.demo.fragment.HomePageFragment;
import com.alicom.fusion.demo.fragment.OrdersFragment;
import com.alicom.fusion.demo.fragment.PersonalCenterFragment;
import com.alicom.fusion.demo.net.HttpRequestUtil;
import com.alicom.fusion.demo.utils.PermissionUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((getIntent().getFlags()& Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)!=0){
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        MyApplication.getInstance().addActivity(this);
        if (Build.VERSION.SDK_INT >= 23) {
            PermissionUtils.checkAndRequestPermissions(this, 10001, Manifest.permission.INTERNET,
                    Manifest.permission.READ_PHONE_STATE);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }
        ViewPager viewPager = findViewById(R.id.viewpager);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bn_view);
        List<Fragment> list = new ArrayList<>();
        // 随便加了三个有内容的Fragment
        list.add(new HomePageFragment());
        list.add(new ActivitiesFragment());
        list.add(new OrdersFragment());
        list.add(new PersonalCenterFragment());
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.item_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.item_activities);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.item_orders);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.item_personal);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.item_activities:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.item_orders:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.item_personal:
                        viewPager.setCurrentItem(3);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }



}