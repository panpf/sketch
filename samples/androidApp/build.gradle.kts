plugins {
    id("com.android.application")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlinx.atomicfu")
    id("androidx.navigation.safeargs.kotlin")
}

androidApplication(
    nameSpace = "com.github.panpf.sketch.sample",
    applicationId = "com.github.panpf.sketch4.sample"
) {
    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"${property("versionName").toString()}\"")
        buildConfigField("int", "VERSION_CODE", property("versionCode").toString())
    }
    signingConfigs {
        create("sample") {
            storeFile = project.file("sample.keystore")
            storePassword = "B027HHiiqKOMYesQ"
            keyAlias = "panpf-sample"
            keyPassword = "B027HHiiqKOMYesQ"
        }
    }
    buildTypes {
        debug {
            multiDexEnabled = true
            signingConfig = signingConfigs.getByName("sample")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("sample")
        }
    }

    flavorDimensions.add("default")

    androidResources {
        noCompress.add("bmp")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

//    applicationVariants.all {
//        val variant = this
//        variant.outputs.all {
//            val output = this
//            if (output is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
//                output.outputFileName =
//                    "sketch-sample-${variant.name}-${variant.versionName}.apk"
//            }
//        }
//    }
}

dependencies {
    implementation(projects.samples.shared)

    implementation(projects.sketchExtensionsView)
    implementation(projects.sketchViewKoin)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.google.material)
    implementation(libs.google.flexbox)
    implementation(libs.panpf.assemblyadapter4.pager2)
    implementation(libs.panpf.assemblyadapter4.recycler)
    implementation(libs.panpf.assemblyadapter4.recycler.paging)
    implementation(libs.panpf.zoomimage.view)
    implementation(libs.penfeizhou.animation.awebp)

    debugImplementation(libs.leakcanary)

    androidTestImplementation(projects.internal.test)
    androidTestImplementation(projects.internal.testSingleton)
}