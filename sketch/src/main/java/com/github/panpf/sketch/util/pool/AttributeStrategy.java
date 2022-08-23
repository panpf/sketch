package com.github.panpf.sketch.util.pool;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.panpf.sketch.util.BitmapUtilsKt;

import java.util.Locale;

/**
 * A strategy for reusing bitmaps that requires any returned bitmap's dimensions to exactly match those request.
 */
public class AttributeStrategy implements LruPoolStrategy {

    @NonNull
    private final KeyPool keyPool = new KeyPool();
    @NonNull
    private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap<>();

    @Override
    public void put(@NonNull Bitmap bitmap) {
        final Key key = keyPool.get(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        groupedMap.put(key, bitmap);
    }

    @Nullable
    @Override
    public Bitmap get(int width, int height, @Nullable Bitmap.Config config) {
        final Key key = keyPool.get(width, height, config);
        return groupedMap.get(key);
    }

    @Override
    public boolean exist(int width, int height, @Nullable Bitmap.Config config) {
        final Key key = keyPool.get(width, height, config);
        return groupedMap.exist(key);
    }

    @Override
    public boolean exist(Bitmap bitmap) {
        final Key key = keyPool.get(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        return groupedMap.exist(key, bitmap);
    }

    @Nullable
    @Override
    public Bitmap removeLast() {
        return groupedMap.removeLast();
    }

    @NonNull
    @Override
    public String logBitmap(@NonNull Bitmap bitmap) {
        return getBitmapString(bitmap);
    }

    @NonNull
    @Override
    public String logBitmap(int width, int height, @Nullable Bitmap.Config config) {
        return getBitmapString(width, height, config);
    }

    @Override
    public int getSize(@NonNull Bitmap bitmap) {
        return BitmapUtilsKt.getAllocationByteCountCompat(bitmap);
    }

    @NonNull
    @Override
    public String toString() {
        return "AttributeStrategy(" + groupedMap + ")";
    }

    @NonNull
    private static String getBitmapString(@NonNull Bitmap bitmap) {
        return getBitmapString(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
    }

    @NonNull
    private static String getBitmapString(int width, int height, @Nullable Bitmap.Config config) {
        return String.format(Locale.getDefault(), "[%dx%d](%s)", width, height, config);
    }

    private static class KeyPool extends BaseKeyPool<Key> {

        @NonNull
        public Key get(int width, int height, @Nullable Bitmap.Config config) {
            Key result = get();
            result.init(width, height, config);
            return result;
        }

        @NonNull
        @Override
        protected Key create() {
            return new Key(this);
        }
    }

    private static class Key implements Poolable {

        @NonNull
        private final KeyPool pool;
        private int width;
        private int height;
        @Nullable
        private Bitmap.Config config;

        public Key(@NonNull KeyPool pool) {
            this.pool = pool;
        }

        public void init(int width, int height, @Nullable Bitmap.Config config) {
            this.width = width;
            this.height = height;
            this.config = config;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Key) {
                Key other = (Key) o;
                return width == other.width && height == other.height && config == other.config;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = width;
            result = 31 * result + height;
            result = 31 * result + (config != null ? config.hashCode() : 0);
            return result;
        }

        @NonNull
        @Override
        public String toString() {
            return getBitmapString(width, height, config);
        }

        @Override
        public void offer() {
            pool.offer(this);
        }
    }
}