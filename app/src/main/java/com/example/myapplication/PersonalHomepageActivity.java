package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.io.FileNotFoundException;

import cn.leancloud.LCFile;

public class PersonalHomepageActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textView;
    private TextView userName;
    private ImageView iv_avatar;
    private LCFile avatar_file;
    private TextView localAdress;
    private MyApplication app;

    // 图片裁减的请求码
    public static final int PICTURE_CROPPING_CODE = 200;
    // 选择图片的请求码
    public static final int OPEN_ALBUM_CODE = 2;


    // 声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    // 声明定位回调监听器
    public AMapLocationListener mLocationListener = new MyAMapLocationListener();
    // 声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    // 权限申请
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    //定位信息
    private static String local_address;

    //用于删除登录时保留的用户信息
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_homepage);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.Shipping_address) {
            locating();
        }else if (view.getId() == R.id.login_exit) {
            loginExit();
        }else if (view.getId() == R.id.iv_avatar) {
            // 更换头像
            System.out.println("这里是头像！！！！！");
        }else if (view.getId() == R.id.re_xiangce) {
            Intent intent = new Intent(PersonalHomepageActivity.this, InformationChangeActivity.class);
            startActivity(intent);
        }else if (view.getId() == R.id.team) {
            Intent intent = new Intent(PersonalHomepageActivity.this, ProductionTeamActivity.class);
            startActivity(intent);
        }else if (view.getId() == R.id.re_maidongxi) {
            Intent intent = new Intent(PersonalHomepageActivity.this, CommodityReleaseActivity.class);
            startActivity(intent);
        }else if (view.getId() == R.id.my_commodity) {
            Intent intent = new Intent(PersonalHomepageActivity.this, MyCommdityActivity.class);
            startActivity(intent);
        }


        else if (view.getId() == R.id.ceshi1) {
            Intent intent = new Intent(PersonalHomepageActivity.this, CommodityListActivity.class);
            startActivity(intent);
        }
    }

    private void initView() {
        getSupportActionBar().hide();
        LoginExitActivity.addActivity(this);

        //点击事件
        findViewById(R.id.Shipping_address).setOnClickListener(this);
        findViewById(R.id.iv_avatar).setOnClickListener(this);
        findViewById(R.id.login_exit).setOnClickListener(this);
        findViewById(R.id.re_xiangce).setOnClickListener(this);
        findViewById(R.id.team).setOnClickListener(this);
        findViewById(R.id.re_maidongxi).setOnClickListener(this);
        findViewById(R.id.my_commodity).setOnClickListener(this);

        // ceshi1 2 commodityList
        findViewById(R.id.ceshi1).setOnClickListener(this);

        localAdress = findViewById(R.id.local_adress);
        app = MyApplication.getInstance();
        String Ac_num = app.infoMap.get("username");
        String yonghuming = app.infoMap.get("yonghuming");
        userName = findViewById(R.id.yonghuxingming);
        userName.setText(yonghuming);
        textView = findViewById(R.id.dengruyonghu);
        textView.setText(Ac_num);
    }
/*
    // 申请读写权限
    private void applyForPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }else {
                requestPermissions(PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        }else {
            // 获取权限后跳转至相册
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpg");
            startActivityForResult(intent, OPEN_ALBUM_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_ALBUM_CODE) {
            // 从相册返回的数据
            Log.e(this.getClass().getName(), "Result:" + data.toString());
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                iv_avatar.setImageURI(uri);
                String path = getPath(uri);
                try {
                    avatar_file = LCFile.withAbsoluteLocalPath("demo.jpg", path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }*/

    // 退出登录, 返回登录页面
    private void loginExit() {
        System.out.println("开始退出App！！！！");

        // 清除config文件中保存的用户信息
        sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        LoginExitActivity.finishAllActivity();
        startActivity(new Intent(this, LoginActivity.class));
    }

    // 定位
    private void locating() {
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);
        try {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                }else {
                    requestPermissions(PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
                }
            }else {
                init();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "正在定位!", Toast.LENGTH_SHORT).show();
    }

    private void init() throws Exception {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.ZH);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private class MyAMapLocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    Log.e("您的位置：", aMapLocation.getAddress());
//                    System.out.println("国家:"+aMapLocation.getCountry()+"省:"+aMapLocation.getProvince()+"城市:"+aMapLocation.getCity());
                    local_address = aMapLocation.getAddress();
                    app.infoMap.put("adress", localAdress.toString());
                    System.out.println("toString"+local_address.toString());
                    localAdress.setText(local_address);
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    }

    // 返回此页面时, 调用该方法刷新数据
    @Override
    public void onResume() {
        super.onResume();
        app = MyApplication.getInstance();
        String yonghuming = app.infoMap.get("yonghuming");
        userName.setText(yonghuming);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        LoginExitActivity.removeActivity(this);
    }
}