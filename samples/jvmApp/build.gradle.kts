import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    applyMyHierarchyTemplate()

    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(projects.samples.shared)
        }

        jvmTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.github.panpf.sketch.sample.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = project.sampleAppName
            packageVersion = convertDesktopPackageVersion(property("versionName").toString())
            vendor = "panpfpanpf@outlook.com"
            description = "Sketch Image Loader Library Sample App"
            macOS {
                bundleID = project.sampleAppId
                iconFile.set(project.file("icons/icon-macos.icns"))
            }
            windows {
                iconFile.set(project.file("icons/icon-windows.ico"))
                menu = true
                shortcut = true
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
            configurationFiles.from(project.file("proguard-jvm.pro"))
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
                    /*
                     * Sketch Sample-4.7.0012.msi, Sketch Sample-4.7.0012.exe
                     * Sketch Sample-4.7.0012.dmg, Sketch Sample-4.7.0012.pkg
                     * sketch-sample_4.7.0012_amd64.deb, sketch-sample_4.7.0012_amd64.rpm
                     */
                    val fileName = file.name
                    var newFileName = fileName.replace(oldValue = "_", newValue = "-")
                    val (oldValue, platformType) = when (file.extension) {
                        "msi", "exe" -> project.sampleAppName to "-windows"
                        "dmg", "pkg" -> project.sampleAppName to "-macos"
                        "deb", "rpm" -> project.sampleAppName.lowercase()
                            .replace(" ", "-") to "-linux"

                        else -> throw IllegalArgumentException("Unsupported file extension: ${file.extension}")
                    }
                    newFileName = newFileName.replace(
                        oldValue = oldValue,
                        newValue = "sketch-sample-jvm${platformType}",
                        ignoreCase = true
                    )
                    if (newFileName != fileName) {
                        val newFile = file.parentFile.resolve(newFileName)
                        if (file.renameTo(newFile)) {
                            logger.lifecycle("Rename successful. '$file' -> '${newFile.name}'")
                        } else {
                            logger.error("Rename failed. '$file'")
                        }
                    }
                }
        }
    }
}