# Android ProGuard rules for Live Human AI
# Add project-specific ProGuard rules here.

# Basic ProGuard rules for Android
-keep class androidx.** { *; }
-keep class android.** { *; }
-keep class com.google.** { *; }

# Keep all activities, services, and receivers
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver

# Keep all ViewModels
-keep class * extends androidx.lifecycle.ViewModel

# Keep all Hilt components
-keep class * extends dagger.hilt.** { *; }
-keep class * implements dagger.hilt.** { *; }

# Keep Room database classes
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.Database
-keep class * extends androidx.room.Entity
-keep class * extends androidx.room.Dao

# Keep Retrofit and OkHttp classes
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class com.squareup.okhttp.** { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep Jetpack Compose classes
-keep class androidx.compose.** { *; }

# Keep CameraX classes
-keep class androidx.camera.** { *; }

# Keep WorkManager classes
-keep class androidx.work.** { *; }

# Keep DataStore classes
-keep class androidx.datastore.** { *; }

# Keep Navigation classes
-keep class androidx.navigation.** { *; }

# Keep native JNI classes
-keep class * extends java.lang.Object {
    public native * *(...);
}

# Keep native library names
-keep class * {
    public native <methods>;
}

# Keep R classes
-keep class **.R$* { *; }

# Keep data binding classes
-keep class **.BR { *; }
-keep class **Databinding* { *; }

# Keep Parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep WebView classes
-keep class android.webkit.** { *; }

# Keep for enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep for lambda expressions
-keepclassmembers class * {
    public <init>(...);
    public synthetic ** lambda*(...);
}

# Keep for serialization
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
}

# Keep for GSON
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep for OpenCV (if used)
-keep class org.opencv.** { *; }

# Keep for llama.cpp and whisper.cpp (if used directly)
-keep class com.livehumanai.livehumanai.native.** { *; }

# Don't warn about unused code in these packages
-dontwarn android.**
-dontwarn androidx.**
-dontwarn com.google.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn org.opencv.**

# Ignore warnings about missing classes
-ignorewarnings

# Optimize and obfuscate
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable

# Keep annotations
-keepattributes *Annotation*

# Keep signature information
-keepattributes Signature

# Keep inner classes
-keepclassmembers class * {
    public <init>(...);
    public static ** get*();
    public static ** set*(...);
}
