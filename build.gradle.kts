plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.org.jetbrains.kotlin.serialization) apply false
    alias(libs.plugins.org.jetbrains.kotlin.parcelize) apply false
    alias(libs.plugins.mavenpublish) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

allprojects {
    /**
     * publish config
     */
    if (hasProperty("signing.keyId")    // configured in the ~/.gradle/gradle.properties file
        && hasProperty("signing.password")    // configured in the ~/.gradle/gradle.properties file
        && hasProperty("signing.secretKeyRingFile")    // configured in the ~/.gradle/gradle.properties file
        && hasProperty("mavenCentralUsername")    // configured in the ~/.gradle/gradle.properties file
        && hasProperty("mavenCentralPassword")    // configured in the ~/.gradle/gradle.properties file
        && hasProperty("versionName")    // configured in the rootProject/gradle.properties file
        && hasProperty("GROUP")    // configured in the rootProject/gradle.properties file
        && hasProperty("POM_ARTIFACT_ID")    // configured in the project/gradle.properties file
    ) {
        apply { plugin("com.vanniktech.maven.publish") }

        configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
            version = property("versionName").toString()
        }
    }
}
