# http://stackoverflow.com/questions/11029836/problems-with-using-jackson-json-library-when-using-proguard
# http://sourceforge.net/p/proguard/discussion/182456/thread/e4d73acf
# http://stackoverflow.com/questions/4830474/how-to-keep-exclude-a-particular-package-path-when-using-proguard
-keepattributes Signature,*Annotation*,Exceptions,InnerClasses,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

-keep class us.mikeandwan.photos.models.** {
    *;
}

-keep class com.fasterxml.jackson.** {
    *;
}
