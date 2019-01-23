apply { from("kotlin-android")}
apply { from("kotlin-android-extensions")}

//androidExtensions {
//    experimental = true
//}

dependencies{
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${property("KOTLIN_VERSION")}")
    implementation("androidx.core:core-ktx:${property("ANDROIDX_CORE_KTX")}")
}