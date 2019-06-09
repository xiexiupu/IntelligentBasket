package com.automation.zzx.intelligent_basket_demo.activity.common;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.automation.zzx.intelligent_basket_demo.R;
import com.automation.zzx.intelligent_basket_demo.adapter.rentAdmin.MgBasketContentFragmentAdapter;
import com.automation.zzx.intelligent_basket_demo.entity.UserInfo;
import com.automation.zzx.intelligent_basket_demo.fragment.BasketRepairEndFragment;
import com.automation.zzx.intelligent_basket_demo.fragment.BasketRepairFragment;
import com.automation.zzx.intelligent_basket_demo.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class RepairInfoListActivity extends AppCompatActivity {

    public final static String PROJECT_ID = "projectId";  // 上传图片的项目Id

    //控件及布局
    private TabLayout mTabLayout; // 顶部导航栏
    private NoScrollViewPager mViewPager; // 页面布局
    // 用户登录信息相关
    private UserInfo mUserInfo;
    private String mToken;
    private String mProjectId;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_rent_mg_basket);

        // 绑定控件
        mTabLayout = (TabLayout) findViewById(R.id.head_tab_layout);
        mViewPager = (NoScrollViewPager) findViewById(R.id.view_pager);

        List<Fragment> fragmentList = new ArrayList<>();   // 添加fragment
        fragmentList.add(new BasketRepairFragment());
        fragmentList.add(new BasketRepairEndFragment());

        List<String> titleList = new ArrayList<>();  // 添加fragment说明
        titleList.add("正在保修");
        titleList.add("历史记录");

        getUserInfo();

        MgBasketContentFragmentAdapter mgBasketContentFragmentAdapter =
                new MgBasketContentFragmentAdapter(getSupportFragmentManager(), fragmentList, titleList);
        mViewPager.setAdapter(mgBasketContentFragmentAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    // 获取用户数据
    private void getUserInfo(){
        // 从本地获取数据
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mUserInfo = new UserInfo();
        mUserInfo.setUserId(mPref.getString("userId", ""));
        mUserInfo.setUserPhone(mPref.getString("userPhone", ""));
        mUserInfo.setUserRole(mPref.getString("userRole", ""));
        mToken = mPref.getString("loginToken","");
        //获取当前项目ID
        Intent intent = getIntent();
        mProjectId = intent.getStringExtra(PROJECT_ID);
    }

    // 将用户信息传递给子Fragment
    public UserInfo pushUserInfo(){
        return mUserInfo;
    }
    // 将用户token传递给子Fragment
    public String pushToken(){
        return mToken;
    }
    // 将ProjectId传递给子Fragment
    public String pushProjectId(){
        return mProjectId;
    }
}