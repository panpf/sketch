import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.org.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.org.jetbrains.compose)
}

group = property("GROUP").toString()
version = property("versionName").toString()

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":sample-common"))
                implementation(project(":sketch-resources"))
                implementation(project(":sketch-compose-core"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = property("GROUP").toString()
            packageVersion = property("versionName").toString().let {
                if (it.contains("-")) {
                    it.substring(0, it.indexOf("-"))
                } else {
                    it
                }
            }
        }
    }
}