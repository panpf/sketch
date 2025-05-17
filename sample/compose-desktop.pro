# ----------------------------------------- Basic ------------------------------------------------ #
-keepattributes *Annotation*

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


# ----------------------------------------- Okio ------------------------------------------------- #
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
-keep class okio.** { *; }


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
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { volatile <fields>; }
-keep class io.ktor.client.engine.cio.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**

# Obfuscation breaks coroutines/ktor for some reason
-dontobfuscate


# ----------------------------------------- App Ruls --------------------------------------------- #
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

# ----------------------------------------- Sketch Privider --------------------------------------------- #
-keep class * implements com.github.panpf.sketch.util.DecoderProvider { *; }
-keep class * implements com.github.panpf.sketch.util.FetcherProvider { *; }