package com.example.myapplication;

import android.app.Activity;

import java.util.ArrayList;

public class LoginExitActivity {
    public static ArrayList<Activity> mActivityList = new ArrayList<Activity>();

    //onCreate()时添加
    public static void addActivity(Activity activity){
        //判断集合中是否已经添加，添加过的则不再添加
        if (!mActivityList.contains(activity)){
            mActivityList.add(activity);
        }
    }

    //onDestroy()时删除
    public static void removeActivity(Activity activity){
        mActivityList.remove(activity);
    }


    //关闭所有Activity
    public static void finishAllActivity(){
        for (Activity activity : mActivityList){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
