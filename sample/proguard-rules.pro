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
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-dontwarn me.xiaopan.android.inject.***

# 不混淆SearchView，因为发现混淆的时候会把SearchView的构造函数给弄丢了，导致无法实例化
-keep class android.support.v7.widget.SearchView { *; }

# 不混淆所有的枚举类，防止使用枚举类的名字来匹配时出问题
-keep enum * {*;}

##---------------Begin: proguard configuration for Gson  ----------
-keep class com.google.**{*;}

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }

-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
# 这里需要改成自己项目里的javabean
#-keep class com.google.gson.examples.android.model.** { *; }

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class * implements java.io.Serializable {*;}
##---------------End: proguard configuration for Gson  ----------

-keep public class com.tencent.bugly.**{*;}


