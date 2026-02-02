import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

buildscript {
    repositories {
//        maven { setUrl("https://maven.aliyun.com/repository/public") }  // central、jcenter
//        maven { setUrl("https://maven.aliyun.com/repository/google") }  // google
//        maven { setUrl("https://repo.huaweicloud.com/repository/maven/") }    // central、google、jcenter
//        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        gradlePluginPortal()
        mavenCentral()
        google()
    }
    dependencies {
        classpath(libs.gradlePlugin.android)
        classpath(libs.gradlePlugin.androidxNavigationSafeArgs)
        classpath(libs.gradlePlugin.buildkonfig)
        classpath(libs.gradlePlugin.jetbrainsCompose)
        classpath(libs.gradlePlugin.kotlin)
        classpath(libs.gradlePlugin.kotlinComposeCompiler)
        classpath(libs.gradlePlugin.kotlinSerialization)
        classpath(libs.gradlePlugin.kotlinxAtomicfu)
        classpath(libs.gradlePlugin.kotlinxCover)
        classpath(libs.gradlePlugin.mavenPublish)
    }
}

plugins {
    alias(libs.plugins.dokka)
}

tasks.register("cleanRootBuild", Delete::class) {
    delete(rootProject.project.layout.buildDirectory.get().asFile.absolutePath)
}

// Aggregate dokka documentation for all submodules
dependencies {
    for (module in publicModules) {
        dokka(project(":$module"))
    }
}

allprojects {
    repositories {
//        maven { setUrl("https://maven.aliyun.com/repository/public") }  // central、jcenter
//        maven { setUrl("https://maven.aliyun.com/repository/google") }  // google
//        maven { setUrl("https://repo.huaweicloud.com/repository/maven/") }    // central、google、jcenter
        mavenCentral()
        google()
        maven { setUrl("https://www.jitpack.io") }
//        maven { setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots") }
//        mavenLocal()
    }

    // Minimum compatible Java version
    afterEvaluate {
        // Must be included in afterEvaluate to find the plugin
        // Compose Multiplatform 1.8.0 must use JVM target 11+, and Android View also requires 1.8+
        val (version, target) = if (plugins.findPlugin("org.jetbrains.kotlin.plugin.compose") != null) {
            JavaVersion.VERSION_11 to JvmTarget.JVM_11
        } else {
            JavaVersion.VERSION_1_8 to JvmTarget.JVM_1_8
        }
        tasks.withType<JavaCompile>().configureEach {
            sourceCompatibility = version.toString()
            targetCompatibility = version.toString()
            options.compilerArgs = options.compilerArgs + "-Xlint:-options"
        }
        tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions.jvmTarget = target
        }
    }

    // 'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta. Consider using the '-Xexpect-actual-classes' flag to suppress this warning.
    // Also see: https://youtrack.jetbrains.com/issue/KT-61573
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            targets.configureEach {
                compilations.configureEach {
                    compilerOptions.configure {
                        freeCompilerArgs.addAll(listOf<String>("-Xexpect-actual-classes"))
                    }
                }
            }
        }
    }

    // Can't dispatch to the main thread in native tests. https://youtrack.jetbrains.com/issue/KT-53129
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            targets.withType<KotlinNativeTarget> {
                if (konanTarget.family.isAppleFamily) {
                    binaries.withType<TestExecutable> {
                        freeCompilerArgs += listOf(
                            "-e",
                            "com.github.panpf.sketch.test.utils.mainBackground"
                        )
                    }
                }
            }
        }
    }

    // Add compilation configuration for Compose module
    plugins.withId("org.jetbrains.kotlin.plugin.compose") {
        extensions.configure<ComposeCompilerGradlePluginExtension> {
            stabilityConfigurationFiles.add {
                rootDir.resolve("sketch-core/compose_compiler_config.conf")
            }
        }
    }

    /*
     * Run the `./gradlew clean :sketch-compose:assembleRelease -PcomposeCompilerReports=true` command to generate a report,
     * which is located in the `project/module/build/compose_compiler` directory.
     *
     * Interpretation of the report: https://developer.android.com/jetpack/compose/performance/stability/diagnose#kotlin
     */
    if (project.findProperty("composeCompilerReports") == "true") {
        plugins.withId("org.jetbrains.kotlin.plugin.compose") {
            extensions.configure<ComposeCompilerGradlePluginExtension> {
                val outputDir = layout.buildDirectory.dir("compose_compiler").get().asFile
                metricsDestination = outputDir
                reportsDestination = outputDir
            }
        }
    }

    // jetbrains-compose bug https://youtrack.jetbrains.com/issue/CMP-5831
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlinx" && requested.name == "atomicfu") {
                useVersion(libs.versions.kotlinx.atomicfu.get())
            }
        }
    }

    // Uninstall test APKs after running instrumentation tests.
    tasks.configureEach {
        if (name == "connectedDebugAndroidTest") {
            finalizedBy("uninstallDebugAndroidTest")
        }
    }

    // Configure publish plugin for all publishable library modules
    if (
//        && hasProperty("mavenCentralUsername")    // configured in the ~/.gradle/gradle.properties file
//        && hasProperty("mavenCentralPassword")    // configured in the ~/.gradle/gradle.properties file
        hasProperty("versionName")    // configured in the rootProject/gradle.properties file
        && hasProperty("GROUP")    // configured in the rootProject/gradle.properties file
        && hasProperty("POM_ARTIFACT_ID")    // configured in the project/gradle.properties file
    ) {
        apply { plugin("com.vanniktech.maven.publish") }

        configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
            version = property("versionName").toString()
            if (hasProperty("signing.keyId")    // configured in the ~/.gradle/gradle.properties file
                && hasProperty("signing.password")    // configured in the ~/.gradle/gradle.properties file
                && hasProperty("signing.secretKeyRingFile")    // configured in the ~/.gradle/gradle.properties file
            ) {
                signAllPublications()
            } else if (
                System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey").orEmpty()
                    .isNotEmpty()    // configured in the github workflow env
                && System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyPassword").orEmpty()
                    .isNotEmpty()    // configured in the github workflow env
            ) {
                signAllPublications()
            }
        }
    }

    // Configure Dokka plugin for all publishable library modules
    if (hasProperty("POM_ARTIFACT_ID")) {   // configured in the module/gradle.properties file
        apply { plugin("org.jetbrains.dokka") }
    }
}