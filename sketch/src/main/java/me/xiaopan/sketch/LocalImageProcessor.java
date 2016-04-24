package me.xiaopan.sketch;

import me.xiaopan.sketch.cache.DiskCache;

/**
 * 本地图片处理器
 */
public interface LocalImageProcessor {
    /**
     * 是不是特殊的本地图片
     */
    boolean isSpecific(LoadRequest loadRequest);

    /**
     * 获取特殊本地图片的本地缓存实体
     */
    DiskCache.Entry getDiskCacheEntry(LoadRequest loadRequest);

    /**
     * 获取标识符
     *
     * @return 标识符
     */
    String getIdentifier();

    /**
     * 追加标识符
     */
    StringBuilder appendIdentifier(StringBuilder builder);
}
