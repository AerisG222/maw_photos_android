# https://proguard-rules.blogspot.com/2017/05/jackson-proguard-rules.html
# Jackson
-keep @com.fasterxml.jackson.annotation.JsonIgnoreProperties class * { *; }
-keep class com.fasterxml.** { *; }
-keep class org.codehaus.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepclassmembers public final enum com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility {
    public static final com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility *;
}

# General
-keepattributes SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,Signature,Exceptions,InnerClasses


## https://github.com/FasterXML/jackson-docs/wiki/JacksonOnAndroid
#-keep class com.fasterxml.jackson.databind.ObjectMapper {
#    public <methods>;
#    protected <methods>;
#}
#
#-keep class com.fasterxml.jackson.databind.ObjectWriter {
#    public ** writeValueAsString(**);
#}
#
#-keepnames class com.fasterxml.jackson.** { *; }
#
-dontwarn com.fasterxml.jackson.databind.**
#
#-keepclassmembers class * {
#     @com.fasterxml.jackson.annotation.* *;
#}
