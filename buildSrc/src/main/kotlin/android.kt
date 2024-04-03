import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

fun Project.androidLibrary(
    nameSpace: String,
    action: LibraryExtension.() -> Unit = {},
) = androidBase<LibraryExtension>(nameSpace) {
    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
    @Suppress("UnstableApiUsage")
    testOptions {
        targetSdk = project.targetSdk
    }
    action()
}

fun Project.androidApplication(
    nameSpace: String,
    applicationId: String = nameSpace,
    action: BaseAppModuleExtension.() -> Unit = {},
) = androidBase<BaseAppModuleExtension>(nameSpace) {
    defaultConfig {
        this.applicationId = applicationId
        this.versionCode = project.versionCode
        this.versionName = project.versionName
        this.vectorDrawables.useSupportLibrary = true
    }
    action()
}

private fun <T : BaseExtension> Project.androidBase(
    nameSpace: String,
    action: T.() -> Unit,
) {
    android<T> {
        this.namespace = nameSpace
        compileSdkVersion(project.compileSdk)
        defaultConfig {
            this.minSdk = project.minSdk
            this.targetSdk = project.targetSdk
            this.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        packagingOptions {
            resources.pickFirsts += listOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*kotlin_module",
            )
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
//        lint {
//            warningsAsErrors = true
//            disable += listOf(
//                "ComposableNaming",
//                "UnknownIssueId",
//                "UnsafeOptInUsageWarning",
//                "UnusedResources",
//                "UseSdkSuppress",
//                "VectorPath",
//                "VectorRaster",
//            )
//        }
        action()
    }
//    plugins.withId("org.jetbrains.kotlin.multiplatform") {
//        extensions.configure<KotlinMultiplatformExtension> {
//            sourceSets.configureEach {
//                languageSettings {
//                    optIn("com.github.panpf.sketch.annotation.DelicateCoilApi")
//                    optIn("com.github.panpf.sketch.annotation.ExperimentalCoilApi")
//                    optIn("com.github.panpf.sketch.annotation.InternalCoilApi")
//                }
//            }
//            targets.configureEach {
//                compilations.configureEach {
//                    compilerOptions.configure {
//                        val arguments = listOf(
//                            // https://kotlinlang.org/docs/compiler-reference.html#progressive
//                            "-progressive",
//                            // https://youtrack.jetbrains.com/issue/KT-61573
//                            "-Xexpect-actual-classes",
//                        )
//                        freeCompilerArgs.addAll(arguments)
//                    }
//                }
//            }
//        }
//    }
//    tasks.withType<KotlinCompile>().configureEach {
//        compilerOptions {
//            allWarningsAsErrors.set(System.getenv("CI").toBoolean())
//
//            val arguments = mutableListOf<String>()
//
//            // https://kotlinlang.org/docs/compiler-reference.html#progressive
//            arguments += "-progressive"
//
//            // Enable Java default method generation.
//            arguments += "-Xjvm-default=all"
//
//            // Generate smaller bytecode by not generating runtime not-null assertions.
//            arguments += "-Xno-call-assertions"
//            arguments += "-Xno-param-assertions"
//            arguments += "-Xno-receiver-assertions"
//
//            if (project.name != "benchmark") {
//                arguments += "-opt-in=com.github.panpf.sketch.annotation.DelicateCoilApi"
//                arguments += "-opt-in=com.github.panpf.sketch.annotation.ExperimentalCoilApi"
//                arguments += "-opt-in=com.github.panpf.sketch.annotation.InternalCoilApi"
//            }
//
//            freeCompilerArgs.addAll(arguments)
//        }
//    }
}

private fun <T : BaseExtension> Project.android(action: T.() -> Unit) {
    extensions.configure("android", action)
}

//private fun BaseExtension.lint(action: Lint.() -> Unit) {
//    (this as CommonExtension<*, *, *, *, *, *>).lint(action)
//}