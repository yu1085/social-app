package com.alicom.fusion.demo;

import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alicom.fusion.auth.AlicomFusionAuthCallBack;
import com.alicom.fusion.auth.AlicomFusionBusiness;
import com.alicom.fusion.auth.AlicomFusionConstant;
import com.alicom.fusion.auth.HalfWayVerifyResult;
import com.alicom.fusion.auth.demo.R;
import com.alicom.fusion.auth.error.AlicomFusionEvent;
import com.alicom.fusion.auth.token.AlicomFusionAuthToken;
import com.alicom.fusion.demo.net.GetAuthTokenResult;
import com.alicom.fusion.demo.net.HttpRequestUtil;
import com.alicom.fusion.demo.net.VerifyTokenResult;
import com.alicom.fusion.demo.utils.TokenActionFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @Package: com.example.fusionauthdemo
 * @Description:
 * @CreateDate: 2023/2/9
 */
public class PersonalInfoActivity extends AppCompatActivity {

    private static final String TAG = "PersonalInfoActivity";

    private AlicomFusionBusiness mAlicomFusionBusiness;
    private AlicomFusionAuthCallBack mAlicomFusionAuthCallBack;
    private volatile String currentTemplatedId="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }
        MyApplication.getInstance().addActivity(this);
        refreshToken();
        ListView functionListview = (ListView) findViewById(R.id.personal_info_function_listview);
        ArrayAdapter<String> functionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                new String[]{"修改手机号", "绑定新手机号", "验证当前手机号", "重置登录密码"});
        functionListview.setAdapter(functionAdapter);
        functionListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mAlicomFusionBusiness != null) {
                    if (!TextUtils.isEmpty(GlobalInfoManager.getInstance().getToken())) {
                        if(!verifySuccess){
                            Toast.makeText(GlobalInfoManager.getInstance().getContext(), "初始化未完成,请稍候", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        switch (position) {
                            case 0:
                                mAlicomFusionBusiness.startSceneWithTemplateId(PersonalInfoActivity.this, "100002");
                                currentTemplatedId="100002";
                                break;
                            case 1:
                                mAlicomFusionBusiness.startSceneWithTemplateId(PersonalInfoActivity.this, "100004");
                                currentTemplatedId="100004";
                                break;
                            case 2:
                                mAlicomFusionBusiness.startSceneWithTemplateId(PersonalInfoActivity.this, "100005");
                                currentTemplatedId="100005";
                                break;
                            case 3:
                                mAlicomFusionBusiness.startSceneWithTemplateId(PersonalInfoActivity.this, "100003");
                                currentTemplatedId="100003";
                                break;
                            default:
                                break;
                        }

                    } else {
                        Toast.makeText(GlobalInfoManager.getInstance().getContext(), "正在获取token，请稍后", Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                TokenActionFactory.getToken(GlobalInfoManager.getInstance().getContext());
                            }
                        }).start();
                    }
                } else {
                    Toast.makeText(GlobalInfoManager.getInstance().getContext(), "正在初始化SDK，请稍后", Toast.LENGTH_SHORT).show();
                }


            }
        });

        ListView otherFunctionListview = (ListView) findViewById(R.id.personal_info_other_listview);
        ArrayAdapter<String> otherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"功能1", "功能2", "功能3"});
        otherFunctionListview.setAdapter(otherAdapter);
        findViewById(R.id.exitapp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalInfoManager.getInstance().setUserInfo("");
                PersonalInfoActivity.this.finish();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                verifySuccess=false;
                initAlicomFusionSdk();
            }
        }).start();
    }

    private volatile int sum=0;
    private volatile boolean verifySuccess=false;

    private void initAlicomFusionSdk() {
        AlicomFusionBusiness.useSDKSupplyUMSDK(true,"umeng");
        String token = null;
        sum=0;
        if(TextUtils.isEmpty(GlobalInfoManager.getInstance().getToken())){
            TokenActionFactory.getToken(GlobalInfoManager.getInstance().getContext());
        }
        token=GlobalInfoManager.getInstance().getToken();
        mAlicomFusionBusiness = new AlicomFusionBusiness();
        AlicomFusionAuthToken authToken=new AlicomFusionAuthToken();
        authToken.setAuthToken(token);
        mAlicomFusionBusiness.initWithToken(GlobalInfoManager.getInstance().getContext(), Constant.SCHEME_CODE,authToken);
        mAlicomFusionAuthCallBack = new AlicomFusionAuthCallBack() {
            @Override
            public AlicomFusionAuthToken onSDKTokenUpdate() {
                Log.d(TAG, "AlicomFusionAuthCallBack---onSDKTokenUpdate");
                AlicomFusionAuthToken token=new AlicomFusionAuthToken();
                CountDownLatch latch=new CountDownLatch(1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TokenActionFactory.getToken(GlobalInfoManager.getInstance().getContext());
                        latch.countDown();
                    }
                }).start();
                try {
                    latch.await();
                    token.setAuthToken(GlobalInfoManager.getInstance().getToken());
                } catch (InterruptedException e) {
                }
                return token;
            }

            @Override
            public void onSDKTokenAuthSuccess() {
                Log.d(TAG, "AlicomFusionAuthCallBack---onSDKTokenAuthSuccess");
                verifySuccess=true;
            }

            @Override
            public void onSDKTokenAuthFailure(AlicomFusionAuthToken token1, AlicomFusionEvent alicomFusionEvent) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onSDKTokenAuthFailure " +alicomFusionEvent.getErrorCode());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        CountDownLatch latch=new CountDownLatch(1);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                TokenActionFactory.getToken(GlobalInfoManager.getInstance().getContext());
                                latch.countDown();
                            }
                        }).start();
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                        }
                        AlicomFusionAuthToken authToken=new AlicomFusionAuthToken();
                        authToken.setAuthToken(GlobalInfoManager.getInstance().getToken());
                        mAlicomFusionBusiness.updateToken(authToken);
                    }
                }).start();
            }

            @Override
            public void onVerifySuccess(String token, String s1,AlicomFusionEvent alicomFusionEvent) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onVerifySuccess  "+token);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        VerifyTokenResult verifyTokenResult = TokenActionFactory.verifyToken(GlobalInfoManager.getInstance().getContext(), token);
                        updateBusiness(verifyTokenResult,s1);
                    }
                }).start();
            }

            @Override
            public void onHalfWayVerifySuccess(String nodeName, String maskToken,AlicomFusionEvent alicomFusionEvent, HalfWayVerifyResult halfWayVerifyResult) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onHalfWayVerifySuccess  "+maskToken);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        VerifyTokenResult verifyTokenResult = TokenActionFactory.verifyToken(GlobalInfoManager.getInstance().getContext(), maskToken);
                        updateBusinessHalfWay(verifyTokenResult,halfWayVerifyResult,nodeName);

                    }
                }).start();
            }

            @Override
            public void onVerifyFailed(AlicomFusionEvent alicomFusionEvent, String s) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onVerifyFailed "+alicomFusionEvent.getErrorCode()+"   "+alicomFusionEvent.getErrorMsg());
                mAlicomFusionBusiness.continueSceneWithTemplateId(currentTemplatedId,false);
            }

            @Override
            public void onTemplateFinish(AlicomFusionEvent alicomFusionEvent) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onTemplateFinish  "+alicomFusionEvent.getErrorCode());
                sum=0;
                mAlicomFusionBusiness.stopSceneWithTemplateId(currentTemplatedId);
            }


            @Override
            public void onAuthEvent(AlicomFusionEvent alicomFusionEvent) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onAuthEvent"+alicomFusionEvent.getErrorCode());

            }


            @Override
            public String onGetPhoneNumberForVerification(String s,AlicomFusionEvent alicomFusionEvent) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onGetPhoneNumberForVerification");

                return GlobalInfoManager.getInstance().getUserInfo();
            }

            @Override
            public void onVerifyInterrupt(AlicomFusionEvent alicomFusionEvent) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onVerifyInterrupt"+alicomFusionEvent.toString());
            }
        };
        mAlicomFusionBusiness.setAlicomFusionAuthCallBack(mAlicomFusionAuthCallBack);
    }

    private void updateBusiness(VerifyTokenResult verifyTokenResult,String nodeNmae){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(verifyTokenResult!=null&&verifyTokenResult.isSuccess()){
                    if ("PASS".equals(verifyTokenResult.getData().getVerifyResult())) {
                        if("100003".equals(currentTemplatedId)){
                             if(!verifyTokenResult.getData().getPhoneNumber().equals(GlobalInfoManager.getInstance().getUserInfo())){
                                 Toast.makeText(GlobalInfoManager.getInstance().getContext(),"请注意使用已登录账号",Toast.LENGTH_SHORT).show();
                                 return;
                             }
                        }
                        Toast.makeText(GlobalInfoManager.getInstance().getContext(),"校验通过",Toast.LENGTH_SHORT).show();
                        mAlicomFusionBusiness.continueSceneWithTemplateId(currentTemplatedId,true);
                        GlobalInfoManager.getInstance().setUserInfo(verifyTokenResult.getData().getPhoneNumber());
                    }else {
                        Toast.makeText(GlobalInfoManager.getInstance().getContext(),"校验未通过",Toast.LENGTH_SHORT).show();
                        if(nodeNmae.equals(AlicomFusionConstant.ALICOMFUSIONAUTH_SMSAUTHNODENAME)&&sum<3){
                            sum++;
                        }else {
                            sum=0;
                            mAlicomFusionBusiness.continueSceneWithTemplateId(currentTemplatedId,false);
                        }
                    }
                }else {
                    Toast.makeText(GlobalInfoManager.getInstance().getContext(),"校验未通过",Toast.LENGTH_SHORT).show();
                    if(nodeNmae.equals(AlicomFusionConstant.ALICOMFUSIONAUTH_SMSAUTHNODENAME)&&sum<3){
                        sum++;
                    }else {
                        sum=0;
                        mAlicomFusionBusiness.continueSceneWithTemplateId(currentTemplatedId,false);
                    }
                }

            }
        });

    }



    private void updateBusinessHalfWay(VerifyTokenResult verifyTokenResult,HalfWayVerifyResult verifyResult,String nodeNmae){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(verifyTokenResult!=null&&verifyTokenResult.isSuccess()){
                    if ("PASS".equals(verifyTokenResult.getData().getVerifyResult())) {
                        if("100002".equals(currentTemplatedId)){
                            if(!verifyTokenResult.getData().getPhoneNumber().equals(GlobalInfoManager.getInstance().getUserInfo())){
                                Toast.makeText(GlobalInfoManager.getInstance().getContext(),"请注意使用已登录账号",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        Toast.makeText(GlobalInfoManager.getInstance().getContext(),"校验通过",Toast.LENGTH_SHORT).show();
                        verifyResult.verifyResult(true);
                        GlobalInfoManager.getInstance().setUserInfo(verifyTokenResult.getData().getPhoneNumber());
                    }else {
                        Toast.makeText(GlobalInfoManager.getInstance().getContext(),"校验未通过",Toast.LENGTH_SHORT).show();
                        if(nodeNmae.equals(AlicomFusionConstant.ALICOMFUSIONAUTH_SMSAUTHNODENAME)&&sum<3){
                            sum++;
                        }else {
                            verifyResult.verifyResult(false);
                            sum=0;
                        }
                    }
                }else {
                    Toast.makeText(GlobalInfoManager.getInstance().getContext(),"校验未通过",Toast.LENGTH_SHORT).show();
                    if(nodeNmae.equals(AlicomFusionConstant.ALICOMFUSIONAUTH_SMSAUTHNODENAME)&&sum<3){
                        sum++;
                    }else {
                        verifyResult.verifyResult(false);
                        sum=0;
                    }
                }
            }
        });

    }



    private void refreshToken(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                if(TextUtils.isEmpty(GlobalInfoManager.getInstance().getToken())){
                    CountDownLatch latch=new CountDownLatch(1);
                    GetAuthTokenResult authToken = HttpRequestUtil.getAuthToken(GlobalInfoManager.getInstance().getContext());
                    latch.countDown();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                    }
                    GlobalInfoManager.getInstance().setToken(authToken==null?"":authToken.getModel());
                }
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAlicomFusionBusiness!=null){
            mAlicomFusionBusiness.destory();
        }
    }
}
