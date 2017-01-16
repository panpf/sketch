package me.xiaopan.sketch.cache;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.cache.recycle.AttributeStrategy;
import me.xiaopan.sketch.cache.recycle.LruPoolStrategy;
import me.xiaopan.sketch.cache.recycle.SizeConfigStrategy;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * An {@link BitmapPool} implementation that uses an
 * {@link me.xiaopan.sketch.cache.recycle.LruPoolStrategy} to bucket {@link Bitmap}s and then uses an LRU
 * eviction policy to evict {@link android.graphics.Bitmap}s from the least recently used bucket in order to keep
 * the pool below a given maximum size limit.
 */
public class LruBitmapPool implements BitmapPool {
    private static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;

    private final LruPoolStrategy strategy;
    private final Set<Bitmap.Config> allowedConfigs;
    private final int initialMaxSize;
    private final BitmapTracker tracker;

    protected String logName = "LruBitmapPool";
    private int maxSize;
    private int currentSize;
    private int hits;
    private int misses;
    private int puts;
    private int evictions;

    private Context context;
    private boolean closed;
    private boolean disabled;

    // Exposed for testing only.
    LruBitmapPool(Context context, int maxSize, LruPoolStrategy strategy, Set<Bitmap.Config> allowedConfigs) {
        context = context.getApplicationContext();
        this.context = context;
        this.initialMaxSize = maxSize;
        this.maxSize = maxSize;
        this.strategy = strategy;
        this.allowedConfigs = allowedConfigs;
        this.tracker = new NullBitmapTracker();
    }

    /**
     * Constructor for LruBitmapPool.
     *
     * @param maxSize The initial maximum size of the pool in bytes.
     */
    public LruBitmapPool(Context context, int maxSize) {
        this(context, maxSize, getDefaultStrategy(), getDefaultAllowedConfigs());
    }

    /**
     * Constructor for LruBitmapPool.
     *
     * @param maxSize        The initial maximum size of the pool in bytes.
     * @param allowedConfigs A white listed set of {@link android.graphics.Bitmap.Config} that are allowed to be put
     *                       into the pool. Configs not in the allowed set will be rejected.
     */
    @SuppressWarnings("unused")
    public LruBitmapPool(Context context, int maxSize, Set<Bitmap.Config> allowedConfigs) {
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
        Set<Bitmap.Config> configs = new HashSet<Bitmap.Config>();
        configs.addAll(Arrays.asList(Bitmap.Config.values()));
        if (Build.VERSION.SDK_INT >= 19) {
            configs.add(null);
        }
        return Collections.unmodifiableSet(configs);
    }

    @Override
    public synchronized boolean put(Bitmap bitmap) {
        if (closed) {
            return false;
        }

        if (disabled) {
            SLog.w(SLogType.CACHE, logName, "Disabled. Unable put, bitmap=%s,%s",
                    strategy.logBitmap(bitmap), SketchUtils.toHexString(bitmap));
            return false;
        }

        if (bitmap == null) {
            throw new NullPointerException("Bitmap must not be null");
        }
        if (bitmap.isRecycled() || !bitmap.isMutable() || strategy.getSize(bitmap) > maxSize || !allowedConfigs.contains(bitmap.getConfig())) {
            SLog.w(SLogType.CACHE, logName, "Reject bitmap from pool, bitmap: %s, is recycled: %s, is mutable: %s, is allowed config: %s, %s",
                    strategy.logBitmap(bitmap), bitmap.isRecycled(), bitmap.isMutable(),
                    allowedConfigs.contains(bitmap.getConfig()), SketchUtils.toHexString(bitmap));
            return false;
        }

        final int size = strategy.getSize(bitmap);
        strategy.put(bitmap);
        tracker.add(bitmap);

        puts++;
        currentSize += size;

        SLog.v(SLogType.CACHE, logName, "Put bitmap in pool=%s,%s",
                strategy.logBitmap(bitmap), SketchUtils.toHexString(bitmap));
        dump();

        evict();
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public synchronized Bitmap getDirty(int width, int height, Bitmap.Config config) {
        if (closed) {
            return null;
        }

        if (disabled) {
            SLog.w(SLogType.CACHE, logName, "Disabled. Unable get, bitmap=%s,%s",
                    strategy.logBitmap(width, height, config));
            return null;
        }

        // Config will be null for non public config types, which can lead to transformations naively passing in
        // null as the requested config here. See issue #194.
        final Bitmap result = strategy.get(width, height, config != null ? config : DEFAULT_CONFIG);
        if (result == null) {
            SLog.w(SLogType.CACHE, logName, "Missing bitmap=%s", strategy.logBitmap(width, height, config));
            misses++;
        } else {
            SLog.i(SLogType.CACHE, logName, "Get bitmap=%s,%s", strategy.logBitmap(width, height, config), SketchUtils.toHexString(result));
            hits++;
            currentSize -= strategy.getSize(result);
            tracker.remove(result);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                result.setHasAlpha(true);
            }
        }
        dump();

        return result;
    }

    @Override
    public synchronized Bitmap get(int width, int height, Bitmap.Config config) {
        Bitmap result = getDirty(width, height, config);
        if (result != null) {
            // Bitmaps in the pool contain random data that in some cases must be cleared for an image to be rendered
            // correctly. we shouldn't force all consumers to independently erase the contents individually, so we do so
            // here. See issue #131.
            result.eraseColor(Color.TRANSPARENT);
        }

        return result;
    }

    @Override
    public Bitmap getOrMake(int width, int height, Bitmap.Config config) {
        Bitmap result = get(width, height, config);
        if (result == null) {
            result = Bitmap.createBitmap(width, height, config);
            if (SLogType.CACHE.isEnabled()) {
                StackTraceElement[] elements = new Exception().getStackTrace();
                StackTraceElement element = elements.length > 1 ? elements[1] : elements[0];
                SLog.i(SLogType.CACHE, String.format("Make bitmap. info:%dx%d,%s,%s - %s.%s:%d",
                        result.getWidth(), result.getHeight(), result.getConfig(), SketchUtils.toHexString(result),
                        element.getClassName(), element.getMethodName(), element.getLineNumber()));
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
        this.disabled = disabled;
        if (disabled) {
            SLog.w(SLogType.CACHE, logName, "setDisabled. %s", true);
        } else {
            SLog.i(SLogType.CACHE, logName, "setDisabled. %s", false);
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

        if (SLogType.CACHE.isEnabled()) {
            String releasedSize = Formatter.formatFileSize(context, size - getSize());
            SLog.w(SLogType.CACHE, logName, "trimMemory. level=%s, released: %s",
                    SketchUtils.getTrimLevelName(level), releasedSize);
        }
    }

    @Override
    public synchronized void clear() {
        if (SLogType.CACHE.isEnabled()) {
            SLog.w(SLogType.CACHE, logName, "clear. before size %s", Formatter.formatFileSize(context, getSize()));
        }

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
                SLog.w(SLogType.CACHE, logName, "Size mismatch, resetting");
                dumpUnchecked();
                currentSize = 0;
                return;
            }

            SLog.e(SLogType.CACHE, logName, "Evicting bitmap=%s,%s",
                    strategy.logBitmap(removed), SketchUtils.toHexString(removed));
            tracker.remove(removed);
            currentSize -= strategy.getSize(removed);
            removed.recycle();
            evictions++;
            dump();
        }
    }

    private void dump() {
        if (Log.isLoggable(logName, Log.VERBOSE)) {
            dumpUnchecked();
        }
    }

    private void dumpUnchecked() {
        SLog.v(SLogType.CACHE, logName,
                "Hits=%d, misses=%d, puts=%d, evictions=%d, currentSize=%d, maxSize=%d, Strategy=%s",
                hits, misses, puts, evictions, currentSize, maxSize, strategy);
    }

    @Override
    public String getKey() {
        return String.format("%s(maxSize=%s)", logName, Formatter.formatFileSize(context, getMaxSize()));
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
