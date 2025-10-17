package cn.jiguang.demo;


import android.app.Application;

import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import cn.jiguang.demo.baselibrary.SPUtils;
import cn.jpush.android.api.JPushInterface;
import cn.jiguang.api.utils.JCollectionAuth;
import cn.jpush.android.data.JPushCollectControl;


/**
 * Copyright(c) 2020 极光
 */
public class App extends Application {
    public static String J_APP_KEY="";//需要用户主动赋值，否则 在通过API设置appKey时，Demo会显示为空
    @Override
    public void onCreate() {
        super.onCreate();
        initToast();
        initKV();
        if (SPUtils.isShowPrivacy(this)) {
            //打开日志开关，发布版本建议关闭
            JPushInterface.setDebugMode(true);

            //同意隐私政策，同意开启推送业务开关
            JCollectionAuth.setAuth(this, true);
            // 启用推送业务
            JPushInterface.init(this);
        } else {
            JCollectionAuth.setAuth(this, false);
        }
    }

    private void initKV() {
        MMKV.initialize(this);
    }

    private void initToast() {
        ToastUtils.init(this);
    }
}
