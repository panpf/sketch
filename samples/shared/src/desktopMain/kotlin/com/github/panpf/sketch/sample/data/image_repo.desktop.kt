package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.mp.KoinPlatform
import java.io.File
import java.util.Locale

val acceptImageExtensions = setOf("jpeg", "jpg", "png", "gif", "webp", "bmp", "heic", "heif", "svg")

actual suspend fun localImages(context: PlatformContext): List<String> =
    withContext(ioCoroutineDispatcher()) {
        val userHomeDir = File(System.getProperty("user.home"))
        val userPicturesDir = File(userHomeDir, "Pictures")

        val appSettings: AppSettings = KoinPlatform.getKoin().get()
        val localPhotosDir = appSettings.localPhotosDirPath.value
            .takeIf { it.isNotEmpty() }?.let { File(it) }
        listOfNotNull(localPhotosDir, userPicturesDir)
            .flatMap { dir ->
                dir.walkTopDown()
                    .filter { it.isFile }
                    .filter { !it.name.startsWith(".") }
                    .filter {
                        val extension = it.extension
                        acceptImageExtensions.contains(extension.lowercase(Locale.getDefault()))
                    }
                    .map { it.path }.toList()
            }
    }