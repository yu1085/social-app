package com.alicom.fusion.demo.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alicom.fusion.auth.AlicomFusionAuthCallBack;
import com.alicom.fusion.auth.AlicomFusionAuthUICallBack;
import com.alicom.fusion.auth.AlicomFusionBusiness;
import com.alicom.fusion.auth.AlicomFusionConstant;
import com.alicom.fusion.auth.AlicomFusionLog;
import com.alicom.fusion.auth.HalfWayVerifyResult;
import com.alicom.fusion.auth.demo.R;
import com.alicom.fusion.auth.error.AlicomFusionEvent;
import com.alicom.fusion.auth.numberauth.FusionNumberAuthModel;
import com.alicom.fusion.auth.smsauth.AlicomFusionInputView;
import com.alicom.fusion.auth.smsauth.AlicomFusionVerifyCodeView;
import com.alicom.fusion.auth.token.AlicomFusionAuthToken;
import com.alicom.fusion.auth.upsms.AlicomFusionUpSMSView;
import com.alicom.fusion.demo.Constant;
import com.alicom.fusion.demo.PackageUtil;
import com.alicom.fusion.demo.net.VerifyTokenResult;
import com.alicom.fusion.demo.utils.TokenActionFactory;
import com.mobile.auth.gatewayauth.AuthRegisterViewConfig;
import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig;
import com.mobile.auth.gatewayauth.AuthUIControlClickListener;
import com.mobile.auth.gatewayauth.CustomInterface;
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate;
import com.nirvana.tools.core.ExecutorManager;
import com.alicom.fusion.demo.GlobalInfoManager;
import com.alicom.fusion.demo.PersonalInfoActivity;

import java.util.concurrent.CountDownLatch;

/**
 * @Package: com.example.fusionauthdemo
 * @Description:
 * @CreateDate: 2023/2/7
 */
public class PersonalCenterFragment extends BaseFragment {

    private static final String TAG = "PersonalCenterFragment";

    private View root;

    private AlicomFusionBusiness mAlicomFusionBusiness;
    private AlicomFusionAuthCallBack mAlicomFusionAuthCallBack;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        super.mTextView.setText("我的");
        super.mPersonalInfoLayout.setVisibility(View.VISIBLE);
        verifySuccess=false;
        if (!TextUtils.isEmpty(GlobalInfoManager.getInstance().getUserInfo())) {
            mPersonInfoTitleTv.setText(GlobalInfoManager.getInstance().getUserInfo().substring(0,4)+"****"+GlobalInfoManager.getInstance().getUserInfo().substring(7));
        } else {
            mPersonInfoTitleTv.setText("登录/注册");
        }
        super.mPersonalInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(GlobalInfoManager.getInstance().getUserInfo())) {
                    Intent intent = new Intent(PersonalCenterFragment.this.getActivity(), PersonalInfoActivity.class);
                    startActivity(intent);
                } else {
                    if (!TextUtils.isEmpty(GlobalInfoManager.getInstance().getToken())) {
                            if(verifySuccess){
                                mAlicomFusionBusiness.startSceneWithTemplateId(PersonalCenterFragment.this.getActivity(), "100001",uiCallBack);
                            }else {
                                Toast.makeText(GlobalInfoManager.getInstance().getContext(), "初始化未完成,请稍候", Toast.LENGTH_SHORT).show();
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
                }
            }
        });
        if (TextUtils.isEmpty(GlobalInfoManager.getInstance().getUserInfo())){
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    initAlicomFusionSdk();
                }
            }.start();
        }

    }


    private volatile int  sum=0;

    private volatile boolean verifySuccess=false;

    //1竖屏 2横屏 默认竖屏
    private volatile int screenOretation=2;

    private void initAlicomFusionSdk() {
        AlicomFusionBusiness.useSDKSupplyUMSDK(true,"ymeng");
        //关闭sdk内部日志输出
        AlicomFusionLog.setLogEnable(false);
        mAlicomFusionBusiness = new AlicomFusionBusiness();
        sum=0;
        AlicomFusionAuthToken token=new AlicomFusionAuthToken();
        token.setAuthToken(GlobalInfoManager.getInstance().getToken());
        mAlicomFusionBusiness.initWithToken(GlobalInfoManager.getInstance().getContext(), Constant.SCHEME_CODE,token);
        //适配异形屏
        //mAlicomFusionBusiness.adapterPageShape(true);
        //设置界面方向必须在initWithToken之后，否则无效
        //mAlicomFusionBusiness.setAllPageOrientation(screenOretation);
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
            public void onSDKTokenAuthFailure(AlicomFusionAuthToken token, AlicomFusionEvent alicomFusionEvent) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onSDKTokenAuthFailure "+alicomFusionEvent.getErrorCode() +"  "+alicomFusionEvent.getErrorMsg());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        TokenActionFactory.getToken(GlobalInfoManager.getInstance().getContext());
                        AlicomFusionAuthToken authToken=new AlicomFusionAuthToken();
                        authToken.setAuthToken(GlobalInfoManager.getInstance().getToken());
                        mAlicomFusionBusiness.updateToken(authToken);
                    }
                }).start();
            }

            @Override
            public void onVerifySuccess(String token, String s1, AlicomFusionEvent alicomFusionEvent) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onVerifySuccess  " +token);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        VerifyTokenResult verifyTokenResult = TokenActionFactory.verifyToken(GlobalInfoManager.getInstance().getContext(), token);
                        updateBusiness(verifyTokenResult,s1);
                    }
                }).start();
            }

            @Override
            public void onHalfWayVerifySuccess(String nodeName, String maskToken, AlicomFusionEvent alicomFusionEvent, HalfWayVerifyResult halfWayVerifyResult) {
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

                Log.d(TAG, "AlicomFusionAuthCallBack---onVerifyFailed "+alicomFusionEvent.getErrorCode()+"   "+alicomFusionEvent.getErrorMsg()+"     "+alicomFusionEvent.getCarrierFailedResultData());
                mAlicomFusionBusiness.continueSceneWithTemplateId("100001",false);
            }

            @Override
            public void onTemplateFinish(AlicomFusionEvent alicomFusionEvent) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onTemplateFinish  "+alicomFusionEvent.getErrorCode()+"  "+alicomFusionEvent.getErrorMsg());
                sum=0;
                mAlicomFusionBusiness.stopSceneWithTemplateId("100001");
            }

            @Override
            public void onAuthEvent(AlicomFusionEvent alicomFusionEvent) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onAuthEvent"+alicomFusionEvent.getErrorCode()+"    "+alicomFusionEvent.getCarrierFailedResultData());
            }

            @Override
            public String onGetPhoneNumberForVerification(String s, AlicomFusionEvent alicomFusionEvent) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onGetPhoneNumberForVerification");
                return GlobalInfoManager.getInstance().getUserInfo();
            }

            @Override
            public void onVerifyInterrupt(AlicomFusionEvent alicomFusionEvent) {
                Log.d(TAG, "AlicomFusionAuthCallBack---onVerifyInterrupt"+alicomFusionEvent.toString());
            }
        };
        mAlicomFusionBusiness.setAlicomFusionAuthCallBack(mAlicomFusionAuthCallBack);
        //是否开启日志输出功能
        //AlicomFusionLog.setLogEnable(false);

    }

    private void updateBusinessHalfWay(VerifyTokenResult verifyTokenResult,HalfWayVerifyResult verifyResult,String nodeName){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(verifyTokenResult!=null&&verifyTokenResult.isSuccess()){
                    if ("PASS".equals(verifyTokenResult.getData().getVerifyResult())) {
                        Toast.makeText(GlobalInfoManager.getInstance().getContext(),"校验通过",Toast.LENGTH_SHORT).show();
                        verifyResult.verifyResult(true);
                        GlobalInfoManager.getInstance().setUserInfo(verifyTokenResult.getData().getPhoneNumber());
                    }else {
                        Toast.makeText(GlobalInfoManager.getInstance().getContext(),"校验未通过",Toast.LENGTH_SHORT).show();
                        if(nodeName.equals(AlicomFusionConstant.ALICOMFUSIONAUTH_SMSAUTHNODENAME)&&sum<3){
                            sum++;
                        }else {
                            verifyResult.verifyResult(false);
                            sum=0;
                        }
                    }
                }else {
                    Toast.makeText(GlobalInfoManager.getInstance().getContext(),"校验未通过",Toast.LENGTH_SHORT).show();
                    if(nodeName.equals(AlicomFusionConstant.ALICOMFUSIONAUTH_SMSAUTHNODENAME)&&sum<3){
                        sum++;
                    }else {
                        verifyResult.verifyResult(false);
                        sum=0;
                    }
                }
            }
        });

    }

    private void updateBusiness(VerifyTokenResult verifyTokenResult,String nodeNmae){
       getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(verifyTokenResult!=null&&verifyTokenResult.isSuccess()){
                    if ("PASS".equals(verifyTokenResult.getData().getVerifyResult())) {
                        Toast.makeText(GlobalInfoManager.getInstance().getContext(),"校验通过",Toast.LENGTH_SHORT).show();
                        mAlicomFusionBusiness.continueSceneWithTemplateId("100001",true);
                        GlobalInfoManager.getInstance().setUserInfo(verifyTokenResult.getData().getPhoneNumber());
                        updateView();
                    }else {
                        Toast.makeText(GlobalInfoManager.getInstance().getContext(),"校验未通过",Toast.LENGTH_SHORT).show();
                        if(nodeNmae.equals(AlicomFusionConstant.ALICOMFUSIONAUTH_SMSAUTHNODENAME)&&sum<3){
                            sum++;
                        }else {
                            sum=0;
                            mAlicomFusionBusiness.continueSceneWithTemplateId("100001",false);
                        }
                    }
                }else {
                    Toast.makeText(GlobalInfoManager.getInstance().getContext(),"校验未通过",Toast.LENGTH_SHORT).show();
                    if(nodeNmae.equals(AlicomFusionConstant.ALICOMFUSIONAUTH_SMSAUTHNODENAME)&&sum<3){
                        sum++;
                    }else {
                        sum=0;
                        mAlicomFusionBusiness.continueSceneWithTemplateId("100001",false);
                    }
                }

            }
        });

    }

    private AlicomFusionAuthUICallBack uiCallBack =new AlicomFusionAuthUICallBack() {
        @Override
        public void onPhoneNumberVerifyUICustomView(String templateId,String nodeId, FusionNumberAuthModel fusionNumberAuthModel) {
            fusionNumberAuthModel.getBuilder()
                    .setPrivacyAlertBefore("请阅读")
                    .setPrivacyAlertEnd("等协议")
                    //单独设置授权页协议文本颜色
                    .setPrivacyOneColor(Color.RED)
                    .setPrivacyTwoColor(Color.BLUE)
                    .setPrivacyThreeColor(Color.BLUE)
                    .setPrivacyOperatorColor(Color.GREEN)
                    .setCheckBoxMarginTop(10)
                    .setPrivacyAlertOneColor(Color.RED)
                    .setPrivacyAlertTwoColor(Color.BLUE)
                    .setPrivacyAlertThreeColor(Color.GRAY)
                    .setPrivacyAlertOperatorColor(Color.GREEN)
                    //隐藏授权页顶部登录字样
                    //.hiddenLoginText(true)
                    //隐藏授权页手机号国家代码
                    //.hiddenNumberCountry(true)
                    //隐藏授权页更多登录方式 >按钮
                    //.hiddenSwtichLogin(true)
                   /* .setProtocolShakePath("protocol_shake")
                    .setCheckBoxShakePath("protocol_shake")*/
                    //二次弹窗标题及确认按钮使用系统字体
                    /*.setPrivacyAlertTitleTypeface(Typeface.MONOSPACE)
                    .setPrivacyAlertBtnTypeface(Typeface.MONOSPACE)*/
                    /*//二次弹窗使用自定义字体
                    .setPrivacyAlertTitleTypeface(createTypeface(mContext,"globalFont.ttf"))
                    .setPrivacyAlertBtnTypeface(createTypeface(mContext,"testFont.ttf"))
                    .setPrivacyAlertContentTypeface(createTypeface(mContext,"testFont.ttf"))*/
                    //授权页使用系统字体
                    /* .setNavTypeface(Typeface.SANS_SERIF)
                     .setSloganTypeface(Typeface.SERIF)
                     .setLogBtnTypeface(Typeface.MONOSPACE)
                     .setSwitchTypeface(Typeface.MONOSPACE)
                     .setProtocolTypeface(Typeface.MONOSPACE)
                     .setNumberTypeface(Typeface.MONOSPACE)
                     .setPrivacyAlertContentTypeface(Typeface.MONOSPACE)*/
                    //授权页使用自定义字体
                    /*.setNavTypeface(createTypeface(mContext,"globalFont.ttf"))
                    .setSloganTypeface(createTypeface(mContext,"globalFont.ttf"))
                    .setLogBtnTypeface(createTypeface(mContext,"globalFont.ttf"))
                    .setSwitchTypeface(createTypeface(mContext,"testFont.ttf"))
                    .setProtocolTypeface(createTypeface(mContext,"testFont.ttf"))
                    .setNumberTypeface(createTypeface(mContext,"testFont.ttf"))*/
                    //授权页协议名称系统字体
                    /*.setProtocolNameTypeface(Typeface.SANS_SERIF)
                    //授权页协议名称自定义字体
                    //.setProtocolNameTypeface(createTypeface(mContext,"globalFont.ttf"))
                    //授权页协议名称是否添加下划线
                    .protocolNameUseUnderLine(true)
                    //二次弹窗协议名称系统字体
                    //.setPrivacyAlertProtocolNameTypeface(Typeface.SANS_SERIF)
                    //二次弹窗协议名称自定义字体
                    .setPrivacyAlertProtocolNameTypeface(createTypeface(getActivity(),"globalFont.ttf"))
                    //二次弹窗协议名称是否添加下划线
                    .privacyAlertProtocolNameUseUnderLine(true)*/
                    .setPrivacyAlertTitleContent("请注意")
                    .setPrivacyAlertBtnOffsetX(200)
                    .setPrivacyAlertBtnOffsetY(84)
                    .setPrivacyAlertBtnContent("同意");

            fusionNumberAuthModel.addAuthRegistViewConfig("destory",new AuthRegisterViewConfig.Builder()
                    //两种加载方式都可以
                    .setView(initTestView(500))
                    //.setView(initNumberTextView())
                    //RootViewId有三个参数
                    //AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY 导航栏以下部分为body
                    //AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_TITLE_BAR 导航栏部分 设置导航栏部分记得setNavHidden和setNavReturnHidden显示后才可看到效果
                    //AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_NUMBER 手机号码部分
                    .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
                    .setCustomInterface(new CustomInterface() {
                        @Override
                        public void onClick(Context context) {
                            mAlicomFusionBusiness.destory();
                            //必须在setProtocolShakePath之后才能使用
                            //mAlicomFusionBusiness.privacyAnimationStart();
                            //必须在setCheckBoxShakePath之后才能使用
                            //mAlicomFusionBusiness.checkBoxAnimationStart();
                        }
                    }).build());

            fusionNumberAuthModel.addAuthRegisterXmlConfig(new AuthRegisterXmlConfig.Builder()
                    .setLayout(R.layout.fusion_action, new AbstractPnsViewDelegate() {
                        @Override
                        public void onViewCreated(View view) {
                            findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAlicomFusionBusiness.destory();
                                }
                            });
                        }
                    })
                    .build());


            fusionNumberAuthModel.addPrivacyAuthRegistViewConfig("privacy_cancel",new AuthRegisterViewConfig.Builder()
                    //两种加载方式都可以
                    .setView(initCancelView(100))
                    //.setView(initNumberTextView())
                    //RootViewId有三个参数
                    //AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY 协议区域
                    //AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_TITLE_BAR 导航栏部分
                    //AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_NUMBER 二次弹窗为按钮区域
                    .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_NUMBER)
                    .setCustomInterface(new CustomInterface() {
                        @Override
                        public void onClick(Context context) {
                            fusionNumberAuthModel.quitPrivacyPage();
                        }
                    }).build());
        }

        @Override
        public void onSMSCodeVerifyUICustomView(String templateId,String s,boolean isAutoInput, AlicomFusionVerifyCodeView alicomFusionVerifyCodeView) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*alicomFusionVerifyCodeView.getSendVerifyCodeView().getSendsmsRootRl().removeAllViews();
                    AlicomFusionInputView inputView = alicomFusionVerifyCodeView.getSendVerifyCodeView().getSendsmsRootRl();*/
                   /* alicomFusionVerifyCodeView.getTitleRl().setBackgroundColor(Color.TRANSPARENT);
                    alicomFusionVerifyCodeView.getRootRl().setBackgroundResource(R.drawable.air);*/
                    RelativeLayout inputNumberRootRL =alicomFusionVerifyCodeView.getInputView().getInputNumberRootRL();
                    View inflate = LayoutInflater.from(getContext()).inflate(R.layout.sms_title_content, null);
                    TextView otherLogin = inflate.findViewById(R.id.tv_test);
                    otherLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlicomFusionBusiness.destory();
                        }
                    });
                    RelativeLayout.LayoutParams rl=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rl.addRule(RelativeLayout.ALIGN_BOTTOM,RelativeLayout.TRUE);
                    inflate.setLayoutParams(rl);
                    inputNumberRootRL.addView(inflate);
                }
            });

        }

        @Override
        public void onSMSSendVerifyUICustomView(String templateId, String nodeId, AlicomFusionUpSMSView view, String receivePhoneNumber, String verifyCode) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout rootRl = view.getRootRl();
                    View inflate = LayoutInflater.from(getContext()).inflate(R.layout.up_title_content, null);
                    TextView otherLogin = inflate.findViewById(R.id.tv_test);
                    otherLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlicomFusionBusiness.destory();
                        }
                    });
                    RelativeLayout.LayoutParams rl=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rl.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
                    inflate.setLayoutParams(rl);
                    rootRl.addView(inflate);
                }
            });

        }


    };

    protected View initTestView(int marginTop) {
        TextView switchTV = new TextView(getActivity());
        RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        //一键登录按钮默认marginTop 270dp
        mLayoutParams.setMargins(0, dp2px(getActivity(), marginTop), 0, 0);
        mLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        switchTV.setText("其他登录");
        switchTV.setTextColor(Color.BLACK);
        switchTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.0F);
        switchTV.setLayoutParams(mLayoutParams);
        return switchTV;
    }


    protected View initCancelView(int marginTop) {
        TextView switchTV = new TextView(getActivity());
        RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams.setMargins(dp2px(getActivity(), 50), dp2px(getActivity(), marginTop), 0, 0);
        switchTV.setText("取消");
        switchTV.setTextColor(Color.BLACK);
        switchTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.0F);
        switchTV.setLayoutParams(mLayoutParams);
        return switchTV;
    }


    public static int dp2px(Context context, float dipValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        } catch (Exception e) {
            return (int) dipValue;
        }
    }

    private void updateView(){
        ExecutorManager.getInstance().postMain(new Runnable() {
            @Override
            public void run() {
                mPersonInfoTitleTv.setText(GlobalInfoManager.getInstance().getUserInfo().substring(0,4)+"****"+GlobalInfoManager.getInstance().getUserInfo().substring(7));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAlicomFusionBusiness!=null){
            mAlicomFusionBusiness.destory();
        }
    }


    public  static Typeface createTypeface(Context mContext, String fileName){
        Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(),fileName);
        return typeFace;
    }
}
