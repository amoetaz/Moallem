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

# If you -keep class android.support.v7.widget.** { *; }keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class android.support.v7.widget.** { *; }
-ignorewarnings
-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.apache.**
-dontwarn org.w3c.dom.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}
-keep class com.moallem.stu.models.InitRequest** { <fields>; }
-keep class com.moallem.stu.models.InitTransition** { <fields>; }
-keep class com.moallem.stu.models.VerificationRequest** { <fields>; }
-keep class com.moallem.stu.models.VerificationResponse** { <fields>; }
-keep class com.moallem.stu.models.ResendPincodeRequest** { <fields>; }
-keep class com.moallem.stu.models.ResendPincodeResponse** { <fields>; }
-dontwarn javax.annotation.**
-dontwarn okhttp3.internal.platform.*
