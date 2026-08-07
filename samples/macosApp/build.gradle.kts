import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.desktop.application.tasks.AbstractNativeMacApplicationPackageAppDirTask

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    applyMyHierarchyTemplate()

    macosArm64 {
        binaries.executable {
            entryPoint = "com.github.panpf.sketch.sample.main"
        }
    }

    sourceSets {
        macosMain {
            // Kotlin resources are not collected transitively for a native executable.
            resources.srcDirs("../../internal/images/src/appleMain/resources")

            dependencies {
                implementation(projects.samples.shared)
            }
        }
    }
}

val appId = "com.github.panpf.sketch.sample"
val appName = "Sketch4"

compose.desktop {
    nativeApplication {
        targets(kotlin.macosArm64())
        distributions {
            targetFormats(TargetFormat.Dmg)
            packageName = appName
            packageVersion = convertDesktopPackageVersion(property("versionName").toString())
            vendor = "panpfpanpf@outlook.com"
            description = "Sketch4 Image Loader Library Sample App"
            macOS {
                bundleID = appId
                // https://github.com/JetBrains/compose-multiplatform/blob/8ebd34efd881bfa9101cc81083ce5182b5fdc0e7/gradle-plugins/compose/src/main/kotlin/org/jetbrains/compose/desktop/application/tasks/AbstractNativeMacApplicationPackageAppDirTask.kt#L64-L65
                // The icon file in Contents/Resources has been hardcoded to "$packageName.icns".
                iconFile = project.file("icon/$appName.icns")
            }
        }
    }
}

// Compose 1.11 does not propagate nativeApplication.macOS.bundleID to the generated task.
tasks.withType<AbstractNativeMacApplicationPackageAppDirTask>().configureEach {
    bundleID = appId
}

listOf("Debug", "Release").forEach { buildType ->
    val buildTypeDirectory = buildType.lowercase()
    val linkTask = tasks.named("link${buildType}ExecutableMacosArm64")
    val copyResourcesTask = tasks.register<Sync>(
        "copyMacosArm64ResourcesTo${buildType}Executable"
    ) {
        description = "Copies macOS resources into the $buildType executable's output directory"
        dependsOn("macosArm64AggregateResources")
        mustRunAfter(linkTask)
        from(
            layout.buildDirectory.dir(
                "kotlin-multiplatform-resources/aggregated-resources/macosArm64"
            )
        )
        from(rootProject.file("internal/images/src/appleMain/resources"))
        into(
            layout.buildDirectory.dir(
                "bin/macosArm64/${buildTypeDirectory}Executable/compose-resources"
            )
        )
    }
    linkTask.configure {
        finalizedBy(copyResourcesTask)
    }
    tasks.named("run${buildType}ExecutableMacosArm64") {
        dependsOn(copyResourcesTask)
    }
}

tasks.configureEach {
    val targetTaskNames = listOf(
        "packageDmgNativeDebugMacosArm64", "packageDmgNativeReleaseMacosArm64",
        "packagePkgNativeDebugMacosArm64", "packagePkgNativeReleaseMacosArm64",
    )
    val targetExtensions = listOf(
        "dmg", "pkg",
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
                    if (newFileName.contains(appName, ignoreCase = false)) {
                        newFileName = newFileName.replace(appName, "sketch-sample-native")
                    }

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
