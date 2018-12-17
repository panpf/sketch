package me.panpf.sketch.cache;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * {@link Bitmap} 复用缓存池，用于缓存并复用 {@link Bitmap}，便于解码时直接使用，减少内存分配
 */
public interface BitmapPool {

    /**
     * 获取最大容量
     */
    int getMaxSize();

    /**
     * 获取已用容量
     */
    int getSize();

    /**
     * 调整最大容量，用现有最大容量乘以 sizeMultiplier ，如果调整后已用容量超过新的最大容量会立即释放 {@link Bitmap}
     *
     * @param sizeMultiplier 取值范围 0 到 1.
     */
    @SuppressWarnings("unused")
    void setSizeMultiplier(float sizeMultiplier);

    /**
     * 缓存 {@link Bitmap}，如果返回 false，调用者有义务调用 {@link Bitmap#recycle()} 回收这个 {@link Bitmap}
     *
     * @param bitmap {@link Bitmap}
     * @return false：添加缓存失败，这个 {@link Bitmap} 可能是不可变的或者已经回收了
     * @see android.graphics.Bitmap#isMutable()
     * @see android.graphics.Bitmap#recycle()
     */
    boolean put(@NonNull Bitmap bitmap);

    /**
     * 获取可复用的 {@link Bitmap}，这个方法不会抹除 {@link Bitmap} 所有的颜色，除非是必要的情况下，否则请使用 {@link #get(int, int, Bitmap.Config)}
     *
     * @param width  所需要的宽度
     * @param height 所需要的高度
     * @param config 所需要的 {@link android.graphics.Bitmap.Config}
     * @return {@link android.graphics.Bitmap}. null：缓存池中没有可复用的 {@link Bitmap}
     */
    @Nullable
    Bitmap getDirty(int width, int height, @NonNull Bitmap.Config config);

    /**
     * 获取可复用的 {@link Bitmap} 并抹除所有的颜色
     *
     * @param width  所需要的宽度
     * @param height 所需要的高度
     * @param config 所需要的 {@link android.graphics.Bitmap.Config}
     * @return {@link android.graphics.Bitmap}. null：缓存池中没有可复用的 {@link Bitmap}
     */
    @Nullable
    Bitmap get(int width, int height, @NonNull Bitmap.Config config);

    /**
     * 获取可复用的 {@link Bitmap} 并抹除所有的颜色，如果没有可复用的 {@link Bitmap} 就创建
     *
     * @param width  所需要的宽度
     * @param height 所需要的高度
     * @param config 所需要的 {@link android.graphics.Bitmap.Config}
     * @return {@link android.graphics.Bitmap}
     */
    @NonNull
    Bitmap getOrMake(int width, int height, @NonNull Bitmap.Config config);

    /**
     * 是否已禁用
     */
    @SuppressWarnings("unused")
    boolean isDisabled();

    /**
     * 设置是否禁用
     *
     * @param disabled 是否禁用
     */
    void setDisabled(boolean disabled);

    /**
     * 清除所有 {@link Bitmap}
     */
    void clear();

    /**
     * 根据 level 修整缓存
     *
     * @param level 修剪级别，对应 APP 的不同状态
     * @see android.content.ComponentCallbacks2
     */
    void trimMemory(int level);

    /**
     * 是否已关闭
     */
    @SuppressWarnings("unused")
    boolean isClosed();

    /**
     * 关闭，关闭后就彻底不能用了，如果你只是想暂时的关闭就使用 {@link #setDisabled(boolean)}
     */
    void close();
}
