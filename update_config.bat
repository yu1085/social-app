@echo off
echo ========================================
echo 更新支付宝配置
echo ========================================

echo.
echo 请将您从支付宝开放平台获取的支付宝公钥粘贴到下面：
echo （按Ctrl+V粘贴，然后按回车继续）
echo.

set /p alipay_public_key="支付宝公钥: "

echo.
echo 正在更新配置文件...

cd SocialMeet\src\main\resources

echo # 支付宝API真实配置 > application-alipay-real.yml
echo app: >> application-alipay-real.yml
echo   alipay: >> application-alipay-real.yml
echo     # 应用ID（从支付宝开放平台获取） >> application-alipay-real.yml
echo     app-id: 2021005195696348 >> application-alipay-real.yml
echo     # 应用私钥（PKCS8格式） >> application-alipay-real.yml
echo     private-key: ^| >> application-alipay-real.yml
echo       MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDgl26R+MzSa8Y9 >> application-alipay-real.yml
echo       g2IEAkYZaG8I/X19pLN0ZXqWs94RPqwgJSFBBiqFiNHJgRQjhUxUwjmj1/lBXfYd >> application-alipay-real.yml
echo       mShcDGWua+4unFJtfnZv69UYOJhGkLFR0zkSeN/CorDFoFbKzvZju6JqWNczBqHj >> application-alipay-real.yml
echo       NZ2kkAWrHfT6yPxXTpldkErDH1l81xhi3hJ2KyKVFA1aXhFPkUy/25GVodjpe8n5 >> application-alipay-real.yml
echo       vyRdWjcRkIv0lA1K9GxvJs+w7X9GbnJFFvgPTL6/mtJLx1mMD5LKYccQN0kvfwbP >> application-alipay-real.yml
echo       UIAH16y/hevMU0pMdzJthBHcVsfAjjlDAK8Y/EwxLu+sY/upLJzEOwjiRAPunyB9 >> application-alipay-real.yml
echo       vizUi88bAgMBAAECggEADzCexVem/rpUU5e9utiuhgxmqhx+7s8DZiVw551tsQvk >> application-alipay-real.yml
echo       l137hErW+GYbDj5hLs/WrErgnCDA1nd07GaN8L/T5rqdBuECwC4T85jnNjapsega >> application-alipay-real.yml
echo       Xhbjnsi3FZJsU0l3ZUsfJfqsTvYIRAa4T0Yyjc1B3ONvTMDoND14KPhX3h+SLVu8 >> application-alipay-real.yml
echo       cO3P44NJ7N8EbPyF7khc4IM00BgQi0mh039HNJS0fWsb+ffBLil7FHVul1rsgTJ8 >> application-alipay-real.yml
echo       Sca0ReCRcSotcPPzqHsapQSkrrKbzdYLo/yoOLSs0oilJq7NRLIo9vATh8Y2MFLi >> application-alipay-real.yml
echo       RBcxxZlBtjuNFhYB+CScGqGWNIwrunRvfoXMWZCIkQKBgQDnE5v93qg2IPmNBtD+ >> application-alipay-real.yml
echo       uaCk8ZxtT3etMwQV/oHzxX3CSM4OJo+YHC415wnm8yyv4cV+yq2FEBsMHFqto6uF >> application-alipay-real.yml
echo       N5zNCdBGzJ5Gy5omAIy4iW/y36Sc5jyhhV91/bR2ABitcPjG4FyWXO8TQmgQCykm >> application-alipay-real.yml
echo       rpNs+18xqjks+Ptde8EvbJU8UQKBgQD40MJzsie7Us+av8mMDl8hyrgBK6Ie0CEt >> application-alipay-real.yml
echo       3ccgX0QmzrtOld2yCFuVOZCtNTgNymzbjWnv8hBiK9Wxt1y9Z6ppkV2gEUat3l9o >> application-alipay-real.yml
echo       PeXBlO+JLMER8rXzdJCM/DYnsb/o5jSHQ+riMlyPZJUR9J93m+ARIazdxDFJ6f4Y >> application-alipay-real.yml
echo       Bp7iljP1qwKBgBuODBD25yolPnzhrjRh4FUq9pNWYZgGuFsDEW4HQ9rSIbgwQvlJ >> application-alipay-real.yml
echo       iv0kgtrGbrK+gnV/J7fxVrhX/TwtgzuMiScuH2cB6XHZv5T+hclPokjdAF0xW1OL >> application-alipay-real.yml
echo       evmv+kAD/O1ZxmC4ynGnvV6XkZ8wJYUWVkAtrYlXmh/RxO+93SiRky4xAoGAbo5T >> application-alipay-real.yml
echo       2JSQkut404nkRBceriUlHWAF6SsRkw+4KuopnhS9pW1x1GliSCwL3OqIvZf0RpnP >> application-alipay-real.yml
echo       OI6WOQjBKvYOfxpQ3hd5QmZqwVbNjcPzEtDanlkEcLUno8VndT2b0odoflPLg07q >> application-alipay-real.yml
echo       TxeRiSVoF1JaiQr5xKLx7JgATpdqD8LhVkznSYECgYBdigTEgXLeE3+IrKHD62Jd >> application-alipay-real.yml
echo       ZuKuENd5U8OdJUwoLKl7tLNBpgZE0kEqktvwR7bNE21AG0i1CZv4BDQYetT69hT1 >> application-alipay-real.yml
echo       kjqMNMu9DvnWJy6NehuA6h90LFVvraQJBy5o28HHiZ3eJJbrHHzraN3/mdK1G2cQ >> application-alipay-real.yml
echo       5kYJGdjaA95j9jwYqqD0gA== >> application-alipay-real.yml
echo     # 支付宝公钥（从开放平台获取） >> application-alipay-real.yml
echo     public-key: ^| >> application-alipay-real.yml
echo       %alipay_public_key% >> application-alipay-real.yml
echo     # 支付宝网关地址 >> application-alipay-real.yml
echo     gateway-url: https://openapi.alipay.com/gateway.do >> application-alipay-real.yml
echo     # 签名算法 >> application-alipay-real.yml
echo     sign-type: RSA2 >> application-alipay-real.yml
echo     # 字符编码 >> application-alipay-real.yml
echo     charset: UTF-8 >> application-alipay-real.yml
echo     # 数据格式 >> application-alipay-real.yml
echo     format: json >> application-alipay-real.yml
echo     # API版本 >> application-alipay-real.yml
echo     version: 1.0 >> application-alipay-real.yml
echo     # 超时时间（毫秒） >> application-alipay-real.yml
echo     timeout: 30000 >> application-alipay-real.yml

echo.
echo 配置文件更新完成！
echo 文件位置: SocialMeet\src\main\resources\application-alipay-real.yml
echo.
pause
