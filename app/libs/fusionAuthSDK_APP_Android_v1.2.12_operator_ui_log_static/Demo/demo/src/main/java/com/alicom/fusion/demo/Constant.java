package com.alicom.fusion.demo;

/**
 * @Package: com.example.fusionauthdemo
 * @Description:
 * @CreateDate: 2023/2/10
 */
public class Constant {

    public static final String SCHEME_CODE = "";
    //服务地址
    public static final String NETURL = "";
    //获取token接口
    public static final String GETAUTHREQUESTACTION= "";
    //换取手机号接口
    public static final String VERIFYREQUESTACTION = "";


    //* 1. 快速访问模式
    // * 可快速调试demo体验【部分】功能，可暂不接入服务端api
    // * 临时鉴权token请登陆阿里云控制台获取(https://next.api.aliyun.com/api/Dypnsapi/2017-05-25/GetFusionAuthToken)
    // * 由于最终校验结果需通过服务端api进行，此模式下默认校验成功，填充默认手机号：18888888888
    // */
    /* 2. 正常访问模式
     * 正常接入融合认证服务端和客户端SDK，进行正式调试体验全部功能
     * 请根据接入指南（https://help.aliyun.com/document_detail/2248981.html?spm=a2c4g.2249334.0.0.34c37b8cnywXZq） 首先接入融合认证服务端 APP Server，通过访问APP Server获取鉴权token，并通过访问APP Server做最终认证结果校验
     */
    //默认正常访问模式
    public static final int TOKEN_MODEL=2;
    //快速访问模式下token本地存放
    public static final String LOCAL_TOKEN="";
}
