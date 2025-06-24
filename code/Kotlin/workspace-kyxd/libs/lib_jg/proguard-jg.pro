-dontoptimize
-dontpreverify

-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

