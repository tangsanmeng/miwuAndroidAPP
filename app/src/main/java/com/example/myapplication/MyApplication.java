package com.example.myapplication;

import android.app.Application;

import java.util.HashMap;

import cn.leancloud.LeanCloud;

public class MyApplication extends Application {

    /*
    * 内存模块
     */

    private static MyApplication mApp;

    public static MyApplication getInstance() {
        return mApp;
    }

    //声明一个公共的信息映射对象，作为全局变量使用
    public HashMap<String, String> infoMap = new HashMap<>();

    //在App启动时调用
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化LeanCloud云数据库
        LeanCloud.initialize(this, "ILpxKphAjAghfoPeZkNVtN0w-gzGzoHsz",
                "jI1kxeh2mhYrSfzZHapy5Y3w",
                "https://ilpxkpha.lc-cn-n1-shared.com");

        mApp = this;
    }

    //App终止的时候调用
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
