package com.github.panpf.sketch.util.pool;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.github.panpf.sketch.util.BitmapUtilsKt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Keys {@link Bitmap Bitmaps} using both {@link Bitmap#getAllocationByteCount()} and
 * the {@link Bitmap.Config} returned from {@link Bitmap#getConfig()}.
 * <p>
 * <p>
 * Using both the config and the byte size allows us to safely re-use a greater variety of
 * {@link Bitmap Bitmaps}, which increases the hit rate of the pool and therefore the performance
 * of applications. This class works around #301 by only allowing re-use of {@link Bitmap Bitmaps}
 * with a matching number of bytes per pixel.
 * </p>
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
public class SizeConfigStrategy implements LruPoolStrategy {

    private static final int MAX_SIZE_MULTIPLE = 8;
    @NonNull
    private static final Bitmap.Config[] ARGB_8888_IN_CONFIGS;

    static {
        // null: The value returned by Bitmaps with the hidden Bitmap config.
        Bitmap.Config[] result = new Bitmap.Config[]{Bitmap.Config.ARGB_8888, null};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = Arrays.copyOf(result, result.length + 1);
            result[result.length - 1] = Bitmap.Config.RGBA_F16;
        }
        ARGB_8888_IN_CONFIGS = result;
    }

    @NonNull
    private static final Bitmap.Config[] RGBA_F16_IN_CONFIGS = ARGB_8888_IN_CONFIGS;

    // We probably could allow ARGB_4444 and RGB_565 to decode into each other, but ARGB_4444 is
    // deprecated and we'd rather be safe.
    @NonNull
    private static final Bitmap.Config[] RGB_565_IN_CONFIGS =
            new Bitmap.Config[]{Bitmap.Config.RGB_565};
    @NonNull
    private static final Bitmap.Config[] ARGB_4444_IN_CONFIGS =
            new Bitmap.Config[]{Bitmap.Config.ARGB_4444};
    @NonNull
    private static final Bitmap.Config[] ALPHA_8_IN_CONFIGS =
            new Bitmap.Config[]{Bitmap.Config.ALPHA_8};

    @NonNull
    private final KeyPool keyPool = new KeyPool();
    @NonNull
    private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap<>();
    @NonNull
    private final Map<Bitmap.Config, NavigableMap<Integer, Integer>> sortedSizes = new HashMap<>();

    @Override
    public void put(@NonNull Bitmap bitmap) {
        int size = BitmapUtilsKt.getAllocationByteCountCompat(bitmap);
        Key key = keyPool.get(size, bitmap.getConfig());

        groupedMap.put(key, bitmap);

        NavigableMap<Integer, Integer> sizes = getSizesForConfig(bitmap.getConfig());
        Integer current = sizes.get(key.size);
        sizes.put(key.size, current == null ? 1 : current + 1);
    }

    @Override
    public Bitmap get(int width, int height, @Nullable Bitmap.Config config) {
        int size = BitmapUtilsKt.getBitmapByteSize(width, height, config);
        Key bestKey = findBestKey(size, config);

        Bitmap result = groupedMap.get(bestKey);
        if (result != null) {
            // Decrement must be called before reconfigure.
            decrementBitmapOfSize(bestKey.size, result);
            try {
                result.reconfigure(width, height, config);
            } catch (IllegalArgumentException e) {
                // Bitmap.cpp Bitmap_reconfigure method may throw "IllegalArgumentException: Bitmap not large enough to support new configuration" exception
                e.printStackTrace();
                put(result);
            }
        }
        return result;
    }

    @Override
    public boolean exist(int width, int height, @Nullable Bitmap.Config config) {
        int size = BitmapUtilsKt.getBitmapByteSize(width, height, config);
        Key bestKey = findBestKey(size, config);
        return groupedMap.exist(bestKey);
    }

    @NonNull
    private Key findBestKey(int size, @Nullable Bitmap.Config config) {
        Key result = keyPool.get(size, config);
        for (Bitmap.Config possibleConfig : getInConfigs(config)) {
            NavigableMap<Integer, Integer> sizesForPossibleConfig = getSizesForConfig(possibleConfig);
            Integer possibleSize = sizesForPossibleConfig.ceilingKey(size);
            if (possibleSize != null && possibleSize <= size * MAX_SIZE_MULTIPLE) {
                if (possibleSize != size || (!Objects.equals(possibleConfig, config))) {
                    keyPool.offer(result);
                    result = keyPool.get(possibleSize, possibleConfig);
                }
                break;
            }
        }
        return result;
    }

    @Nullable
    @Override
    public Bitmap removeLast() {
        Bitmap removed = groupedMap.removeLast();
        if (removed != null) {
            int removedSize = BitmapUtilsKt.getAllocationByteCountCompat(removed);
            decrementBitmapOfSize(removedSize, removed);
        }
        return removed;
    }

    private void decrementBitmapOfSize(@NonNull Integer size, @NonNull Bitmap removed) {
        Bitmap.Config config = removed.getConfig();
        NavigableMap<Integer, Integer> sizes = getSizesForConfig(config);
        Integer current = sizes.get(size);
        if (current == null) {
            String message = String.format(
                    Locale.getDefault(),
                    "Tried to decrement empty size, size: %d, removed: %s, this: %s",
                    size, logBitmap(removed), this
            );
            throw new NullPointerException(message);
        }
        if (current == 1) {
            sizes.remove(size);
        } else {
            sizes.put(size, current - 1);
        }
    }

    @NonNull
    private NavigableMap<Integer, Integer> getSizesForConfig(@Nullable Bitmap.Config config) {
        NavigableMap<Integer, Integer> sizes = sortedSizes.get(config);
        if (sizes == null) {
            sizes = new TreeMap<>();
            sortedSizes.put(config, sizes);
        }
        return sizes;
    }

    @NonNull
    @Override
    public String logBitmap(@NonNull Bitmap bitmap) {
        int size = BitmapUtilsKt.getAllocationByteCountCompat(bitmap);
        return getBitmapString(size, bitmap.getConfig());
    }

    @NonNull
    @Override
    public String logBitmap(int width, int height, @Nullable Bitmap.Config config) {
        int size = BitmapUtilsKt.getBitmapByteSize(width, height, config);
        return getBitmapString(size, config);
    }

    @Override
    public int getSize(@NonNull Bitmap bitmap) {
        return BitmapUtilsKt.getAllocationByteCountCompat(bitmap);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("SizeConfigStrategy(groupedMap=")
                .append(groupedMap)
                .append(", sortedSizes=(");
        for (Map.Entry<Bitmap.Config, NavigableMap<Integer, Integer>> entry : sortedSizes.entrySet()) {
            sb.append(entry.getKey()).append('[').append(entry.getValue()).append("], ");
        }
        if (!sortedSizes.isEmpty()) {
            sb.replace(sb.length() - 2, sb.length(), "");
        }
        return sb.append("))").toString();
    }

    // Visible for testing.
    static class KeyPool extends BaseKeyPool<Key> {

        @NonNull
        public Key get(int size, @Nullable Bitmap.Config config) {
            Key result = get();
            result.init(size, config);
            return result;
        }

        @NonNull
        @Override
        protected Key create() {
            return new Key(this);
        }
    }

    // Visible for testing.
    static final class Key implements Poolable {

        @NonNull
        private final KeyPool pool;
        private int size;
        @Nullable
        private Bitmap.Config config;

        public Key(@NonNull KeyPool pool) {
            this.pool = pool;
        }

        public void init(int size, @Nullable Bitmap.Config config) {
            this.size = size;
            this.config = config;
        }

        @Override
        public void offer() {
            pool.offer(this);
        }

        @NonNull
        @Override
        public String toString() {
            return getBitmapString(size, config);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Key) {
                Key other = (Key) o;
                return size == other.size && (Objects.equals(config, other.config));
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = size;
            result = 31 * result + (config != null ? config.hashCode() : 0);
            return result;
        }
    }

    @NonNull
    private static String getBitmapString(int size, @Nullable Bitmap.Config config) {
        return "[" + size + "](" + config + ")";
    }

    @NonNull
    private static Bitmap.Config[] getInConfigs(@Nullable Bitmap.Config requested) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Bitmap.Config.RGBA_F16.equals(requested)) { // NOPMD - Avoid short circuiting sdk checks.
                return RGBA_F16_IN_CONFIGS;
            }
        }

        if (requested == null) {
            return new Bitmap.Config[]{null};
        }
        switch (requested) {
            case ARGB_8888:
                return ARGB_8888_IN_CONFIGS;
            case RGB_565:
                return RGB_565_IN_CONFIGS;
            case ARGB_4444:
                return ARGB_4444_IN_CONFIGS;
            case ALPHA_8:
                return ALPHA_8_IN_CONFIGS;
            default:
                return new Bitmap.Config[]{requested};
        }
    }
}