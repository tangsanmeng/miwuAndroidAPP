package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyCommdityActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ll_myCommodity;
    private MyApplication app;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_commdity);

        initView();
        showMyCommodityTest();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_return) {
            finish();
        }else if (view.getId() == R.id.tv_shuaxin) {
            ll_myCommodity.removeAllViews();
            showMyCommodityTest();
        }
    }

    private void initView() {
        getSupportActionBar().hide();
        LoginExitActivity.addActivity(this);
        findViewById(R.id.tv_return).setOnClickListener(this);
        findViewById(R.id.tv_shuaxin).setOnClickListener(this);

        ll_myCommodity = findViewById(R.id.ll_myCommodity);
    }


    private void showMyCommodityTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //okhttp 客户端
                OkHttpClient client = new OkHttpClient();
                app = MyApplication.getInstance();
                username = app.infoMap.get("username");
                //请求体
                FormBody body = new FormBody.Builder()
                        .add("username", username)
                        .build();
                final Request request = new Request.Builder()
                        .url("https://600274a7f0.zicp.fun/login/Mycommodity/")
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    //请求处理
                    String rd = response.body().string();
                    JSONObject jsonObject = new JSONObject(rd);
                    String myCommodityList =  jsonObject.get("list").toString();
                    JSONArray jsonArray = new JSONArray(myCommodityList);


                    if (jsonObject.getString("code").equals("110")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i=0; i< jsonArray.length(); i++) {
                                    try {
                                        JSONObject object = jsonArray.getJSONObject(i);

                                        int screenWidth = getResources().getDisplayMetrics().widthPixels;
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, 200);

                                        View view = LayoutInflater.from(MyCommdityActivity.this).inflate(R.layout.mycommoditystyle, null);

                                        ImageView imageView = view.findViewById(R.id.iv_myCommodityPicture);
                                        TextView textView_info = view.findViewById(R.id.tv_commodityInfo);
                                        TextView textView_from = view.findViewById(R.id.tv_commodityFrom);
                                        TextView textView_price = view.findViewById(R.id.tv_commodityPrice);
                                        TextView textView_id = view.findViewById(R.id.tv_delete_id);

                                        String url = object.getString("picture");
                                        if (url.startsWith("http:")) {
                                            url = url.replace("http:", "https:");
                                        }

                                        Glide.with(MyCommdityActivity.this).load(url).into(imageView);
                                        textView_info.setText(object.getString("commodityIntroduction"));
                                        textView_from.setText(object.getString("commodityfrom"));
                                        textView_price.setText(object.getString("price"));
                                        textView_id.setText(object.getString("id"));

                                        view.setOnLongClickListener(view1 -> {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MyCommdityActivity.this);
                                            builder.setMessage("是否删除此物品?");
                                            builder.setPositiveButton("确定", (dialog, which) ->{
                                                ll_myCommodity.removeView(view1);
                                                TextView textView = view1.findViewById(R.id.tv_delete_id);
                                                String objectId = textView.getText().toString();
                                                LeanCloud.deleteCommodity(objectId);
                                            });
                                            builder.setNegativeButton("取消", null);
                                            builder.create().show();
                                            return true;
                                        });

                                        ll_myCommodity.addView(view, params);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }else if (jsonObject.getString("code").equals("111")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(MyCommdityActivity.this,"异常: "+jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MyCommdityActivity.this,"登录成功!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

//    private void showMyCommodity() {
//        // 获取设备屏幕的宽
//        int screenWidth = getResources().getDisplayMetrics().widthPixels;
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, 200);
//
//        app = MyApplication.getInstance();
//        String objectId = app.infoMap.get("objectId");
//        String yonghuming = app.infoMap.get("yonghuming");
//
//        LCQuery<LCObject> query = new LCQuery<>("commodityinfo");
//        query.whereEqualTo("commodityfrom", objectId);
//        query.findInBackground().subscribe(new Observer<List<LCObject>>() {
//            public void onSubscribe(Disposable disposable) {}
//            public void onNext(List<LCObject> commodity) {
//
//                for (int i=0; i<commodity.size(); i++) {
//                    View view = LayoutInflater.from(MyCommdityActivity.this).inflate(R.layout.mycommoditystyle, null);
//
//                    ImageView imageView = view.findViewById(R.id.iv_myCommodityPicture);
//                    TextView textView_info = view.findViewById(R.id.tv_commodityInfo);
//                    TextView textView_from = view.findViewById(R.id.tv_commodityFrom);
//                    TextView textView_price = view.findViewById(R.id.tv_commodityPrice);
//                    TextView textView_id = view.findViewById(R.id.tv_delete_id);
//
//                    String url = commodity.get(i).getLCFile("picture").getUrl();
//                    if (url.startsWith("http:")) {
//                        url = url.replace("http:", "https:");
//                    }
//
//                    Glide.with(MyCommdityActivity.this).load(url).into(imageView);
//                    textView_info.setText((String) commodity.get(i).get("commodityIntroduction"));
//                    textView_from.setText(yonghuming);
//                    textView_price.setText((String) commodity.get(i).get("price") + " ¥");
//                    textView_id.setText(commodity.get(i).getObjectId());
//
//                    view.setOnLongClickListener(view1 -> {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MyCommdityActivity.this);
//                        builder.setMessage("是否删除此物品?");
//                        builder.setPositiveButton("确定", (dialog, which) ->{
//                            ll_myCommodity.removeView(view1);
//                            TextView textView = view1.findViewById(R.id.tv_delete_id);
//                            String objectId = textView.getText().toString();
//                            LeanCloud.deleteCommodity(objectId);
//                        });
//                        builder.setNegativeButton("取消", null);
//                        builder.create().show();
//                        return true;
//                    });
//
//                    System.out.println("即将展示的内容数组为 ============= "+commodity);
//                    System.out.println("即将展示的内容为商品简介 ============= "+commodity.get(i).get("commodityIntroduction"));
//                    System.out.println("即将展示的内容为商品价格 ============= "+commodity.get(i).get("price"));
//                    System.out.println("即将展示的内容为卖家 ============= "+commodity.get(i).get("commodityfrom"));
//                    System.out.println("即将展示的内容数为价格 ============= "+commodity.get(i).get("picture"));
//                    System.out.println("即将展示的内容为网络图片url ============= "+commodity.get(i).getLCFile("picture").getUrl());
//                    System.out.println("即将展示的内容为商品ID ============= "+commodity.get(i).getObjectId());
//
//                    ll_myCommodity.addView(view, params);
//                }
//            }
//            public void onError(Throwable throwable) {}
//            public void onComplete() {}
//        });
//    }
}