# https://github.com/FasterXML/jackson-docs/wiki/JacksonOnAndroid
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}

-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}

-keepnames class com.fasterxml.jackson.** { *; }

-dontwarn com.fasterxml.jackson.databind.**

-keepclassmembers class * {
     @com.fasterxml.jackson.annotation.* *;
}
