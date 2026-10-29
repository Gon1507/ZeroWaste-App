# Regras ProGuard/R8 do projeto.
# kotlinx.serialization: manter @Serializable
-keepattributes *Annotation*, InnerClasses
-keep,includedescriptorclasses class com.example.zerowaste.**$$serializer { *; }
-keepclassmembers class com.example.zerowaste.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.zerowaste.** {
    kotlinx.serialization.KSerializer serializer(...);
}
