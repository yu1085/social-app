plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.compose")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // 阿里云融合认证SDK - 一键登录
    implementation(files("libs/fusionAuthSDK_APP_Android_v1.2.12_operator_ui_log_static/SDK/fusionauth-1.2.12-online-release.aar"))
    implementation(files("libs/fusionAuthSDK_APP_Android_v1.2.12_operator_ui_log_static/SDK/umeng-asm/umeng-asms-v1.8.0.aar"))
    implementation(files("libs/fusionAuthSDK_APP_Android_v1.2.12_operator_ui_log_static/SDK/umeng-common/umeng-common-9.5.6.aar"))
    
    // 相机和图像处理依赖
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    
    // 阿里云官方Android SDK v2.3.40
    implementation(files("libs/aliyun-base-2.3.40.250909104131.aar"))
    implementation(files("libs/aliyun-facade-2.3.40.250909104131.aar"))
    implementation(files("libs/aliyun-face-2.3.40.250909104131.aar"))
    implementation(files("libs/aliyun-faceaudio-2.3.40.250909104131.aar"))
    implementation(files("libs/aliyun-facelanguage-2.3.40.250909104131.aar"))
    implementation(files("libs/aliyun-facequality-2.3.40.250909104131.aar"))
    implementation(files("libs/aliyun-nfc-2.3.40.250909104131.aar"))
    implementation(files("libs/aliyun-ocr-2.3.40.250909104131.aar"))
    implementation(files("libs/aliyun-wishverify-2.3.40.250909104131.aar"))
    implementation(files("libs/Android-AliyunFaceGuard-10057.aar"))
    implementation(files("libs/APSecuritySDK-deepSec-7.0.1.20250411.jiagu.aar"))
    
    // HTTP客户端
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("androidx.compose.runtime:runtime-livedata")
    
    // Material3 for XML themes
    implementation("com.google.android.material:material:1.10.0")
    
    // 网络请求依赖
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    
    
    // 支付宝SDK依赖 - 暂时注释掉，因为无法从Maven仓库下载
    // implementation("com.alipay.sdk:alipay-sdk-android:15.8.11")
    // implementation("com.alipay.sdk:alipay-sdk-java:4.38.10.ALL")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}