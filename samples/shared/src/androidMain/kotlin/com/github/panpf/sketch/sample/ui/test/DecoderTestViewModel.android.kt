package com.github.panpf.sketch.sample.ui.test

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.ApkIconDecoder
import com.github.panpf.sketch.decode.ImageDecoderAnimatedHeifDecoder
import com.github.panpf.sketch.decode.ImageDecoderAnimatedWebpDecoder
import com.github.panpf.sketch.decode.ImageDecoderGifDecoder
import com.github.panpf.sketch.decode.KoralGifDecoder
import com.github.panpf.sketch.decode.MovieGifDecoder
import com.github.panpf.sketch.decode.SvgDecoder.Factory
import com.github.panpf.sketch.decode.VideoFrameDecoder
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.sample.compose.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("NewApi")
actual suspend fun buildDecoderTestItems(
    context: PlatformContext
): List<DecoderTestItem> = buildList {
    add(DecoderTestItem(name = "JPEG", imageUri = ComposeResImageFiles.jpeg.uri))
    add(DecoderTestItem(name = "PNG", imageUri = ComposeResImageFiles.png.uri))
    add(DecoderTestItem(name = "WEBP", imageUri = ComposeResImageFiles.webp.uri))
    add(DecoderTestItem(name = "BMP", imageUri = ComposeResImageFiles.bmp.uri))
    add(
        DecoderTestItem(
            name = "SVG",
            imageUri = ComposeResImageFiles.svg.uri,
            imageDecoder = Factory()
        )
    )
    add(
        DecoderTestItem(
            name = "HEIC",
            imageUri = ComposeResImageFiles.heic.uri,
            minAPI = VERSION_CODES.P,
            currentApi = VERSION.SDK_INT,
        )
    )
    add(
        DecoderTestItem(
            name = "AVIF",
            imageUri = ComposeResImageFiles.avif.uri,
            minAPI = VERSION_CODES.S,
            currentApi = VERSION.SDK_INT,
        )
    )
    add(
        DecoderTestItem(
            name = "GIF_KORAL",
            imageUri = ComposeResImageFiles.animGif.uri,
            imageDecoder = KoralGifDecoder.Factory()
        )
    )
    add(
        DecoderTestItem(
            name = "GIF_MOVIE",
            imageUri = ComposeResImageFiles.animGif.uri,
            minAPI = VERSION_CODES.KITKAT,
            currentApi = VERSION.SDK_INT,
            imageDecoder = MovieGifDecoder.Factory()
        )
    )
    add(
        DecoderTestItem(
            name = "GIF_ANIMATED",
            imageUri = ComposeResImageFiles.animGif.uri,
            minAPI = VERSION_CODES.P,
            currentApi = VERSION.SDK_INT,
            imageDecoder = ImageDecoderGifDecoder.Factory()
        )
    )
    add(
        DecoderTestItem(
            name = "WEBP_ANIMATED",
            imageUri = ComposeResImageFiles.animWebp.uri,
            minAPI = VERSION_CODES.P,
            currentApi = VERSION.SDK_INT,
            imageDecoder = ImageDecoderAnimatedWebpDecoder.Factory()
        )
    )
    add(
        DecoderTestItem(
            name = "HEIF_ANIMATED",
            imageUri = ComposeResImageFiles.animHeif.uri,
            minAPI = VERSION_CODES.P,
            currentApi = VERSION.SDK_INT,
            imageDecoder = ImageDecoderAnimatedHeifDecoder.Factory()
        )
    )
//    add(
//        DecoderTestItem(
//            name = "MP4_FFMPEG",
//            imageUri = ComposeResImageFiles.mp4.uri,
//            imageDecoder = FFmpegVideoFrameDecoder.Factory()
//        )
//    )
    add(
        DecoderTestItem(
            name = "MP4_BUILTIN",
            imageUri = ComposeResImageFiles.mp4.uri,
            minAPI = VERSION_CODES.O_MR1,
            currentApi = VERSION.SDK_INT,
            imageDecoder = VideoFrameDecoder.Factory()
        )
    )
    add(
        DecoderTestItem(
            name = "XML",
            imageUri = newResourceUri(R.drawable.bg_circle_accent),
        )
    )
    add(
        DecoderTestItem(
            name = "VECTOR",
            imageUri = newResourceUri(R.drawable.ic_play),
        )
    )
    val headerUserPackageInfo = loadUserAppPackageInfo(context, true)
    add(
        DecoderTestItem(
            name = "APK_ICON",
            imageUri = headerUserPackageInfo.applicationInfo!!.publicSourceDir,
            imageDecoder = ApkIconDecoder.Factory()
        )
    )
}

private suspend fun loadUserAppPackageInfo(
    context: PlatformContext,
    fromHeader: Boolean
): PackageInfo {
    return withContext(Dispatchers.IO) {
        val packageList =
            context.packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        (if (fromHeader) {
            packageList.find {
                it.applicationInfo!!.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
        } else {
            packageList.findLast {
                it.applicationInfo!!.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
        } ?: context.packageManager.getPackageInfo(context.packageName, 0))
    }
}