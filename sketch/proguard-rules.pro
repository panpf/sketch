# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/xiaopan/Program/android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# 只有SketchGifDrawableImpl类与sketch-gif有联系，因此当缺失sketch-gif时SketchGifDrawableImpl类在混淆时会发出警告
-dontwarn me.xiaopan.sketch.drawable.SketchGifDrawableImpl
-dontwarn me.xiaopan.sketch.drawable.SketchGifDrawableImpl$1

# 实现了Initializer接口的类需要在AndroidManifest中配置，然后再运行时实例化，因此不能混淆
-keep public class * implements me.xiaopan.sketch.Initializer