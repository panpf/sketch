//package com.github.panpf.sketch.cache
//
//import android.content.res.Resources
//import android.graphics.Bitmap
//import android.graphics.drawable.Drawable
//import androidx.annotation.MainThread
//import com.github.panpf.sketch.BitmapImage
//import com.github.panpf.sketch.CountingImage
//import com.github.panpf.sketch.cache.MemoryCache.Value
//import com.github.panpf.sketch.decode.internal.freeBitmap
//import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
//import com.github.panpf.sketch.request.internal.RequestContext
//import com.github.panpf.sketch.util.configOrNull
//import com.github.panpf.sketch.util.requiredMainThread
//import com.github.panpf.sketch.util.toHexString
//
//fun BitmapImage.toCountingBitmapImage(
//    requestContext: RequestContext,
//): CountingBitmapImage {
//    return toCountingBitmapImage(
//        bitmapPool = requestContext.sketch.bitmapPool,
//        disallowReuseBitmap = requestContext.request.disallowReuseBitmap,
//    )
//}
//
//fun BitmapImage.toCountingBitmapImage(
//    bitmapPool: BitmapPool,
//    disallowReuseBitmap: Boolean,
//): CountingBitmapImage {
//    return if (this is CountingBitmapImage) {
//        this
//    } else {
//        CountingBitmapImage(
//            originBitmap = bitmap,
//            shareable = shareable,
//            resources = resources,
//            bitmapPool = bitmapPool,
//            disallowReuseBitmap = disallowReuseBitmap,
//        )
//    }
//}
//
//class CountingBitmapImage(
//    private val originBitmap: Bitmap,
//    shareable: Boolean = !originBitmap.isMutable,
//    resources: Resources? = null,
//    private val bitmapPool: BitmapPool,
//    private val disallowReuseBitmap: Boolean,
//) : BitmapImage(originBitmap, shareable, resources), CountingImage {
//
//    companion object {
//        private const val MODULE = "CountingBitmapImage"
//    }
//
//    private var _bitmap: Bitmap? = originBitmap
//    private var cachedCount = 0
//    private var displayedCount = 0
//    private var pendingCount = 0
//
//    override val bitmap: Bitmap
//        get() = _bitmap ?: throw IllegalStateException("Bitmap recycled")
//
//    val isRecycled: Boolean
//        get() = _bitmap?.isRecycled ?: true
//
//    override fun cacheValue(requestContext: RequestContext, extras: Map<String, Any?>): Value {
//        return CountingBitmapImageValue(this, extras)
//    }
//
//    override fun asDrawable(): Drawable {
//        return SketchCountBitmapDrawable(resources, this)
//    }
//
//    @MainThread
//    override fun setIsPending(pending: Boolean, caller: String?) {
//        // Pending is to prevent the Drawable from being recycled before it is not used by the target, so it does not need to trigger tryFree
//        requiredMainThread()
//        if (pending) {
//            pendingCount++
//            tryFree(caller = "$caller:pending:true", pending = true)
//        } else {
//            if (pendingCount > 0) {
//                pendingCount--
//            }
//            tryFree(caller = "$caller:pending:false", pending = true)
//        }
//    }
//
//    @Synchronized
//    override fun setIsCached(cached: Boolean, caller: String?) {
//        if (cached) {
//            cachedCount++
//            tryFree(caller = "$caller:cached:true", pending = false)
//        } else {
//            if (cachedCount > 0) {
//                cachedCount--
//            }
//            tryFree(caller = "$caller:cached:false", pending = false)
//        }
//    }
//
//    @MainThread
//    override fun setIsDisplayed(displayed: Boolean, caller: String?) {
//        requiredMainThread()
//        if (displayed) {
//            displayedCount++
//            tryFree(caller = "$caller:displayed:true", pending = false)
//        } else {
//            if (displayedCount > 0) {
//                displayedCount--
//            }
//            tryFree(caller = "$caller:displayed:false", pending = false)
//        }
//    }
//
//    @MainThread
//    override fun getPendingCount(): Int {
//        requiredMainThread()
//        return pendingCount
//    }
//
//    @Synchronized
//    override fun getCachedCount(): Int {
//        return cachedCount
//    }
//
//    @MainThread
//    override fun getDisplayedCount(): Int {
//        requiredMainThread()
//        return displayedCount
//    }
//
//    private fun tryFree(caller: String, pending: Boolean) {
//        val bitmap = this._bitmap
//        if (bitmap == null) {
//            bitmapPool.logger?.w(MODULE, "Bitmap freed. $caller. ${toString()}")
//        } else if (isRecycled) {
//            throw IllegalStateException("Bitmap recycled. $caller. ${toString()}")
//        } else if (!pending && cachedCount == 0 && displayedCount == 0 && pendingCount == 0) {
//            this._bitmap = null
//            bitmapPool.freeBitmap(bitmap, disallowReuseBitmap, caller)
//            bitmapPool.logger?.d(MODULE) { "freeBitmap. $caller. ${toString()}" }
//        } else {
//            bitmapPool.logger?.d(MODULE) { "keep. $caller. ${toString()}" }
//        }
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//        if (!super.equals(other)) return false
//        other as CountingBitmapImage
//        return _bitmap == other.originBitmap
//    }
//
//    override fun hashCode(): Int {
//        return originBitmap.hashCode()
//    }
//
//    override fun toString(): String {
//        val bitmapInfo = originBitmap.run { "${width}x${height},$configOrNull,@${toHexString()}" }
//        val countInfo = "$pendingCount/$cachedCount/$displayedCount"
//        return "CountingBitmapImage($bitmapInfo,$countInfo)"
//    }
//}