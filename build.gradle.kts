buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    dependencies {
        classpath(libs.gradlePlugin.kotlin)
        classpath(libs.gradlePlugin.kotlinSerialization)
//        classpath(libs.gradlePlugin.kotlinParcelize)
        classpath(libs.gradlePlugin.kotlinxAtomicfu)
        classpath(libs.gradlePlugin.android)
        classpath(libs.gradlePlugin.androidxNavigationSafeArgs)
        classpath(libs.gradlePlugin.jetbrainsCompose)
        classpath(libs.gradlePlugin.mavenPublish)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.project.layout.buildDirectory.get().asFile.absolutePath)
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

/**
 * Run the `./gradlew assembleRelease -PcomposeCompilerReports=true` command to generate a report,
 * which is located in the `project/module/build/compose_compiler` directory.
 *
 * Interpretation of the report: https://developer.android.com/jetpack/compose/performance/stability/diagnose#kotlin
 */
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            // Enable Compose Compiler Report
            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.layout.buildDirectory.get().asFile.absolutePath}/compose_compiler"
                )
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.layout.buildDirectory.get().asFile.absolutePath}/compose_compiler"
                )
            }
        }
    }
}