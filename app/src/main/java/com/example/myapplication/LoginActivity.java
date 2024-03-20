package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import cn.leancloud.LCUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener {

    /*
    * 登录模块
     */

    private EditText et_username;  // 用户输入的账号
    private EditText et_password;  // 用户输入的密码

    private Button btn_login;      // 登录按钮

    private ImageView two;         // 登录界面左图片
    private ImageView three;       // 登录界面右图片

    private MyApplication app;     // 内存
    private ActivityResultLauncher<Intent> register;  // 用于向上一个页面返回信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null) {
                Intent intent = result.getData();
                if (intent != null && result.getResultCode() == Activity.RESULT_OK) {
                    Bundle bundle = intent.getExtras();
                    String AcName = bundle.getString("username");
                    String pwd = bundle.getString("password");
                    et_username.setText(AcName);
                    et_password.setText(pwd);
                } else {
                    System.out.println("接收到返回值但未处理");
                }
            } else {
                System.out.println("未接收到返回值");
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_register) {
            register();
        }else {
            login(et_username.getText().toString().trim(), et_password.getText().toString().trim());
        }
    }

    // 焦点变更监听器
    @Override
    public void onFocusChange(View view, boolean b) {
        if (view.getId() == R.id.et_password && hasWindowFocus()) {
            two.setImageResource(R.drawable.two1_2);
            three.setImageResource(R.drawable.three1_2);
        }else if (view.getId() == R.id.et_username && hasWindowFocus()) {
            two.setImageResource(R.drawable.two1_1);
            three.setImageResource(R.drawable.three1_1);
        }
    }


    // 编辑框监听器, 密码框不为空时启用登录btn
    private class UseBtn implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!et_username.getText().toString().trim().equals("") && !et_password.getText().toString().trim().equals("")) {
                btn_login.setEnabled(true);
            }else {
                btn_login.setEnabled(false);
            }
        }
    }

    // 初始化各个控件
    private void initView() {
        getSupportActionBar().hide();
        LoginExitActivity.addActivity(this);

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        btn_login = findViewById(R.id.btn_login);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_username.setOnFocusChangeListener(this);
        et_password.setOnFocusChangeListener(this);
        et_username.addTextChangedListener(new UseBtn());
        et_password.addTextChangedListener(new UseBtn());
    }

    // 注册
    private void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        register.launch(intent);
    }

    // 登录
    private void login(String username, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //okhttp 客户端
                OkHttpClient client = new OkHttpClient();
                //请求体
                FormBody body = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();
                final Request request = new Request.Builder()
                        .url("https://600274a7f0.zicp.fun/login/verify/")
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    //请求处理
                    String rd = response.body().string();
                    JSONObject jsonObject = new JSONObject(rd);
                    if (jsonObject.getString("code").equals("100")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this,"账号或密码错误!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (jsonObject.getString("code").equals("101")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                app = MyApplication.getInstance();
                                try {
                                    app.infoMap.put("yonghuming", jsonObject.getString("yonghuming"));
                                    app.infoMap.put("username", username);
                                    app.infoMap.put("password", password);
                                    Intent intent = new Intent(LoginActivity.this, PersonalHomepageActivity.class);
                                    startActivity(intent);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(LoginActivity.this,"登录成功!",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("异常: "+e.getMessage());
                            Toast.makeText(LoginActivity.this,"异常: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        LoginExitActivity.removeActivity(this);
    }


}