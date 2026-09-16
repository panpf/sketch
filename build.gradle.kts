import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.abi.BinariesSource
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
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
    description = "Clean the root build directory."
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

    // Target JVM 11.
    afterEvaluate {
        tasks.withType<JavaCompile>().configureEach {
            sourceCompatibility = JavaVersion.VERSION_11.toString()
            targetCompatibility = JavaVersion.VERSION_11.toString()
            options.compilerArgs = options.compilerArgs + "-Xlint:-options"
        }
        tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions.jvmTarget = JvmTarget.JVM_11
        }
    }

    // 'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta. Consider using the '-Xexpect-actual-classes' flag to suppress this warning.
    // Also see: https://youtrack.jetbrains.com/issue/KT-61573
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            targets.configureEach {
                compilations.configureEach {
                    compileTaskProvider.configure {
                        compilerOptions {
                            freeCompilerArgs.addAll(listOf("-Xexpect-actual-classes"))
                        }
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
    val isPublishableModule =
        hasProperty("POM_ARTIFACT_ID")    // configured in the project/gradle.properties file
    if (isPublishableModule) {
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
                    .isNotEmpty()    // configured in the GitHub workflow env
                && System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyPassword").orEmpty()
                    .isNotEmpty()    // configured in the GitHub workflow env
            ) {
                signAllPublications()
            }
        }
    }

    // Configure Dokka plugin for all publishable library modules
    if (isPublishableModule) {
        apply { plugin("org.jetbrains.dokka") }
    }

    // Enable ABI validation for all publishable library modules.
    if (isPublishableModule) {
        configureAbiValidation()
        configureAndroidAbiValidation()
    }
}

@OptIn(ExperimentalAbiValidation::class)
fun Project.configureAbiValidation() {
    // TODO The abiValidation feature does not yet support kmp Android targets and pure Android modules. https://youtrack.jetbrains.com/issue/KT-78025
    afterEvaluate {
        val kotlinExtension =
            extensions.findByType<KotlinProjectExtension>() ?: return@afterEvaluate

        kotlinExtension.abiValidation {
            referenceDumpDir.set(layout.projectDirectory.dir("api"))
            binariesSource.set(BinariesSource.MAIN_COMPILATION)
        }
    }
}

fun Project.configureAndroidAbiValidation() {
    plugins.withId("com.android.library") {
        afterEvaluate {
            configureAndroidAbiValidation(
                subdirectoryName = "",
                dependencyTaskNames = listOf(
                    "compileReleaseKotlin",
                    "compileReleaseJavaWithJavac",
                ),
                classfiles = files(
                    layout.buildDirectory.dir("intermediates/built_in_kotlinc/release/compileReleaseKotlin/classes"),
                    layout.buildDirectory.dir("intermediates/javac/release/compileReleaseJavaWithJavac/classes"),
                ),
            )
        }
    }

    plugins.withId("com.android.kotlin.multiplatform.library") {
        afterEvaluate {
            configureAndroidAbiValidation(
                subdirectoryName = "android",
                dependencyTaskNames = listOf(
                    "compileAndroidMain",
                    "compileAndroidMainJavaWithJavac",
                ),
                classfiles = files(
                    layout.buildDirectory.dir("classes/kotlin/android/main"),
                    layout.buildDirectory.dir("classes/java/android/main"),
                    layout.buildDirectory.dir("intermediates/javac/androidMain/classes"),
                ),
            )
        }
    }
}

private fun Project.configureAndroidAbiValidation(
    subdirectoryName: String,
    dependencyTaskNames: List<String>,
    classfiles: FileCollection,
) {
    val dependencyTasks = tasks.matching { it.name in dependencyTaskNames }
    tasks.matching { it.name == "internalDumpKotlinAbi" }.configureEach {
        dependsOn(dependencyTasks)
        configureKotlinAbiDumpInput(subdirectoryName, classfiles)
    }
}

private fun Task.configureKotlinAbiDumpInput(
    subdirectoryName: String,
    classfiles: FileCollection,
) {
    val dumpTaskClass = javaClass
    val getJvm = runCatching { dumpTaskClass.getMethod("getJvm") }.getOrNull() ?: return
    val jvmProperty = getJvm.invoke(this)

    val propertyClass = jvmProperty.javaClass
    val get = propertyClass.getMethod("get")
    val set = propertyClass.getMethod("set", Iterable::class.java)

    val existingEntries = mutableListOf<Any>()
    val currentEntries = get.invoke(jvmProperty)
    if (currentEntries is Iterable<*>) {
        currentEntries.filterNotNullTo(existingEntries)
    } else {
        return
    }

    val entryClass = existingEntries.firstOrNull()?.javaClass
        ?: runCatching {
            dumpTaskClass.classLoader.loadClass(
                $$"org.jetbrains.kotlin.gradle.tasks.abi.KotlinAbiDumpTaskImpl$JvmTargetInfo",
            )
        }.getOrNull()
        ?: return

    val getSubdirectoryName = entryClass.getMethod("getSubdirectoryName")
    if (existingEntries.any { getSubdirectoryName.invoke(it) == subdirectoryName }) return

    val constructor = entryClass.getConstructor(String::class.java, FileCollection::class.java)
    existingEntries.add(constructor.newInstance(subdirectoryName, classfiles))
    set.invoke(jvmProperty, existingEntries)
}