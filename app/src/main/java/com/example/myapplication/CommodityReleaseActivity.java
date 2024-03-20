package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.leancloud.LCFile;
import cn.leancloud.LCObject;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommodityReleaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static MyApplication app;
    private EditText et_commodityinfo;
    private EditText et_price;
    private TextView tv_locate;
    private ImageView iv_1;

    // 用户准备上传的图片
    LCFile file = null;
    File file1 = null;
    // 用户输入的价格
    String price = null;
    // 卖家的地址 ( 发货地址
    String maijia_address = null;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new CommodityReleaseActivity.MyAMapLocationListener();
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    // 申请读写和定位权限
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_release);

        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 用户点击发布
            case R.id.tv_submit:
                price = et_price.getText().toString().trim();
                app = MyApplication.getInstance();
                String objectId = app.infoMap.get("objectId");
                String info = et_commodityinfo.getText().toString().trim();
                if (price.equals("")) {
                    Toast.makeText(CommodityReleaseActivity.this, "请先输入价格! ", Toast.LENGTH_SHORT).show();
                }else if (file == null) {
                    Toast.makeText(CommodityReleaseActivity.this, "请上传一张图片作为描述! ", Toast.LENGTH_SHORT).show();
                }else if (info.equals("")) {
                    Toast.makeText(CommodityReleaseActivity.this, "请填写必要的商品描述! ", Toast.LENGTH_SHORT).show();
                }else {






                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //okhttp 客户端
                            OkHttpClient client = new OkHttpClient();
                            //请求体
                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("info",info)// 其他信息
                                    .addFormDataPart("username",app.infoMap.get("username"))// 其他信息
                                    .addFormDataPart("price",price)// 其他信息
                                    .addFormDataPart("maijia_address",maijia_address)// 其他信
                                    .addFormDataPart("file", file1.getName(),
                                            RequestBody.create(MediaType.parse("image/jpg"), file1))//文件
                                    .build();
                            final Request request = new Request.Builder()
                                    .url("https://600274a7f0.zicp.fun/login/commoditySubmit/")
                                    .post(requestBody)
                                    .build();
                            try {
                                Response response = client.newCall(request).execute();
                                //请求处理
                                String rd = response.body().string();
                                JSONObject jsonObject = new JSONObject(rd);
                                if (jsonObject.getString("code").equals("107")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CommodityReleaseActivity.this,"上传成功!",Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }else if (jsonObject.getString("code").equals("108")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CommodityReleaseActivity.this,"上传失败!",Toast.LENGTH_SHORT).show();
                                            try {
                                                System.out.println("上传失败: "+jsonObject.getString("msg"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                }else if (jsonObject.getString("code").equals("109")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CommodityReleaseActivity.this,"上传失败!",Toast.LENGTH_SHORT).show();
                                            try {
                                                System.out.println("上传失败: "+jsonObject.getString("msg"));
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
                                        Toast.makeText(CommodityReleaseActivity.this,"异常: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();








//                    LeanCloud.commodityUpload(info, file, price, objectId, maijia_address);
//                    Toast.makeText(CommodityReleaseActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
//                    finish();
                }
                break;

            // 用户点击上传图片
            case R.id.tv_picture:
                applyForPermission();
                break;

            // 用户取消发布
            case R.id.quxiao:
                finish();
                break;
        }
    }

    private void initView() {
        getSupportActionBar().hide();
        LoginExitActivity.addActivity(this);
        locating();
        findViewById(R.id.quxiao).setOnClickListener(this);
        findViewById(R.id.tv_picture).setOnClickListener(this);
        findViewById(R.id.tv_submit).setOnClickListener(this);

        tv_locate = findViewById(R.id.tv_locate);
        iv_1 = findViewById(R.id.iv_1);
        et_commodityinfo = findViewById(R.id.et_commodityinfo);
        et_price =findViewById(R.id.et_price);
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
                    System.out.println("国家:"+aMapLocation.getCountry()+"省:"+aMapLocation.getProvince()+"城市:"+aMapLocation.getCity());
                    tv_locate.setText(aMapLocation.getCountry() + " " +  aMapLocation.getProvince() + " " + aMapLocation.getCity());
                    maijia_address = aMapLocation.getCountry() + " " +  aMapLocation.getProvince() + " " + aMapLocation.getCity();
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    }

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
            startActivityForResult(intent, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            // 从相册返回的数据
            Log.e(this.getClass().getName(), "Result:" + data.toString());
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                iv_1.setImageURI(uri);
                String path = getPath(uri);
                try {
                    file = LCFile.withAbsoluteLocalPath("demo.jpg", path);
                    file1 = new File(path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    // 获取用户选择图片路径
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginExitActivity.removeActivity(this);
    }
}