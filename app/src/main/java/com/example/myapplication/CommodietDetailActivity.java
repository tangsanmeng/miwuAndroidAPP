package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommodietDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView detail_yonghuming;
    private TextView detail_address;
    private TextView detail_price;
    private ImageView detail_picture;
    private TextView detail_commodity_info;
    private TextView detail_create_time;
    private LinearLayout ll_pinglun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_detail2);
        initView();
        init();
    }

    private void initView() {
        getSupportActionBar().hide();
        LoginExitActivity.addActivity(this);

        detail_yonghuming = findViewById(R.id.detail_yonghuming);
        detail_address = findViewById(R.id.detail_address);
        detail_commodity_info = findViewById(R.id.detail_commodity_info);
        detail_picture = findViewById(R.id.detail_picture);
        detail_create_time = findViewById(R.id.detail_create_time);
        detail_price = findViewById(R.id.detail_price);
        ll_pinglun = findViewById(R.id.ll_pinglun);
        findViewById(R.id.comment_submit).setOnClickListener(this);
        findViewById(R.id.tv_return).setOnClickListener(this);
        findViewById(R.id.shuaxin_detail).setOnClickListener(this);
    }

    private void init() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                String objectId = intent.getStringExtra("objectId");
                //okhttp 客户端
                OkHttpClient client = new OkHttpClient();
                //请求体
                FormBody body = new FormBody.Builder()
                        .add("objectId", objectId)
                        .build();
                final Request request = new Request.Builder()
                        .url("https://600274a7f0.zicp.fun/login/CommodityDetail/")
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    //请求处理
                    String rd = response.body().string();
                    JSONObject jsonObject = new JSONObject(rd);
                    String commentList = jsonObject.get("list").toString();
                    JSONArray jsonArray = new JSONArray(commentList);
                    if (jsonObject.getString("code").equals("114")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String url = null;
                                try {
                                    url = jsonObject.getString("picture");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (url.startsWith("http:")) {
                                    url = url.replace("http:", "https:");
                                }
                                try {
                                    detail_yonghuming.setText(jsonObject.getString("commodityfrom"));
                                    detail_address.setText(jsonObject.getString("maijia_address"));
                                    detail_commodity_info.setText(jsonObject.getString("info"));
                                    detail_create_time.setText(jsonObject.getString("createAt"));
                                    detail_price.setText(jsonObject.getString("price")+" ¥");
                                    Glide.with(CommodietDetailActivity.this).load(url).into(detail_picture);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                comment(jsonArray);
                            }
                        });
                    }else if (jsonObject.getString("code").equals("115")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(CommodietDetailActivity.this,"异常: "+jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
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
                            System.out.println("异常: "+e.getMessage());
                            Toast.makeText(CommodietDetailActivity.this,"异常: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_return) {
            finish();
        } else if (view.getId() == R.id.shuaxin_detail) {
            ll_pinglun.removeAllViews();
            init();
        }
    }

    private void comment(JSONArray jsonArray) {
        for (int i=0; i< jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, 200);

                View view = LayoutInflater.from(CommodietDetailActivity.this).inflate(R.layout.commentstyle, null);
                TextView commentFrom = view.findViewById(R.id.commentFrom);
                TextView commodityAt = view.findViewById(R.id.commodityAt);
                TextView commentDetail = view.findViewById(R.id.commentDetail);
                commentFrom.setText(object.getString("comment_from"));
                commodityAt.setText(object.getString("comment_at"));
                commentDetail.setText(object.getString("comment_detail"));
                ll_pinglun.addView(view, params);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}