package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
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
import java.util.Date;
import java.util.List;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommodityListActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ll_Commodity;

    private String yonghuming = null;
    private String price = null;
    private String info = null;
    private String maijia_address = null;
    private String url = null;
    private String commodityId = null;
    private Date create_time = null;

    private String LEANCLOUD_SEARCH_CODE = "114514";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_list2);
        initView();
        showMyCommodityTest();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.shuaxin) {
            ll_Commodity.removeAllViews();
            showMyCommodityTest();
        }
    }

    private void initView() {
        getSupportActionBar().hide();
        LoginExitActivity.addActivity(this);
        findViewById(R.id.shuaxin).setOnClickListener(this);

        ll_Commodity = findViewById(R.id.ll_CommodityList);
    }

    private void showMyCommodityTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //okhttp 客户端
                OkHttpClient client = new OkHttpClient();
                //请求体
                FormBody body = new FormBody.Builder()
                        .build();
                final Request request = new Request.Builder()
                        .url("https://600274a7f0.zicp.fun/login/CommodityList/")
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    //请求处理
                    String rd = response.body().string();
                    JSONObject jsonObject = new JSONObject(rd);

                    String myCommodityList =  jsonObject.get("list").toString();
                    JSONArray jsonArray = new JSONArray(myCommodityList);

                    if (jsonObject.getString("code").equals("112")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i=0; i< jsonArray.length(); i++) {
                                    try {
                                        JSONObject object = jsonArray.getJSONObject(i);

                                        int screenWidth = getResources().getDisplayMetrics().widthPixels;
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, 200);

                                        View view = LayoutInflater.from(CommodityListActivity.this).inflate(R.layout.commoditystyleforlist, null);

                                        ImageView imageView = view.findViewById(R.id.iv_CommodityPictureList);
                                        TextView textView_info = view.findViewById(R.id.tv_commodityInfoList);
                                        TextView textView_from = view.findViewById(R.id.tv_commodityFromList);
                                        TextView textView_price = view.findViewById(R.id.tv_commodityPriceList);
                                        TextView textView_id = view.findViewById(R.id.maijia_address);
                                        TextView textView_commodityId = view.findViewById(R.id.commodityId);

                                        String url = object.getString("picture");
                                        if (url.startsWith("http:")) {
                                            url = url.replace("http:", "https:");
                                        }

                                        Glide.with(CommodityListActivity.this).load(url).into(imageView);
                                        textView_from.setText(object.getString("commodityfrom"));
                                        textView_info.setText(object.getString("commodityIntroduction"));
                                        textView_price.setText(object.getString("price")+" ¥");
                                        textView_id.setText(object.getString("maijia_address"));
                                        textView_commodityId.setText(object.getString("id"));

                                        view.setOnClickListener(view1 -> {
                                            Intent intent = new Intent(CommodityListActivity.this, CommodietDetailActivity.class);
                                            TextView textView = view1.findViewById(R.id.commodityId);
                                            String objectId = textView.getText().toString().trim();
                                            intent.putExtra("objectId", objectId);
                                            startActivity(intent);
                                        });

                                        ll_Commodity.addView(view, params);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }else if (jsonObject.getString("code").equals("113")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(CommodityListActivity.this,"异常: "+jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CommodityListActivity.this,"异常: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void showMyCommodity() {
        // 获取设备屏幕的宽
        System.out.println("开始执行");
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, 200);

        LCQuery<LCObject> query = new LCQuery<>("commodityinfo");
        query.whereEqualTo("flag", LEANCLOUD_SEARCH_CODE);
        System.out.println("开始查询");
        query.findInBackground().subscribe(new Observer<List<LCObject>>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(List<LCObject> commodity) {

                System.out.println("查询结果:" + commodity.size());

                for (int i=0; i<commodity.size(); i++) {
                    View view = LayoutInflater.from(CommodityListActivity.this).inflate(R.layout.commoditystyleforlist, null);

                    ImageView imageView = view.findViewById(R.id.iv_CommodityPictureList);
                    TextView textView_info = view.findViewById(R.id.tv_commodityInfoList);
                    TextView textView_from = view.findViewById(R.id.tv_commodityFromList);
                    TextView textView_price = view.findViewById(R.id.tv_commodityPriceList);
                    TextView textView_id = view.findViewById(R.id.maijia_address);
                    TextView textView_commodityId = view.findViewById(R.id.commodityId);

                    url = commodity.get(i).getLCFile("picture").getUrl();
                    if (url.startsWith("http:")) {
                        url = url.replace("http:", "https:");
                    }

                    price = commodity.get(i).get("price") + " ¥";
                    info = (String) commodity.get(i).get("commodityIntroduction");
                    maijia_address = (String) commodity.get(i).get("maijia_address");
                    create_time =  commodity.get(i).getCreatedAt();
                    commodityId = commodity.get(i).getObjectId();

                    Glide.with(CommodityListActivity.this).load(url).into(imageView);

                    textView_info.setText(info);
                    textView_price.setText(price);
                    textView_id.setText(maijia_address);
                    textView_commodityId.setText(commodityId);

                    String userObjectId = (String) commodity.get(i).get("commodityfrom");
                    // 通过objectId查询用户的用户名
                    LCQuery<LCObject> query = new LCQuery<>("_User");
                    query.whereEqualTo("objectId", userObjectId);
                    query.getFirstInBackground().subscribe(new Observer<LCObject>() {
                        public void onSubscribe(Disposable disposable) {}
                        public void onNext(LCObject commodityinfo) {
                            yonghuming = commodityinfo.getString("yonghuming");
                            textView_from.setText(yonghuming);
                            System.out.println("查询成功:"+yonghuming);
                        }
                        public void onError(Throwable throwable) {
                            System.out.println("查询失败: "+throwable.getMessage());
                        }
                        public void onComplete() {}
                    });

                    // 给每一个view设置点击事件
                    view.setOnClickListener(view1 -> {
                        TextView Id = view1.findViewById(R.id.commodityId);
                        String objectId = Id.getText().toString();

                        LCQuery<LCObject> query_commodity = new LCQuery<>("commodityinfo");
                        query_commodity.whereEqualTo("objectId", objectId);
                        query_commodity.getFirstInBackground().subscribe(new Observer<LCObject>() {
                            public void onSubscribe(Disposable disposable) {}
                            public void onNext(LCObject commodityinfo) {
                                Intent intent = new Intent(CommodityListActivity.this, CommodietDetailActivity.class);

                                intent.putExtra("yonghuming", yonghuming);
                                intent.putExtra("price", commodityinfo.getString("price"));
                                intent.putExtra("info", commodityinfo.getString("commodityIntroduction"));
                                intent.putExtra("maijia_address", commodityinfo.getString("maijia_address"));
                                intent.putExtra("url", commodityinfo.getLCFile("picture").getUrl().replace("http:", "https:"));
                                intent.putExtra("create_time", commodityinfo.getCreatedAt());
                                startActivity(intent);
                            }
                            public void onError(Throwable throwable) {
                                System.out.println("查询失败: "+throwable.getMessage());
                            }
                            public void onComplete() {}
                        });
                    });

                    ll_Commodity.addView(view, params);
                }
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
    }
}