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

-keep class com.google.android.gms.** { *; }
-dontwarn android.media.AudioTrack$StreamEventCallback

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-dontwarn androidx.compose.**

# Hilt (Dependency Injection)
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class com.google.dagger.** { *; }
-keepclassmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
-dontwarn dagger.hilt.**

# Kotlin Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Coil (Image loading)
-keep class coil.** { *; }
-dontwarn coil.**


# Hilt и Hilt Worker
-keep class ** extends androidx.work.Worker { *; }
-keep class ** extends androidx.work.CoroutineWorker { *; }

-keepclassmembers class * {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# Dagger/Hilt annotations and entry points
-keep class dagger.hilt.EntryPoint { *; }
-keep class dagger.Module { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.Module class * { *; }

# WorkManager Workers (в том числе наследование и reflection)
-keep class com.ngapp.metanmobile.sync.workers.** extends androidx.work.Worker { *; }
-keep class com.ngapp.metanmobile.sync.workers.** extends androidx.work.CoroutineWorker { *; }

# Конструкторы
-keepclassmembers class com.ngapp.metanmobile.sync.workers.** {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}