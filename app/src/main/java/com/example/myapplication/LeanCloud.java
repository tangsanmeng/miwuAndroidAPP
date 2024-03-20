package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.leancloud.LCFile;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.types.LCNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LeanCloud {

    private static MyApplication app;

    // 搜索用户的用户名，并在内存中存入用户的id
    public static void searchYongHuMing(String username) {
        LCQuery<LCObject> query = new LCQuery<>("_User");
        // 获取指定username的用户信息集合
        query.whereEqualTo("username", username);
        query.getFirstInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject todo) {
                // todo 是第一个满足条件的 Todo 对象
                String yonghuming = todo.getString("yonghuming");
                app = MyApplication.getInstance();
                app.infoMap.put("yonghuming", yonghuming);

                String objectId = todo.getString("objectId");
                app = MyApplication.getInstance();
                app.infoMap.put("objectId", objectId);
            }
            public void onError(Throwable throwable) {
                System.out.println("查询失败: "+throwable.getMessage());
            }
            public void onComplete() {}
        });
    }

    // 通过objectId 寻找对应用户的用户名
    public static void searchYongHuMingByObjectId(String objectId) {
        LCQuery<LCObject> query = new LCQuery<>("_User");
        // 获取指定username的用户信息集合
        query.whereEqualTo("objectId", objectId);
        query.getFirstInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject todo) {
                // todo 是第一个满足条件的 Todo 对象
                String yonghuming = todo.getString("yonghuming");
            }
            public void onError(Throwable throwable) {
                System.out.println("查询失败: "+throwable.getMessage());
            }
            public void onComplete() {}
        });
    }

    // 更新用户的用户名
    public static void updateInformation(String objectId, String new_yonghuming) {

        LCObject todo = LCObject.createWithoutData("_User", objectId);
        todo.put("yonghuming", new_yonghuming);
        todo.saveInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject savedTodo) {
                // 保存成功, 覆盖之前的用户名
                String yonghuming = (String) savedTodo.get("yonghuming");
                app = MyApplication.getInstance();
                app.infoMap.put("yonghuming", yonghuming);
                System.out.println("保存成功, 新用户名为: "+yonghuming);
            }
            public void onError(Throwable throwable) {
                System.out.println("保存失败！"+throwable.getMessage());
            }
            public void onComplete() {}
        });;
    }

    // 上传用户输入的商品信息
    public static void commodityUpload(String s, LCFile file, String price, String objectId, String address) {
        // 构建对象
        LCObject commodityinfo = new LCObject("commodityinfo");
        // 为属性赋值
        commodityinfo.put("commodityIntroduction", s);
        commodityinfo.put("picture", file);
        commodityinfo.put("price", price);
        commodityinfo.put("commodityfrom", objectId);
        commodityinfo.put("maijia_address", address);
        commodityinfo.put("flag", "114514");

        commodityinfo.saveInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject todo) {
                // 成功保存之后，执行其他逻辑
                System.out.println("保存成功。objectId：" + todo.getObjectId());
            }
            public void onError(Throwable throwable) {
                // 异常处理
                System.out.println(" 异常: " + throwable.getMessage());
            }
            public void onComplete() {}
        });
    }

    // 删除指定objectId对应的商品
    public static void deleteCommodity(String objectId) {
        LCObject todo = LCObject.createWithoutData("commodityinfo", objectId);
        todo.deleteInBackground().subscribe(new Observer<LCNull>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {}

            @Override
            public void onNext(LCNull response) {
                // 成功删除后
                System.out.println("删除成功啊！！");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println("failed to delete a todo: " + e.getMessage());
            }

            @Override
            public void onComplete() {}
        });
    }


    //  只上传商品信息中的简介 ( 弃用
    public static void commodityInformationUploading(String s) {
        // 构建对象
        LCObject commodityinfo = new LCObject("commodityinfo");

        // 为属性赋值
        commodityinfo.put("commodityIntroduction", s);

        // 将对象保存到云端
        commodityinfo.saveInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject commodityinfo) {
                // 成功保存之后，执行其他逻辑
                System.out.println("保存成功。objectId：" + commodityinfo.getObjectId());
            }
            public void onError(Throwable throwable) {
                // 异常处理
                System.out.println("异常: "+throwable.getMessage());
            }
            public void onComplete() {}
        });
    }

    // 只讲图片保存至数据库 ( 弃用
    public static void pictureUpload(LCFile file) {
        LCObject commodityinfo = new LCObject("commodityinfo");
        commodityinfo.put("picture", file);

        commodityinfo.saveInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject todo) {
                // 成功保存之后，执行其他逻辑
                System.out.println("保存成功。objectId：" + todo.getObjectId());
            }
            public void onError(Throwable throwable) {
                // 异常处理
                System.out.println(" 异常: " + throwable.getMessage());
            }
            public void onComplete() {}
        });
    }
}
