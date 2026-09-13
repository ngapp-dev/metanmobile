-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
   static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
   static **$* *;
}
-keepclassmembers class <2>$<3> {
   kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
   public static ** INSTANCE;
}
-keepclassmembers class <1> {
   public static <1> INSTANCE;
   kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

-keep class com.ngapp.metanmobile.core.datastore.UserPreferences { *; }
-keep class com.ngapp.metanmobile.core.datastore.* { *; }
-keepclassmembers class com.ngapp.metanmobile.core.datastore.UserPreferences { public *; }

-dontwarn android.media.AudioTrack$StreamEventCallback

# Jetpack Compose, Coroutines and Coil each ship their own
# consumer-rules.txt with exactly the -keep rules they need, so blanket
# "-keep class x.** { *; }" rules for them here only disable shrinking,
# obfuscation and optimization for those trees without protecting anything
# that isn't already protected - this was the main cause of the low R8
# optimization/obfuscation/compression percentages reported for 2.3.1.
-dontwarn androidx.compose.**

-dontwarn kotlinx.coroutines.**

-dontwarn coil.**

# WorkManager Workers (в том числе наследование и reflection)
-keep class com.ngapp.metanmobile.sync.workers.** extends androidx.work.Worker { *; }
-keep class com.ngapp.metanmobile.sync.workers.** extends androidx.work.CoroutineWorker { *; }

# Конструкторы
-keepclassmembers class com.ngapp.metanmobile.sync.workers.** {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
