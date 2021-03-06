package com.automation.zzx.intelligent_basket_demo.fragment.basket;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.automation.zzx.intelligent_basket_demo.R;
import com.automation.zzx.intelligent_basket_demo.activity.common.RepairDetailActivity;
import com.automation.zzx.intelligent_basket_demo.activity.common.RepairInfoListActivity;
import com.automation.zzx.intelligent_basket_demo.activity.loginRegist.LoginActivity;
import com.automation.zzx.intelligent_basket_demo.adapter.basket.BasketRepairEndAdapter;
import com.automation.zzx.intelligent_basket_demo.entity.AppConfig;
import com.automation.zzx.intelligent_basket_demo.entity.RepairInfo;
import com.automation.zzx.intelligent_basket_demo.entity.UserInfo;
import com.automation.zzx.intelligent_basket_demo.fragment.proAdmin.ProAdminMessageFragment;
import com.automation.zzx.intelligent_basket_demo.utils.ToastUtil;
import com.automation.zzx.intelligent_basket_demo.utils.okhttp.BaseCallBack;
import com.automation.zzx.intelligent_basket_demo.utils.okhttp.BaseOkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;

import static android.content.ContentValues.TAG;

public class BasketRepairEndFragment extends Fragment {

    // intent 消息参数
    public final static String PROJECT_ID = "projectId";  // 上传图片的项目Id
    public final static String UPLOAD_IMAGE_TYPE  = "uploadImageType";
    // Handler消息
    //private final static int MG_REPAIR_LIST_INFO = 1;  // 获取报修列表信息(未结束）视图更新显示
    private final static int MG_REPAIR_END_LIST_INFO = 2; // 获取报修列表信息(已结束）视图更新显示
    private final static int FAIL_REPAIR_LIST_INFO = 3; // 获取报修列表信息失败

    // 本地存储
    public SharedPreferences pref;
    private UserInfo userInfo; // 个人信息
    private String token; //
    private String projectId;
    private String projectName;

    //主体
    private RecyclerView rvRepairInfo; // 报修列表recycleView
    private List<RepairInfo> mRepairInfoList; //报修信息列表
    private BasketRepairEndAdapter mBasketRepairAdapter;
    // 空空如也
    private RelativeLayout noRepairListRelativeLayout;
    private TextView noRepairListTextView;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MG_REPAIR_END_LIST_INFO: // 获取报修列表信息(未结束）
                    mRepairInfoList.clear();
                    mRepairInfoList.addAll(parseRepairListInfo((String) msg.obj));
                    mBasketRepairAdapter.notifyDataSetChanged();
                    updateContentView();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repair_basket_list, container, false);

        rvRepairInfo = (RecyclerView) view.findViewById(R.id.basket_recycler_view);
        mRepairInfoList = new ArrayList<>();
        rentAdminGetRepairInfoListInfo();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvRepairInfo.setLayoutManager(layoutManager);
        mBasketRepairAdapter = new BasketRepairEndAdapter(getContext(), mRepairInfoList);
        rvRepairInfo.setAdapter(mBasketRepairAdapter);
        mBasketRepairAdapter.setOnItemClickListener(new BasketRepairEndAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // item 点击响应
                Log.i(TAG, "You have clicked the "+ position +" item");
                //TODO 报修信息传递
                Intent intent = new Intent(getActivity(), RepairDetailActivity.class);
                intent.putExtra(ProAdminMessageFragment.PROJECT_ID_MSG, projectId);
                intent.putExtra(ProAdminMessageFragment.PROJECT_NAME_MSG, projectName);
                intent.putExtra(ProAdminMessageFragment.BASKET_ID_MSG, mRepairInfoList.get(position).getDeviceId());
                intent.putExtra(ProAdminMessageFragment.REPAIR_DATE_MSG,
                        mRepairInfoList.get(position).getStartTime().substring(0,16));
                startActivity(intent);
            }
        });
        // 空空如也
        noRepairListRelativeLayout = (RelativeLayout) view.findViewById(R.id.basket_no_avaliable);
        noRepairListTextView = (TextView) view.findViewById(R.id.no_basket_hint);

        return view;
    }

    /*
     * 网络请求相关
     */
    // 从后台获取吊篮列表数据
    public void rentAdminGetRepairInfoListInfo(){
        BaseOkHttpClient.newBuilder()
                .addHeader("Authorization", token)
                .addParam("projectId", projectId)
                .get()
                .url(AppConfig.AREA_ADMIN_GET_REPAIR_END_INFO)
                .build()
                .enqueue(new BaseCallBack() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.i(TAG, "成功" );
                        String responseData = o.toString();
                        Message message = new Message();
                        message.what = MG_REPAIR_END_LIST_INFO;
                        message.obj = responseData;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onError(int code) {
                        Log.i(TAG, "错误：" + code);
                        switch (code){
                            case 401: // 未授权
                                ToastUtil.showToastTips(getActivity(), "登录已过期，请重新登陆");
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                                break;
                            case 403: // 禁止
                                break;
                            case 404: // 404
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "失败：" + e.toString());
                    }
                });
    }
    // 解析吊篮列表数据
    private List<RepairInfo> parseRepairListInfo(String responseDate){
        List<RepairInfo> mgRepairInfos = new ArrayList<>();

        JSONObject jsonObject = JSON.parseObject(responseDate);

        if(projectId == null || projectId.equals("")) { // 解析获取项目Id
            projectId = ((RepairInfoListActivity) getActivity()).pushProjectId();
        }

        String repairInfo =jsonObject.getString("repairEndInfo");
        JSONArray jsonArray = JSON.parseArray(repairInfo);
        if(jsonArray.size() >= 1) {
            Iterator<Object> iterator = jsonArray.iterator();  // 迭代获取项目信息
            while (iterator.hasNext()) {
                JSONObject repairObj = (JSONObject) iterator.next();
                //时间字符串处理
                String timeStartDate = repairObj.getString("startTimeS").substring(0, 10);
                String timeStartHM = repairObj.getString("startTimeS").substring(11, 16);
                String timeStart = timeStartDate + " " + timeStartHM;
                String timeEndDate = repairObj.getString("endTimeS").substring(0, 10);
                String timeEndHM = repairObj.getString("endTimeS").substring(11, 16);
                String timeEnd = timeEndDate + " " + timeEndHM;

                RepairInfo mRepairInfo = new RepairInfo(repairObj.getString("deviceId"), repairObj.getString("projectId"),
                        repairObj.getString("managerId"), repairObj.getString("dealerId"), repairObj.getString("reason"),
                        repairObj.getString("imageStart"), timeStart, repairObj.getString("imageEnd"),
                        repairObj.getString("description"), timeEnd);
                mgRepairInfos.add(mRepairInfo);
            }
        } else {
            mgRepairInfos.clear();
        }
        return mgRepairInfos;
    }
    /*
     * UI 更新相关
     */
    private void updateContentView(){
        if(projectId == null || projectId.equals("")) {
            rvRepairInfo.setVisibility(View.GONE);
            noRepairListRelativeLayout.setVisibility(View.VISIBLE);
            noRepairListTextView.setText("您还没有相关的项目");
        }else {
            if (mRepairInfoList.size() < 1) { // 暂无吊篮
                rvRepairInfo.setVisibility(View.GONE);
                noRepairListRelativeLayout.setVisibility(View.VISIBLE);
                noRepairListTextView.setText("暂无报修记录！");
            } else {  // 好多吊篮
                noRepairListRelativeLayout.setVisibility(View.GONE);
                rvRepairInfo.setVisibility(View.VISIBLE);
            }
        }
    }

    /*
     *  生命周期函数
     */
    /*
     * 登录相关
     */
    protected void onAttachToContext(Context context) {
        //do something
        userInfo = ((RepairInfoListActivity) context).pushUserInfo();
        token = ((RepairInfoListActivity) context).pushToken();
        projectId = ((RepairInfoListActivity) context).pushProjectId();
        projectName = ((RepairInfoListActivity) context).pushProjectName();
    }
    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

}
