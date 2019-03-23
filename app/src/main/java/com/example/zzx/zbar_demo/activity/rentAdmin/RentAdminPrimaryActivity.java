package com.example.zzx.zbar_demo.activity.rentAdmin;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.zzx.zbar_demo.R;
import com.example.zzx.zbar_demo.fragment.rentAdmin.ManageBasketFragment;
import com.example.zzx.zbar_demo.fragment.rentAdmin.ManageWorkerFragment;
import com.example.zzx.zbar_demo.fragment.rentAdmin.RentAdminFragment;
import com.hjm.bottomtabbar.BottomTabBar;

public class RentAdminPrimaryActivity extends AppCompatActivity {

    // 控件
    private BottomTabBar mBottomTabBar;

    // 屏幕素质
    private int mScreenWidth;
    private int mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_admin_primary);

        initWidget();
    }

    private void initWidget(){

        mBottomTabBar = (BottomTabBar) findViewById(R.id.bottom_tab_bar);

        getScreenSize();
        mBottomTabBar.init(getSupportFragmentManager(), mScreenWidth, mScreenHeight)
                .setImgSize(90, 90)
                .setFontSize(30)
                //.setTabPadding(5,0,5)
                .setChangeColor(Color.parseColor("#009688"),Color.parseColor("#cccccc"))
                .addTabItem("吊篮", R.mipmap.ic_navi_basket, ManageBasketFragment.class)
                .addTabItem("工人", R.mipmap.ic_navi_worker, ManageWorkerFragment.class)
                .addTabItem("我", R.mipmap.ic_navi_me,RentAdminFragment.class)
                .isShowDivider(false)
                //.setDividerColor(Color.parseColor("#FF0000"))
                //.setTabBarBackgroundColor(Color.parseColor("#00FF0000"))
                .setOnTabChangeListener(new BottomTabBar.OnTabChangeListener() {
                    @Override
                    public void onTabChange(int position, String name, View view) {
                        if(position == 2)
                            mBottomTabBar.setSpot(2, false);
                    }
                })
                .setSpot(2, true);

    }

    /*
     * 获取屏幕素质
     */
    // 获取屏幕的宽高度
    private void getScreenSize(){
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        mScreenHeight = dm2.heightPixels;
        mScreenWidth = dm2.widthPixels;
    }
}