package com.github.panpf.sketch.sample

import android.annotation.SuppressLint
import android.os.Build
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.FFmpegVideoFrameDecoderProvider
import com.github.panpf.sketch.decode.internal.GifDecoderProvider
import com.github.panpf.sketch.decode.internal.KoralGifDecoderProvider
import com.github.panpf.sketch.decode.internal.VideoFrameDecoderProvider
import com.github.panpf.sketch.decode.supportFFmpegVideoFrame
import com.github.panpf.sketch.decode.supportImageDecoderGif
import com.github.panpf.sketch.decode.supportKoralGif
import com.github.panpf.sketch.decode.supportMovieGif
import com.github.panpf.sketch.decode.supportVideoFrame
import com.github.panpf.sketch.fetch.internal.HurlHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.KtorHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.OkHttpHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.supportHurlHttpUri
import com.github.panpf.sketch.fetch.supportKtorHttpUri
import com.github.panpf.sketch.fetch.supportOkHttpHttpUri
import com.github.panpf.sketch.sample.ui.gallery.PhotoActionViewModel
import com.github.panpf.sketch.sample.ui.gallery.PhotoPaletteViewModel
import com.github.panpf.sketch.sample.ui.setting.AppSettingsViewModel
import com.github.panpf.sketch.sample.ui.test.DrawableScaleTypeViewModel
import com.github.panpf.sketch.sample.ui.test.LocalVideoListViewModel
import com.github.panpf.sketch.sample.ui.test.ProgressIndicatorTestViewModel
import com.github.panpf.sketch.sample.ui.test.transform.BlurTransformationTestViewModel
import com.github.panpf.sketch.sample.ui.test.transform.CircleCropTransformationTestViewModel
import com.github.panpf.sketch.sample.ui.test.transform.MaskTransformationTestViewModel
import com.github.panpf.sketch.sample.ui.test.transform.MultiTransformationTestViewModel
import com.github.panpf.sketch.sample.ui.test.transform.RotateTransformationTestViewModel
import com.github.panpf.sketch.sample.ui.test.transform.RoundedCornersTransformationTestViewModel
import com.github.panpf.sketch.sample.util.PenfeizhouAnimatedWebpDecoder
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

actual fun initialApp(context: PlatformContext) {
    startKoin {
        androidContext(context)
        modules(commonModule(context))
        modules(platformModule(context))
    }

    handleSSLHandshake()
}

actual fun platformModule(context: PlatformContext): Module = module {
    viewModel { AppSettingsViewModel(sketch = get(), appSettings = get(), page = it.get()) }
    viewModelOf(::LocalVideoListViewModel)
    viewModelOf(::DrawableScaleTypeViewModel)
    viewModelOf(::ProgressIndicatorTestViewModel)
    viewModelOf(::BlurTransformationTestViewModel)
    viewModelOf(::MaskTransformationTestViewModel)
    viewModelOf(::MultiTransformationTestViewModel)
    viewModelOf(::RotateTransformationTestViewModel)
    viewModelOf(::CircleCropTransformationTestViewModel)
    viewModelOf(::RoundedCornersTransformationTestViewModel)
    viewModelOf(::PhotoActionViewModel)
    viewModelOf(::PhotoPaletteViewModel)
}

actual fun Sketch.Builder.platformSketchInitial(context: PlatformContext) {
    addIgnoreFetcherProvider(
        KtorHttpUriFetcherProvider::class,
        OkHttpHttpUriFetcherProvider::class,
        HurlHttpUriFetcherProvider::class
    )

    addIgnoreDecoderProvider(
        VideoFrameDecoderProvider::class,
        FFmpegVideoFrameDecoderProvider::class
    )

    addIgnoreDecoderProvider(
        KoralGifDecoderProvider::class,
        GifDecoderProvider::class
    )

    val appSettings: AppSettings = KoinPlatform.getKoin().get()
    addComponents {
        when (val httpClient = appSettings.httpClient.value) {
            "Ktor" -> supportKtorHttpUri()
            "OkHttp" -> supportOkHttpHttpUri()
            "HttpURLConnection" -> supportHurlHttpUri()
            else -> throw IllegalArgumentException("Unknown httpClient: $httpClient")
        }

        when (appSettings.videoFrameDecoder.value) {
            "FFmpeg" -> supportFFmpegVideoFrame()
            "AndroidBuiltIn" -> supportVideoFrame()
            else -> throw IllegalArgumentException("Unknown videoFrameDecoder: ${appSettings.videoFrameDecoder.value}")
        }

        when (appSettings.gifDecoder.value) {
            "KoralGif" -> supportKoralGif()
            "Movie" -> supportMovieGif()
            "Movie+ImageDecoder" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) supportImageDecoderGif() else supportMovieGif()
            else -> throw IllegalArgumentException("Unknown animatedDecoder: ${appSettings.gifDecoder.value}")
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            addDecoder(PenfeizhouAnimatedWebpDecoder.Factory())
        }
    }
}


/**
 * for api.pexels.com on Android 5.0
 */
private fun handleSSLHandshake() {
    try {
        val trustAllCerts = arrayOf<TrustManager>(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(
                    certs: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(
                    certs: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }
            })
        val sc = SSLContext.getInstance("TLS")
        // trustAllCerts trust all certificates
        sc.init(null, trustAllCerts, SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}