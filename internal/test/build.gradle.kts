plugins {
    id("com.android.kotlin.multiplatform.library")
    id("com.codingfeline.buildkonfig")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
}

addMultiplatformTargets(KmpTarget.entries.toTypedArray())
kmpAndroidLibrary(nameSpace = "com.github.panpf.sketch.test")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
            api(projects.internal.images)
            api(libs.kotlin.test)
            api(libs.kotlinx.coroutines.test)
            api(libs.kotlinx.datetime)
            api(libs.okio.fakefilesystem)
        }
        jvmCommonMain.dependencies {
            api(libs.kotlin.test.junit)
            api(libs.kotlin.reflect)
            api(libs.panpf.tools4j.reflect)
            api(libs.panpf.tools4j.security)
        }
        androidMain.dependencies {
            api(libs.androidx.fragment)
            api(libs.androidx.test.runner)
            api(libs.androidx.test.rules)
            api(libs.androidx.test.ext.junit)
            api(libs.panpf.tools4a.device)
            api(libs.panpf.tools4a.dimen)
            api(libs.panpf.tools4a.display)
            api(libs.panpf.tools4a.network)
            api(libs.panpf.tools4a.run)
            api(libs.panpf.tools4a.test)
        }
        desktopMain.dependencies {
            api(skikoAwtRuntimeDependency(libs.versions.skiko.get()))
            api(libs.appdirs)
        }
    }
}

// The ios, js, wasmJs test running environment and the gradle environment are isolated.
// It is impossible to judge whether it is running in GitHub Actions through environment variables.
// Therefore, the environment variables are read in gradle first,
// and the results are passed to the code for use through BuildKonfig.
val isGitHubActions = System.getenv("GITHUB_ACTIONS") == "true"
buildkonfig {
    packageName = "com.github.panpf.zoomimage.test.core"
    defaultConfigs {
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN,
            name = "IS_GITHUB_ACTIONS",
            value = isGitHubActions.toString(),
            const = true
        )
    }
}