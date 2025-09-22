package com.alicom.fusion.demo;

import android.app.Activity;
import android.app.Application;

import com.alicom.fusion.demo.utils.TokenActionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Package: com.alicom.fusion.demo
 * @Description:
 * @CreateDate: 2023/2/15
 */
public class MyApplication extends Application {

  private static  MyApplication instance;

  private List<Activity>activityList=new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalInfoManager.getInstance().setContext(this);

        initToken();
    }

    private void initToken(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                CountDownLatch latch=new CountDownLatch(1);
                TokenActionFactory.getToken(GlobalInfoManager.getInstance().getContext());
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }



    public static MyApplication getInstance()

    {

        if(null == instance)

        {

            instance = new MyApplication();

        }

        return instance;

    }



    public void addActivity(Activity activity)

    {

        activityList.add(activity);

    }

//遍历所有Activity并finish

    public void exit()

    {

        for(Activity activity:activityList)

        {

            activity.finish();

        }

        System.exit(0);

    }
}
