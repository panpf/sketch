package me.panpf.sketch.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import android.text.format.Formatter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import me.panpf.sketch.SLog;
import me.panpf.sketch.cache.recycle.AttributeStrategy;
import me.panpf.sketch.cache.recycle.LruPoolStrategy;
import me.panpf.sketch.cache.recycle.SizeConfigStrategy;
import me.panpf.sketch.util.SketchUtils;

/**
 * 根据最少使用规则释放缓存的 {@link Bitmap} 复用池
 */
public class LruBitmapPool implements BitmapPool {
    private static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;
    private static final String NAME = "LruBitmapPool";

    @NonNull
    private final LruPoolStrategy strategy;
    @NonNull
    private final Set<Bitmap.Config> allowedConfigs;
    private final int initialMaxSize;
    private final BitmapTracker tracker;

    private int maxSize;
    private int currentSize;
    private int hits;
    private int misses;
    private int puts;
    private int evictions;

    private Context context;
    private boolean closed;
    private boolean disabled;

    LruBitmapPool(Context context, int maxSize, @NonNull LruPoolStrategy strategy, @NonNull Set<Bitmap.Config> allowedConfigs) {
        this.context = context.getApplicationContext();
        this.initialMaxSize = maxSize;
        this.maxSize = maxSize;
        this.strategy = strategy;
        this.allowedConfigs = allowedConfigs;
        this.tracker = new NullBitmapTracker();
    }

    /**
     * 创建根据最少使用规则释放缓存的 {@link Bitmap} 复用池，使用默认的 {@link Bitmap} 匹配策略和 {@link Bitmap.Config} 白名单
     *
     * @param maxSize 最大容量
     */
    public LruBitmapPool(Context context, int maxSize) {
        this(context, maxSize, getDefaultStrategy(), getDefaultAllowedConfigs());
    }

    /**
     * 创建根据最少使用规则释放缓存的 {@link Bitmap} 复用池，使用默认的 {@link Bitmap} 匹配策略
     *
     * @param maxSize        最大容量
     * @param allowedConfigs {@link Bitmap.Config} 白名单
     */
    @SuppressWarnings("unused")
    public LruBitmapPool(Context context, int maxSize, @NonNull Set<Bitmap.Config> allowedConfigs) {
        this(context, maxSize, getDefaultStrategy(), allowedConfigs);
    }

    private static LruPoolStrategy getDefaultStrategy() {
        final LruPoolStrategy strategy;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            strategy = new SizeConfigStrategy();
        } else {
            strategy = new AttributeStrategy();
        }
        return strategy;
    }

    private static Set<Bitmap.Config> getDefaultAllowedConfigs() {
        Set<Bitmap.Config> configs = new HashSet<>();
        configs.addAll(Arrays.asList(Bitmap.Config.values()));
        if (Build.VERSION.SDK_INT >= 19) {
            configs.add(null);
        }
        return Collections.unmodifiableSet(configs);
    }

    @Override
    public synchronized boolean put(@NonNull Bitmap bitmap) {
        if (closed) {
            return false;
        }

        if (disabled) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Disabled. Unable put, bitmap=%s,%s", strategy.logBitmap(bitmap), SketchUtils.toHexString(bitmap));
            }
            return false;
        }

        //noinspection ConstantConditions
        if (bitmap == null) {
            throw new NullPointerException("Bitmap must not be null");
        }
        if (bitmap.isRecycled() || !bitmap.isMutable() || strategy.getSize(bitmap) > maxSize || !allowedConfigs.contains(bitmap.getConfig())) {
            SLog.w(NAME, "Reject bitmap from pool, bitmap: %s, is recycled: %s, is mutable: %s, is allowed config: %s, %s",
                    strategy.logBitmap(bitmap), bitmap.isRecycled(), bitmap.isMutable(),
                    allowedConfigs.contains(bitmap.getConfig()), SketchUtils.toHexString(bitmap));
            return false;
        }

        final int size = strategy.getSize(bitmap);
        strategy.put(bitmap);
        tracker.add(bitmap);

        puts++;
        currentSize += size;

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
            SLog.d(NAME, "Put bitmap in pool=%s,%s", strategy.logBitmap(bitmap), SketchUtils.toHexString(bitmap));
        }
        dump();

        evict();
        return true;
    }

    @Override
    public synchronized Bitmap getDirty(int width, int height, @NonNull Bitmap.Config config) {
        if (closed) {
            return null;
        }

        if (disabled) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Disabled. Unable get, bitmap=%s,%s", strategy.logBitmap(width, height, config));
            }
            return null;
        }

        // Config will be null for non public config types, which can lead to transformations naively passing in
        // null as the requested config here. See issue #194.
        //noinspection ConstantConditions
        final Bitmap result = strategy.get(width, height, config != null ? config : DEFAULT_CONFIG);
        if (result == null) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Missing bitmap=%s", strategy.logBitmap(width, height, config));
            }
            misses++;
        } else {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Get bitmap=%s,%s", strategy.logBitmap(width, height, config), SketchUtils.toHexString(result));
            }
            hits++;
            currentSize -= strategy.getSize(result);
            tracker.remove(result);
            result.setHasAlpha(true);
        }
        dump();

        return result;
    }

    @Override
    public synchronized Bitmap get(int width, int height, @NonNull Bitmap.Config config) {
        Bitmap result = getDirty(width, height, config);
        if (result != null) {
            // Bitmaps in the pool contain random data that in some cases must be cleared for an image to be rendered
            // correctly. we shouldn't force all consumers to independently erase the contents individually, so we do so
            // here. See issue #131.
            result.eraseColor(Color.TRANSPARENT);
        }

        return result;
    }

    @NonNull
    @Override
    public Bitmap getOrMake(int width, int height, @NonNull Bitmap.Config config) {
        Bitmap result = get(width, height, config);
        if (result == null) {
            result = Bitmap.createBitmap(width, height, config);

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                StackTraceElement[] elements = new Exception().getStackTrace();
                StackTraceElement element = elements.length > 1 ? elements[1] : elements[0];
                SLog.d(NAME, "Make bitmap. info:%dx%d,%s,%s - %s.%s:%d",
                        result.getWidth(), result.getHeight(), result.getConfig(), SketchUtils.toHexString(result),
                        element.getClassName(), element.getMethodName(), element.getLineNumber());
            }
        }

        return result;
    }

    private void evict() {
        if (closed) {
            return;
        }

        trimToSize(maxSize);
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public int getSize() {
        return currentSize;
    }

    @Override
    public synchronized void setSizeMultiplier(float sizeMultiplier) {
        if (closed) {
            return;
        }

        maxSize = Math.round(initialMaxSize * sizeMultiplier);
        evict();
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        if (this.disabled != disabled) {
            this.disabled = disabled;
            if (disabled) {
                SLog.w(NAME, "setDisabled. %s", true);
            } else {
                SLog.w(NAME, "setDisabled. %s", false);
            }
        }
    }

    @SuppressLint("InlinedApi")
    @Override
    public synchronized void trimMemory(int level) {
        long size = getSize();

        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            trimToSize(0);
        } else if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            trimToSize(maxSize / 2);
        }

        String releasedSize = Formatter.formatFileSize(context, size - getSize());
        SLog.w(NAME, "trimMemory. level=%s, released: %s", SketchUtils.getTrimLevelName(level), releasedSize);
    }

    @Override
    public synchronized void clear() {
        SLog.w(NAME, "clear. before size %s", Formatter.formatFileSize(context, getSize()));

        trimToSize(0);
    }

    @Override
    public synchronized boolean isClosed() {
        return closed;
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }

        closed = true;
        trimToSize(0);
    }

    private synchronized void trimToSize(int size) {
        while (currentSize > size) {
            final Bitmap removed = strategy.removeLast();
            if (removed == null) {
                SLog.w(NAME, "Size mismatch, resetting");
                dumpUnchecked();
                currentSize = 0;
                return;
            }

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Evicting bitmap=%s,%s", strategy.logBitmap(removed), SketchUtils.toHexString(removed));
            }
            tracker.remove(removed);
            currentSize -= strategy.getSize(removed);
            removed.recycle();
            evictions++;
            dump();
        }
    }

    private void dump() {
        dumpUnchecked();
    }

    private void dumpUnchecked() {
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
            SLog.d(NAME, "Hits=%d, misses=%d, puts=%d, evictions=%d, currentSize=%d, maxSize=%d, Strategy=%s",
                    hits, misses, puts, evictions, currentSize, maxSize, strategy);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s(maxSize=%s,strategy=%s,allowedConfigs=%s)",
                NAME, Formatter.formatFileSize(context, getMaxSize()), strategy.getKey(), allowedConfigs.toString());
    }

    private interface BitmapTracker {
        void add(Bitmap bitmap);

        void remove(Bitmap bitmap);
    }

    @SuppressWarnings("unused")
    // Only used for debugging
    private static class ThrowingBitmapTracker implements BitmapTracker {
        private final Set<Bitmap> bitmaps = Collections.synchronizedSet(new HashSet<Bitmap>());

        @Override
        public void add(Bitmap bitmap) {
            if (bitmaps.contains(bitmap)) {
                throw new IllegalStateException("Can't add already added bitmap: " + bitmap + " [" + bitmap.getWidth()
                        + "x" + bitmap.getHeight() + "]");
            }
            bitmaps.add(bitmap);
        }

        @Override
        public void remove(Bitmap bitmap) {
            if (!bitmaps.contains(bitmap)) {
                throw new IllegalStateException("Cannot remove bitmap not in tracker");
            }
            bitmaps.remove(bitmap);
        }
    }

    private static class NullBitmapTracker implements BitmapTracker {
        @Override
        public void add(Bitmap bitmap) {
            // Do nothing.
        }

        @Override
        public void remove(Bitmap bitmap) {
            // Do nothing.
        }
    }
}
