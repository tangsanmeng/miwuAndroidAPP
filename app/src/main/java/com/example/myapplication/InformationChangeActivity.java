package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InformationChangeActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText new_yonghuming;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_change);

        initView();
    }

    @Override
    public void onClick(View view) {
        informationChange();
    }

    public void informationChange() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                app = MyApplication.getInstance();
                System.out.println("username======================="+app.infoMap.get("username"));
                //okhttp 客户端
                OkHttpClient client = new OkHttpClient();
                //请求体
                FormBody body = new FormBody.Builder()
                        .add("yonghuming", new_yonghuming.getText().toString().trim())
                        .add("username", app.infoMap.get("username"))
                        .build();
                final Request request = new Request.Builder()
                        .url("https://600274a7f0.zicp.fun/login/changeName/")
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    //请求处理
                    String rd = response.body().string();
                    JSONObject jsonObject = new JSONObject(rd);

                    if (jsonObject.getString("code").equals("104")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String msg = null;
                                try {
                                    msg = jsonObject.getString("msg");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(InformationChangeActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (jsonObject.getString("code").equals("105")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                app = MyApplication.getInstance();
                                try {
                                    String objectId = jsonObject.getString("objectId");
                                    app.infoMap.put("yonghuming", jsonObject.getString("yonghuming"));
                                    Toast.makeText(InformationChangeActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    },500);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }else if (jsonObject.getString("code").equals("106")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String msg = null;
                                try {
                                    msg = jsonObject.getString("msg");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(InformationChangeActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("异常: "+e.getMessage());
                            Toast.makeText(InformationChangeActivity.this,"无法修改: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

//        app = MyApplication.getInstance();
//        String objectId = app.infoMap.get("objectId");
//        LeanCloud.updateInformation(objectId, new_yonghuming.getText().toString().trim());
//        Toast.makeText(InformationChangeActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        },1000);
    }

    private void initView() {
        getSupportActionBar().hide();

        LoginExitActivity.addActivity(this);

        new_yonghuming = findViewById(R.id.new_yonghuming);
        findViewById(R.id.new_submit).setOnClickListener(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        LoginExitActivity.removeActivity(this);
    }
}