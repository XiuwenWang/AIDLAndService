package com.example.administrator.myapplication;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.mingxiu.dialog.dialog.IOSAlert;

import java.io.File;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.JsonHttpRequestCallback;
import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;

public class MainActivity extends AppCompatActivity {

    private String data ="  \"version\": \"2\",\n" +
            "      \"status\": \"0\",\n" +
            "      \"cont\": \"测试版本\",\n" +
            "      \"id\": 1,\n" +
            "      \"deleteStatus\": false,\n" +
            "      \"addTime\": 1527091200000\n" +
            "    }\n" +
            "  ],\n" +
            "  \"msg\": \"success\"\n" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());

        checkUpdata();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permissions) {
                        downLoadApk();
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permissions) {

                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.REQUEST_INSTALL_PACKAGES);
            }


        });
    }

    private void checkUpdata() {
//        HttpRequest.post("", new JsonHttpRequestCallback() {
//            @Override
//            protected void onSuccess(JSONObject jsonObject) {
//                super.onSuccess(jsonObject);
//
//            }
//        });

        new IOSAlert.Builder(this)
                .setTitle("最新版本")
                .setMessage("1,\n2,\n3,")
                .setPositiveButton("确定", new IOSAlert.OnClickListener() {
                    @Override
                    public void onClick(IOSAlert iosAlert) {
                        downLoadApk();
                    }
                })
                .setCancelable(false)
                .show();

    }

    private void downLoadApk() {
        final File saveFile = new File("/sdcard/rootexplorer_140220.apk");
        String url = "https://whxzy.oss-cn-shanghai.aliyuncs.com/apk/app-release.apk";

        HttpRequest.download(url, saveFile, new FileDownloadCallback() {
            //开始下载
            @Override
            public void onStart() {
                super.onStart();
            }

            //下载进度
            @Override
            public void onProgress(int progress, long networkSpeed) {
                super.onProgress(progress, networkSpeed);
                Log.d("-下载进度-", progress + "");
            }

            //下载失败
            @Override
            public void onFailure() {
                super.onFailure();
            }

            //下载完成（下载成功）
            @Override
            public void onDone() {
                super.onDone();
                installApk(saveFile);
                Log.d("-下载完成-", "下载成功" + "");
            }
        });
    }
    /**
     * 安装apk
     *
     * @param file
     */
    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
            // "sven.com.fileprovider.fileprovider"即是在清单文件中配置的authorities
            // 通过FileProvider创建一个content类型的Uri
            data = FileProvider.getUriForFile(this, "com.example.administrator.myapplication", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
        } else {
            data = Uri.fromFile(file);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
