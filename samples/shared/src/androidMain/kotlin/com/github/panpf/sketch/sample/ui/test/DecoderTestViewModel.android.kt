package com.github.panpf.sketch.sample.ui.test

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.ApkIconDecoder
import com.github.panpf.sketch.decode.FFmpegVideoFrameDecoder
import com.github.panpf.sketch.decode.ImageDecoderAnimatedHeifDecoder
import com.github.panpf.sketch.decode.ImageDecoderAnimatedWebpDecoder
import com.github.panpf.sketch.decode.ImageDecoderGifDecoder
import com.github.panpf.sketch.decode.KoralGifDecoder
import com.github.panpf.sketch.decode.MovieGifDecoder
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.VideoFrameDecoder
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.sample.compose.R
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("NewApi")
actual suspend fun buildDecoderTestItems(
    context: PlatformContext
): List<PhotoTestItem> = buildList {
    add(PhotoTestItem(title = "JPEG", photoUri = ComposeResImageFiles.jpeg.uri))
    add(PhotoTestItem(title = "PNG", photoUri = ComposeResImageFiles.png.uri))
    add(PhotoTestItem(title = "WEBP", photoUri = ComposeResImageFiles.webp.uri))
    add(PhotoTestItem(title = "BMP", photoUri = ComposeResImageFiles.bmp.uri))
    add(
        PhotoTestItem(
            title = "SVG",
            photoUri = ComposeResImageFiles.svg.uri,
            imageDecoder = SvgDecoder.Factory()
        )
    )
    add(
        PhotoTestItem(
            title = "HEIC",
            photoUri = ComposeResImageFiles.heic.uri,
            apiSupport = VERSION.SDK_INT >= VERSION_CODES.P,
        )
    )
    add(
        PhotoTestItem(
            title = "AVIF",
            photoUri = ComposeResImageFiles.avif.uri,
            apiSupport = VERSION.SDK_INT >= VERSION_CODES.S,
        )
    )
    add(
        PhotoTestItem(
            title = "GIF_KORAL",
            photoUri = ComposeResImageFiles.animGif.uri,
            imageDecoder = KoralGifDecoder.Factory()
        )
    )
    add(
        PhotoTestItem(
            title = "GIF_MOVIE",
            photoUri = ComposeResImageFiles.animGif.uri,
            apiSupport = VERSION.SDK_INT >= VERSION_CODES.KITKAT,
            imageDecoder = MovieGifDecoder.Factory()
        )
    )
    add(
        PhotoTestItem(
            title = "GIF_ANIMATED",
            photoUri = ComposeResImageFiles.animGif.uri,
            apiSupport = VERSION.SDK_INT >= VERSION_CODES.P,
            imageDecoder = ImageDecoderGifDecoder.Factory()
        )
    )
    add(
        PhotoTestItem(
            title = "WEBP_ANIMATED",
            photoUri = ComposeResImageFiles.animWebp.uri,
            apiSupport = VERSION.SDK_INT >= VERSION_CODES.P,
            imageDecoder = ImageDecoderAnimatedWebpDecoder.Factory()
        )
    )
    add(
        PhotoTestItem(
            title = "HEIF_ANIMATED",
            photoUri = ComposeResImageFiles.animHeif.uri,
            apiSupport = VERSION.SDK_INT >= VERSION_CODES.P,
            imageDecoder = ImageDecoderAnimatedHeifDecoder.Factory()
        )
    )
    add(
        PhotoTestItem(
            title = "MP4_FFMPEG",
            photoUri = ComposeResImageFiles.mp4.uri,
            imageDecoder = FFmpegVideoFrameDecoder.Factory()
        )
    )
    add(
        PhotoTestItem(
            title = "MP4_BUILTIN",
            photoUri = ComposeResImageFiles.mp4.uri,
            apiSupport = VERSION.SDK_INT >= VERSION_CODES.O_MR1,
            imageDecoder = VideoFrameDecoder.Factory()
        )
    )
    add(
        PhotoTestItem(
            title = "XML",
            photoUri = newResourceUri(R.drawable.bg_circle_accent),
        )
    )
    add(
        PhotoTestItem(
            title = "VECTOR",
            photoUri = newResourceUri(R.drawable.ic_play),
        )
    )
    val headerUserPackageInfo = loadUserAppPackageInfo(context, true)
    add(
        PhotoTestItem(
            title = "APK_ICON",
            photoUri = headerUserPackageInfo.applicationInfo!!.publicSourceDir,
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