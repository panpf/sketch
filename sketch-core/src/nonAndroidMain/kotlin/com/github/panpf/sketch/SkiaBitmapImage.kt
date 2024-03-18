//package com.github.panpf.sketch
//
//import androidx.compose.runtime.Stable
//import com.github.panpf.sketch.cache.MemoryCache.Value
//import com.github.panpf.sketch.request.internal.RequestContext
//
//fun SkiaBitmap.asSketchImage(): SkiaBitmapImage = SkiaBitmapImage(this)
//
//@Stable
//data class SkiaBitmapImage(
//    val bitmap: SkiaBitmap,
//    override val shareable: Boolean = true
//) : Image {
//
//    override val width: Int = bitmap.width
//
//    override val height: Int = bitmap.height
//
//    override val byteCount: Long = (bitmap.rowBytes * bitmap.height).toLong()
//
//    override val allocationByteCount: Long = byteCount
//
//    override fun cacheValue(requestContext: RequestContext, extras: Map<String, Any?>): Value =
////        SkiaBitmapValue(this, extras)
//
//    override fun checkValid(): Boolean = true
//
//    override fun transformer(): ImageTransformer? {
//        // TODO transformer
//        return null
//    }
//
//    override fun getPixels(): IntArray? = bitmap.readIntPixels()
//
//    override fun toString(): String {
//        return "SkiaBitmapImage(bitmap=${bitmap.toLogString()}, shareable=$shareable)"
//    }
//}
//
//class SkiaBitmapValue(
//    private val skiaBitmapImage: SkiaBitmapImage,
//    override val extras: Map<String, Any?>
//) : Value {
//
//    override val image: Image = skiaBitmapImage
//
//    override val size: Long = skiaBitmapImage.byteCount
//
//    override fun checkValid(): Boolean = skiaBitmapImage.checkValid()
//}