package com.example.zzx.zbar_demo.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.zzx.zbar_demo.activity.BasketListActivity;
import com.example.zzx.zbar_demo.activity.LoginActivity;
import com.example.zzx.zbar_demo.activity.ProDetailActivity;
import com.example.zzx.zbar_demo.R;
import com.example.zzx.zbar_demo.Util.HttpUtil;
import com.example.zzx.zbar_demo.activity.WorkerListActivity;
import com.example.zzx.zbar_demo.entity.UserInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

// Created by $USER_NAME on 2018/12/3/003.
@SuppressLint("ValidFragment")
public class UserFragment extends Fragment {

    private View mView;
    private UserInfo userInfo;
    private String token;
    public SharedPreferences pref;

    private TextView txtUserName;
    private TextView txtRoleName;
    private TextView txtProName;
    private LinearLayout llSelfSettle;
    private LinearLayout llBasketManage;
    private LinearLayout llWorkManage;
    private LinearLayout llChangePro;
    private LinearLayout llProDetail;
    private LinearLayout LLLogout;

    private Intent intent;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    txtUserName.setText(userInfo.getUserName());
                    txtRoleName.setText(userInfo.getUserRole());
                    break;
                }
                default: {
                    txtUserName.setText("null");
                    txtRoleName.setText("null");
                    break;
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.user_fragment, container, false);

            txtUserName =  mView.findViewById(R.id.txt_user_name);
            txtRoleName =  mView.findViewById(R.id.txt_role_name);
            txtProName =  mView.findViewById(R.id.txt_pro_number);
            llProDetail = mView.findViewById(R.id.ll_pro_detail);
            llSelfSettle = mView.findViewById(R.id.ll_self_settle);
            llBasketManage = mView.findViewById(R.id.ll_basket_manage);
            llWorkManage = mView.findViewById(R.id.ll_worker_manage);
            llChangePro = mView.findViewById(R.id.ll_change_pro);
            LLLogout = mView.findViewById(R.id.ll_logout);

            //获取当前角色名并显示
            pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            token = pref.getString("loginToken","");
            if(token == null){
                userInfo = new UserInfo(null,null);
                userInfo.setUserRole("游客");
                userInfo.setUserName("游客");
                txtUserName.setText(userInfo.getUserName());
                txtRoleName.setText(userInfo.getUserRole());
            } else {
                userInfo = new UserInfo(null,null);
                getUserInfoHttp();
            }


            //项目详情界面跳转
            llProDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(getActivity(),ProDetailActivity.class);
                    startActivity(intent);
                }
            });

            //个人设置界面跳转
            llSelfSettle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            //吊篮管理界面跳转
            llBasketManage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(getActivity(),BasketListActivity.class);
                    startActivity(intent);
                }
            });

            //施工人员管理界面跳转
            llWorkManage.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    intent = new Intent(getActivity(),WorkerListActivity.class);
                    startActivity(intent);
                }
            });
            //切换项目
            llChangePro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            LLLogout.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    LogoutHttp();
                }
            });
        }
        return mView;
    }

    //登录请求连接
    private void getUserInfoHttp(){
        HttpUtil.getUserInfoOkHttpRequest(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //异常情况处理
                Looper.prepare();
                Toast.makeText(getActivity(), "网络连接失败！", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 返回服务器数据
                String responseData = response.body().string();
                try {
                    JSONObject jsonObject = JSON.parseObject(responseData);
                   /* String isAllowed = jsonObject.getString("isAllowed");*/
                    Message message = new Message();
                    /*switch (isAllowed) {
                        case "true":*/
                            message.what = 0;
                            userInfo.setUserRole(jsonObject.getString("userRole"));
                            userInfo.setUserName(jsonObject.getString("userName"));
                           /* break;
                        default:
                            message.what = 1;
                            userInfo.setUserRole(null);
                            userInfo.setUserName(null);
                            //StartWorkerMainActicity("wrong");
                            break;
                    }*/
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },token);
    }

    //退出登录连接
    private void LogoutHttp(){
       /* HttpUtil.quitLoadOkHttpRequest(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //异常情况处理
                Looper.prepare();
                Toast.makeText(getActivity(), "网络连接失败！", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 返回服务器数据
                String responseData = response.body().string();
                try {
                    JSONObject jsonObject = JSON.parseObject(responseData);
                    String isAllowed = jsonObject.getString("isAllowed");
                    switch (isAllowed) {
                        case "true":
                            //删除token，并跳转至登录界面
                            SharedPreferences.Editor editor = pref.edit();
                            editor.remove("loginToken");
                            editor.commit();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },userInfo.getUserId());*/
    }

}