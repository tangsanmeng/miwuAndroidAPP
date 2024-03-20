package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.leancloud.LCUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    /*
    * 注册模块
     */

    private EditText username;  //用户输入的账号
    private EditText password;  //用户输入的密码
    private EditText phone;     //用户输入的联系方式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    @Override
    public void onClick(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //okhttp 客户端
                OkHttpClient client = new OkHttpClient();
                //请求体
                FormBody body = new FormBody.Builder()
                        .add("username", username.getText().toString().trim())
                        .add("password", password.getText().toString().trim())
                        .add("phone", phone.getText().toString().trim())
                        .build();
                final Request request = new Request.Builder()
                        .url("https://600274a7f0.zicp.fun/login/register/")
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    //请求处理
                    String rd = response.body().string();
                    JSONObject jsonObject = new JSONObject(rd);
                    if (jsonObject.getString("code").equals("102")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this,"注册成功!",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString("username", username.getText().toString());
                                bundle.putString("password", password.getText().toString());
                                bundle.putString("phone", phone.getText().toString());
                                intent.putExtras(bundle);
                                setResult(Activity.RESULT_OK, intent);
                                System.out.println("开始返回上一页面");
                                finish();
                            }
                        });
                    }else if (jsonObject.getString("code").equals("103")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String msg = jsonObject.getString("msg");
                                    Toast.makeText(RegisterActivity.this,msg,Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterActivity.this,"异常: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();





//        // 创建实例
//        LCUser user = new LCUser();
//
//        // 等同于 user.put("username", "Tom")
//        user.setUsername(username.getText().toString().trim());
//        user.setPassword(password.getText().toString().trim());
//        user.setMobilePhoneNumber(phone.getText().toString().trim());
//        user.put("yonghuming", username.getText().toString().trim());
//        // user.setEmail("tom@leancloud.rocks");
//
//        user.signUpInBackground().subscribe(new Observer<LCUser>() {
//            public void onSubscribe(Disposable disposable) {}
//            public void onNext(LCUser user) {
//                // 注册成功
//                System.out.println("注册成功。objectId：" + user.getObjectId());
//                Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString("username", username.getText().toString());
//                bundle.putString("password", password.getText().toString());
//                bundle.putString("phone", phone.getText().toString());
//                intent.putExtras(bundle);
//                setResult(Activity.RESULT_OK, intent);
//                System.out.println("开始返回上一页面");
//                finish();
//            }
//            public void onError(Throwable throwable) {
//                // 注册失败（通常是因为用户名已被使用）
//                Toast.makeText(RegisterActivity.this, "用户名已被使用或手机号输入不规范！"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//            public void onComplete() {}
//        });
    }

    private void initView() {
        getSupportActionBar().hide();
        LoginExitActivity.addActivity(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);

        findViewById(R.id.register).setOnClickListener(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        LoginExitActivity.removeActivity(this);
    }
}