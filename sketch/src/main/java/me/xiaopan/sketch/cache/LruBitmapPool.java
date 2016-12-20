package me.xiaopan.sketch.cache;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import me.xiaopan.sketch.Sketch;
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

    protected String logName = "LruBitmapPool";
    private final LruPoolStrategy strategy;
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

    // Exposed for testing only.
    LruBitmapPool(Context context, int maxSize, LruPoolStrategy strategy, Set<Bitmap.Config> allowedConfigs) {
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
    public synchronized boolean put(Bitmap bitmap) {
        if (closed) {
            return false;
        }

        if (bitmap == null) {
            throw new NullPointerException("Bitmap must not be null");
        }
        if (!bitmap.isMutable() || strategy.getSize(bitmap) > maxSize || !allowedConfigs.contains(bitmap.getConfig())) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, logName + ". Reject bitmap from pool"
                        + ", bitmap: " + strategy.logBitmap(bitmap)
                        + ", is mutable: " + bitmap.isMutable()
                        + ", is allowed config: " + allowedConfigs.contains(bitmap.getConfig()));
            }
            return false;
        }

        final int size = strategy.getSize(bitmap);
        strategy.put(bitmap);
        tracker.add(bitmap);

        puts++;
        currentSize += size;

        if (Sketch.isDebugMode()) {
            Log.v(Sketch.TAG, logName + ". Put bitmap in pool=" + strategy.logBitmap(bitmap));
        }
        dump();

        evict();
        return true;
    }

    private void evict() {
        if (closed) {
            return;
        }

        trimToSize(maxSize);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public synchronized Bitmap getDirty(int width, int height, Bitmap.Config config) {
        if (closed) {
            return null;
        }

        // Config will be null for non public config types, which can lead to transformations naively passing in
        // null as the requested config here. See issue #194.
        final Bitmap result = strategy.get(width, height, config != null ? config : DEFAULT_CONFIG);
        if (result == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, logName + ". Missing bitmap=" + strategy.logBitmap(width, height, config));
            }
            misses++;
        } else {
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, logName + ". Get bitmap=" + strategy.logBitmap(width, height, config));
            }
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
        if (closed) {
            return null;
        }

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
        if (closed) {
            return null;
        }

        Bitmap result = get(width, height, config);
        if (result == null) {
            result = Bitmap.createBitmap(width, height, config);
        }

        return result;
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

        long releasedSize = size - getSize();
        if (Sketch.isDebugMode()) {
            Log.w(Sketch.TAG, SketchUtils.concat(logName,
                    ". trimMemory",
                    ". level=", SketchUtils.getTrimLevelName(level),
                    ", released: ", Formatter.formatFileSize(context, releasedSize)));
        }
    }

    @Override
    public synchronized void clear() {
        if (Sketch.isDebugMode()) {
            Log.w(Sketch.TAG, SketchUtils.concat(logName,
                    ". clear",
                    ". before clean memoryCacheSize: ", Formatter.formatFileSize(context, getSize())));
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
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, logName + ". Size mismatch, resetting");
                    dumpUnchecked();
                }
                currentSize = 0;
                return;
            }

            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, logName + ". Evicting bitmap=" + strategy.logBitmap(removed));
            }
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
        if (Sketch.isDebugMode()) {
            Log.v(Sketch.TAG, logName + ". Hits=" + hits
                    + ", misses=" + misses
                    + ", puts=" + puts
                    + ", evictions=" + evictions
                    + ", currentSize=" + currentSize
                    + ", maxSize=" + maxSize
                    + "\nStrategy=" + strategy);
        }
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(null, new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(String join, StringBuilder builder) {
        if (!TextUtils.isEmpty(join)) {
            builder.append(join);
        }
        return builder.append(logName)
                .append("(")
                .append("maxSize=").append(Formatter.formatFileSize(context, getMaxSize()))
                .append(")");
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
