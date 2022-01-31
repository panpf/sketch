package com.github.panpf.sketch.cache

import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * [Bitmap] 复用缓存池，用于缓存并复用 [Bitmap]，便于解码时直接使用，减少内存分配
 */
interface BitmapPool {
    /**
     * 获取最大容量
     */
    val maxSize: Int

    /**
     * 获取已用容量
     */
    val size: Int

    /**
     * 调整最大容量，用现有最大容量乘以 sizeMultiplier ，如果调整后已用容量超过新的最大容量会立即释放 [Bitmap]
     *
     * @param sizeMultiplier 取值范围 0 到 1.
     */
    fun setSizeMultiplier(sizeMultiplier: Float)

    /**
     * 缓存 [Bitmap]，如果返回 false，调用者有义务调用 [Bitmap.recycle] 回收这个 [Bitmap]
     *
     * @param bitmap [Bitmap]
     * @return false：添加缓存失败，这个 [Bitmap] 可能是不可变的或者已经回收了
     * @see android.graphics.Bitmap.isMutable
     * @see android.graphics.Bitmap.recycle
     */
    fun put(bitmap: Bitmap): Boolean

    /**
     * 获取可复用的 [Bitmap]，这个方法不会抹除 [Bitmap] 所有的颜色，除非是必要的情况下，否则请使用 [.get]
     *
     * @param width  所需要的宽度
     * @param height 所需要的高度
     * @param config 所需要的 [android.graphics.Bitmap.Config]
     * @return [android.graphics.Bitmap]. null：缓存池中没有可复用的 [Bitmap]
     */
    fun getDirty(width: Int, height: Int, config: Bitmap.Config): Bitmap?

    /**
     * 获取可复用的 [Bitmap] 并抹除所有的颜色
     *
     * @param width  所需要的宽度
     * @param height 所需要的高度
     * @param config 所需要的 [android.graphics.Bitmap.Config]
     * @return [android.graphics.Bitmap]. null：缓存池中没有可复用的 [Bitmap]
     */
    operator fun get(width: Int, height: Int, config: Bitmap.Config): Bitmap?

    /**
     * 获取可复用的 [Bitmap] 并抹除所有的颜色，如果没有可复用的 [Bitmap] 就创建
     *
     * @param width  所需要的宽度
     * @param height 所需要的高度
     * @param config 所需要的 [android.graphics.Bitmap.Config]
     * @return [android.graphics.Bitmap]
     */
    fun getOrMake(width: Int, height: Int, config: Bitmap.Config): Bitmap

    /**
     * 从 bitmap pool 中取出可复用的 Bitmap 设置到 inBitmap 上，适用于 BitmapFactory
     *
     * @param options     BitmapFactory.Options 需要用到 inSampleSize 以及 inPreferredConfig 属性
     * @param outWidth    图片原始宽
     * @param outHeight   图片原始高
     * @param outMimeType 图片类型
     * @return true：找到了可复用的 Bitmap
     */
    fun setInBitmap(
        options: BitmapFactory.Options, outWidth: Int, outHeight: Int, outMimeType: String?,
    ): Boolean

    /**
     * 从 bitmap pool 中取出可复用的 Bitmap 设置到 inBitmap 上，适用于 BitmapRegionDecoder
     *
     * @param options    BitmapFactory.Options 需要用到 options 的 inSampleSize 以及 inPreferredConfig 属性
     * @return true：找到了可复用的 Bitmap
     */
    fun setInBitmapForRegionDecoder(
        width: Int,
        height: Int,
        options: BitmapFactory.Options
    ): Boolean

    /**
     * 回收 bitmap，首先尝试放入 bitmap pool，放不进去就回收
     *
     * @param bitmap     要处理的 bitmap
     * @return true：成功放入 bitmap pool
     */
    fun freeBitmapToPool(bitmap: Bitmap?): Boolean

    /**
     * 是否已禁用
     */
    var isDisabled: Boolean

    /**
     * 清除所有 [Bitmap]
     */
    fun clear()

    /**
     * 根据 level 修整缓存
     *
     * @param level 修剪级别，对应 APP 的不同状态
     * @see android.content.ComponentCallbacks2
     */
    fun trimMemory(level: Int)

    /**
     * 是否已关闭
     */
    val isClosed: Boolean

    /**
     * 关闭，关闭后就彻底不能用了，如果你只是想暂时的关闭就使用 [.setDisabled]
     */
    fun close()
}