# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html


# ----------------------------------------- OkHttp ----------------------------------------------- #
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**


# ----------------------------------------- Okio ------------------------------------------------- #
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
-keep class okio.** { *; }


# ----------------------------------------- kotlinx serialization -------------------------------- #
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers public class **$$serializer {
    private ** descriptor;
}


# ----------------------------------------- ktor ------------------------------------------------- #
-dontwarn org.slf4j.**


# ----------------------------------------- FFmpegMediaMetadataRetriever ------------------------- #
-keep public class wseemann.media.**{*;}


# ----------------------------------------- App Rules -------------------------------------------- #
# Change here com.github.panpf.sketch.sample
-keepclassmembers @kotlinx.serialization.Serializable class com.github.panpf.sketch.sample.** {
    # lookup for plugin generated serializable classes
    *** Companion;
    # lookup for serializable objects
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
# lookup for plugin generated serializable classes
-if @kotlinx.serialization.Serializable class com.github.panpf.sketch.sample.**
-keepclassmembers class com.github.panpf.sketch.sample.<1>$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}

# createViewBinding
-keep class com.github.panpf.sketch.sample.ui.base.BaseBindingActivity
-keep class * extends com.github.panpf.sketch.sample.ui.base.BaseBindingActivity
-keep class com.github.panpf.sketch.sample.ui.base.BaseBindingDialogFragment
-keep class * extends com.github.panpf.sketch.sample.ui.base.BaseBindingDialogFragment
-keep class com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
-keep class * extends com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
-keep class com.github.panpf.sketch.sample.ui.base.BaseBindingPermissionFragment
-keep class * extends com.github.panpf.sketch.sample.ui.base.BaseBindingPermissionFragment
-keep class com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
-keep class * extends com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
-keep class com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory
-keep class * extends com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory
-keep class * implements androidx.viewbinding.ViewBinding{
    public *;
}