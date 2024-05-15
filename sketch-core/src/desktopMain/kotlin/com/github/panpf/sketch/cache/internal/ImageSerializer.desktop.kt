//package com.github.panpf.sketch.cache.internal
//
//import com.github.panpf.sketch.Image
//import com.github.panpf.sketch.JvmBitmapImage
//import com.github.panpf.sketch.SkiaBitmapImage
//import com.github.panpf.sketch.asSketchImage
//import com.github.panpf.sketch.source.DataSource
//import com.github.panpf.sketch.decode.ImageInfo
//import com.github.panpf.sketch.decode.internal.decodeImage
//import com.github.panpf.sketch.request.internal.RequestContext
//import okio.BufferedSink
//import org.jetbrains.skiko.toBufferedImage
//import javax.imageio.ImageIO
//
//actual fun createImageSerializer(): ImageSerializer? = DesktopImageSerializer()
//
//class DesktopImageSerializer : ImageSerializer {
//
//    override fun supportImage(image: Image): Boolean {
//        return image is JvmBitmapImage || image is SkiaBitmapImage
//    }
//
//    override fun compress(image: Image, sink: BufferedSink) {
//        when (image) {
//            is JvmBitmapImage -> {
//                ImageIO.write(image.bitmap, "png", sink.outputStream())
//            }
//
//            is SkiaBitmapImage -> {
//                val bufferedImage = image.bitmap.toBufferedImage()
//                ImageIO.write(bufferedImage, "png", sink.outputStream())
//            }
//
//            else -> {
//                throw IllegalArgumentException("Unsupported image type: ${image::class}")
//            }
//        }
//    }
//
//    override fun decode(
//        requestContext: RequestContext,
//        imageInfo: ImageInfo,
//        dataSource: DataSource
//    ): Image {
//        val bufferedImage = dataSource.decodeImage()
//        return bufferedImage.asSketchImage()
//    }
//}