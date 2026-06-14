import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    applyMyHierarchyTemplate()

    jvm("desktop")

    sourceSets {
        desktopMain.dependencies {
            implementation(projects.samples.shared)
        }

        desktopTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
    }
}

val appId = "com.github.panpf.sketch4.sample"
val appName = "Sketch4"
compose.desktop {
    application {
        mainClass = "com.github.panpf.sketch.sample.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = appName
            packageVersion = convertDesktopPackageVersion(property("versionName").toString())
            vendor = "panpfpanpf@outlook.com"
            description = "Sketch4 Image Loader Library Sample App"
            macOS {
                bundleID = appId
                iconFile.set(project.file("icons/icon-macos.icns"))
            }
            windows {
                iconFile.set(project.file("icons/icon-windows.ico"))
            }
            linux {
                iconFile.set(project.file("icons/icon-linux.png"))
            }
            modules(
                "jdk.unsupported",  // 'sun/misc/Unsafe' error
                "java.net.http",    // 'java/net/http/HttpClient$Version ' error
            )
        }
        buildTypes.release.proguard {
            obfuscate.set(true) // Obfuscate the code
            optimize.set(true) // proguard optimization, enabled by default
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}

tasks.configureEach {
    val targetTaskNames = listOf(
        "packageReleaseMsi", "packageReleaseExe",
        "packageReleaseDmg", "packageReleasePkg",
        "packageReleaseDeb", "packageReleaseRpm"
    )
    val targetExtensions = listOf(
        "msi", "exe",
        "dmg", "pkg",
        "deb", "rpm"
    )
    if (name in targetTaskNames) {
        doLast {
            val composeBinariesDir =
                project.layout.buildDirectory.dir("compose/binaries").get().asFile
            composeBinariesDir.walkTopDown()
                .filter { it.isFile && it.extension in targetExtensions }
                .forEach { file ->
                    val fileName = file.name
                    var newFileName = fileName
                    if (fileName.endsWith("deb") || fileName.endsWith("rpm")) {
                        // deb or rpm packages will convert all uppercase letters to lowercase by default, so case sensitivity must be ignored here.
                        val lowercaseAppName = appName.lowercase()
                        if (newFileName.contains(lowercaseAppName, ignoreCase = false)) {
                            newFileName = newFileName.replace(lowercaseAppName, "sketch-sample")
                        }

                        // sketch-sample_1.5.0001_amd64.deb -> sketch-sample-1.5.0001-amd64.deb
                        newFileName = newFileName.replace("_", "-")
                    } else {
                        if (newFileName.contains(appName, ignoreCase = false)) {
                            newFileName = newFileName.replace(appName, "sketch-sample")
                        }
                    }

                    if (newFileName != fileName) {
                        val newFile = file.parentFile.resolve(newFileName)
                        if (file.renameTo(newFile)) {
                            logger.lifecycle("Rename succedd. '$file' -> '${newFile.name}'")
                        } else {
                            logger.error("Rename failed. '$file'")
                        }
                    }
                }
        }
    }
}