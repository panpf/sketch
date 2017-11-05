# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/sdk/tools/proguard/proguard-android.txt
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

# 只有 SketchGifDrawableImpl 类与 sketch-gif 有联系，因此当缺失 sketch-gif 时 SketchGifDrawableImpl 类在混淆时会发出警告
-dontwarn me.panpf.sketch.drawable.SketchGifDrawableImpl
-dontwarn me.panpf.sketch.drawable.SketchGifDrawableImpl$1

# 实现了 Initializer 接口的类需要在 AndroidManifest 中配置，然后在运行时实例化，因此不能混淆
-keep public class * implements me.panpf.sketch.Initializer