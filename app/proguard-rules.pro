# Base Android configuration
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod
-keepattributes *Annotation*

# Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# AndroidX Lifecycle (ViewModels)
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Support for Java 8+ API Desugaring (CRITICAL for URLEncoder fix)
# This keeps the backported versions of modern Java classes
-keep class j$.** { *; }
-dontwarn j$.**

# NewPipe Extractor & Rhino Engine
# These rules ensure the extractor and its JS engine are not stripped
-keep class org.schabi.newpipe.** { *; }
-dontwarn org.schabi.newpipe.**
-keep class org.mozilla.javascript.** { *; }
-keep class org.mozilla.classfile.ClassFileWriter
-dontwarn org.mozilla.javascript.tools.**

# URL Handling & General Encoding
# Ensures that encoding methods aren't renamed or removed during obfuscation
-keepclassmembers class java.net.URLEncoder { *; }
-keepclassmembers class java.net.URLDecoder { *; }
-keepclassmembers class * {
    public static ** encode(...);
    public static ** decode(...);
}

# Koin (Dependency Injection)
-keep class io.insertkoin.** { *; }