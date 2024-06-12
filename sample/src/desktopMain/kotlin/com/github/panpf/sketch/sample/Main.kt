package com.github.panpf.sketch.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScaleTransition
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.GifSkiaAnimatedDecoder
import com.github.panpf.sketch.decode.WebpSkiaAnimatedDecoder
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.http.KtorStack
import com.github.panpf.sketch.http.OkHttpStack
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.ui.MyEvents
import com.github.panpf.sketch.sample.ui.gallery.HomeScreen
import com.github.panpf.sketch.sample.ui.theme.AppTheme
import com.github.panpf.sketch.sample.ui.util.PexelsCompatibleRequestInterceptor
import com.github.panpf.sketch.sample.util.sha256String
import com.github.panpf.sketch.util.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import java.io.File

const val appId = "com.github.panpf.sketch4.sample"

fun main() {
    initialSketch()
    application {
        val coroutineScope = rememberCoroutineScope()
        Window(
            title = "Sketch4",
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(size = DpSize(1000.dp, 800.dp)),
            onKeyEvent = {
                coroutineScope.launch {
                    MyEvents.keyEvent.emit(it)
                }
                false
            }
        ) {
            AppTheme {
                Box(Modifier.fillMaxSize()) {
                    Navigator(HomeScreen) { navigator ->
                        ScaleTransition(navigator = navigator)
                    }

                    val snackbarHostState = remember { SnackbarHostState() }
                    SnackbarHost(
                        snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
                    )
                    LaunchedEffect(Unit) {
                        MyEvents.toastFlow.collect {
                            snackbarHostState.showSnackbar(it)
                        }
                    }
                }
            }

            val context = LocalPlatformContext.current
            LaunchedEffect(Unit) {
                MyEvents.savePhotoFlow.collect {
                    savePhoto(SingletonSketch.get(context), it)
                }
            }
            LaunchedEffect(Unit) {
                MyEvents.sharePhotoFlow.collect {
                    sharePhoto(SingletonSketch.get(context), it)
                }
            }
        }
    }
}

private fun initialSketch() {
    val context = PlatformContext.INSTANCE
    val appSettings = context.appSettings
    SingletonSketch.setSafe {
        Sketch.Builder(context).apply {
            val httpStack = when (appSettings.httpClient.value) {
                "Ktor" -> KtorStack()
                "OkHttp" -> OkHttpStack.Builder().build()
                "HttpURLConnection" -> HurlStack.Builder().build()
                else -> throw IllegalArgumentException("Unknown httpClient: ${appSettings.httpClient.value}")
            }
            httpStack(httpStack)
            components {
                addRequestInterceptor(PexelsCompatibleRequestInterceptor())
                supportSvg()
                addDecoder(GifSkiaAnimatedDecoder.Factory())
                addDecoder(WebpSkiaAnimatedDecoder.Factory())
            }
            // To be able to print the Sketch initialization log
            logger(level = appSettings.logLevel.value)
        }.build().apply {
            @Suppress("OPT_IN_USAGE")
            GlobalScope.launch {
                appSettings.logLevel.collect {
                    logger.level = it
                }
            }
        }
    }
}

private suspend fun savePhoto(sketch: Sketch, imageUri: String) {
    val fetcher = withContext(Dispatchers.IO) {
        sketch.components.newFetcherOrThrow(ImageRequest(sketch.context, imageUri))
    }
    if (fetcher is FileUriFetcher) {
        return MyEvents.toastFlow.emit("Local files do not need to be saved")
    }

    val fetchResult = withContext(Dispatchers.IO) {
        fetcher.fetch()
    }.let {
        it.getOrNull()
            ?: return MyEvents.toastFlow.emit("Failed to save picture: ${it.exceptionOrNull()!!.message}")
    }
    val userHomeDir = File(System.getProperty("user.home"))
    val userPicturesDir = File(userHomeDir, "Pictures")
    val outDir = File(userPicturesDir, "sketch4").apply { mkdirs() }
    val fileExtension = MimeTypeMap.getExtensionFromUrl(imageUri)
        ?: MimeTypeMap.getExtensionFromMimeType(fetchResult.mimeType ?: "")
        ?: "jpeg"
    val imageFile = File(outDir, "${imageUri.sha256String()}.$fileExtension")
    val result = withContext(Dispatchers.IO) {
        runCatching {
            fetchResult.dataSource.openSource().buffer().use { input ->
                imageFile.outputStream().sink().buffer().use { output ->
                    output.writeAll(input)
                }
            }
        }
    }
    return if (result.isSuccess) {
        MyEvents.toastFlow.emit("Saved to the '${imageFile.parentFile?.path}' directory")
    } else {
        val exception = result.exceptionOrNull()
        MyEvents.toastFlow.emit("Failed to save picture: ${exception?.message}")
    }
}

@Suppress("UNUSED_PARAMETER")
private suspend fun sharePhoto(sketch: Sketch, imageUri: String) {
    MyEvents.toastFlow.emit("Desktop platform does not support sharing photo")
}