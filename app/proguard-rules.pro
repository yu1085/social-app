# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ByteRTC SDK ProGuard rules
-keep class com.ss.bytertc.** { *; }
-keep class com.bytedance.** { *; }
-keep class com.volcengine.** { *; }
-dontwarn com.ss.bytertc.**
-dontwarn com.bytedance.**
-dontwarn com.volcengine.**

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep RTC related classes
-keep class * extends com.ss.bytertc.engine.handler.** { *; }
-keep class * implements com.ss.bytertc.engine.handler.** { *; }

# AndroidX support library compatibility
-keep class androidx.core.content.ContextCompat { *; }
-keep class androidx.core.app.ActivityCompat { *; }