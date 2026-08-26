# Add project specific ProGuard rules here.
# Live Human AI - Release Configuration

# Keep native methods for JNI
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep JNI classes
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.examples.android.model.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# OkHttp
dontwarn okhttp3.**
dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Keep model classes
-keep class com.livehumanai.data.model.** { *; }
-keep class com.livehumanai.domain.model.** { *; }

# Native runtime
-keep class com.livehumanai.native.** { *; }
