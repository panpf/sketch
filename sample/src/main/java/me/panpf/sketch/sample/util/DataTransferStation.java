package me.panpf.sketch.sample.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

public class DataTransferStation {
    private static final SparseArray<Map<String, Object>> PAGE_ARRAY = new SparseArray<>();

    private DataTransferStation() {
    }

    @NonNull
    private static String put(int pageId, @NonNull String dataFlag, @NonNull Object data) {
        //noinspection ConstantConditions
        if (data == null) {
            return null;
        }

        Map<String, Object> dataMap = PAGE_ARRAY.get(pageId);
        if (dataMap == null) {
            dataMap = new HashMap<>();
            PAGE_ARRAY.put(pageId, dataMap);
        }

        String key = createKey(pageId, dataFlag);
        dataMap.put(key, data);
        return key;
    }

    private static void clean(int pageId) {
        Map<String, Object> objectMap = PAGE_ARRAY.get(pageId);
        if (objectMap != null) {
            objectMap.clear();
        }
        PAGE_ARRAY.remove(pageId);
    }

    @Nullable
    private static Object get(@Nullable String key) {
        if (key == null) {
            return null;
        }

        int pageId = parsePageIdFromKey(key);
        Map<String, Object> dataMap = PAGE_ARRAY.get(pageId);
        if (dataMap != null) {
            return dataMap.get(key);
        }
        return null;
    }

    @Nullable
    private static Object remove(@Nullable String key) {
        if (key == null) {
            return null;
        }

        int pageId = parsePageIdFromKey(key);
        Map<String, Object> dataMap = PAGE_ARRAY.get(pageId);
        if (dataMap != null) {
            return dataMap.remove(key);
        }
        return null;
    }

    private static String createKey(int pageId, @NonNull String dataFlag) {
        return pageId + "_" + dataFlag;
    }

    private static int parsePageIdFromKey(@NonNull String key) {
        String[] items = key.split("_");
        if (items.length != 2) {
            Log.e("DataTransferStation", "key format error: " + key);
            return -1;
        }

        try {
            return Integer.valueOf(items[0]);
        } catch (NumberFormatException e) {
            Log.e("DataTransferStation", "key format error: " + key);
            e.printStackTrace();
            return -1;
        }
    }

    public static class PageHelper {
        private static final String PAGE_ID = "DATA_TRANSFER_STATION_PAGE_ID";
        private boolean forcedKill;
        private Object page;
        private int pageId = -1;

        public PageHelper(@NonNull Object page) {
            this.page = page;
        }

        public void onCreate(@Nullable Bundle onSaveInstanceState) {
            forcedKill = false;

            if (onSaveInstanceState != null) {
                pageId = onSaveInstanceState.getInt(PAGE_ID);
                if (pageId == -1) {
                    throw new IllegalStateException("Unable to restore pageId");
                }
            } else {
                pageId = page.hashCode();
            }
        }

        public void onSaveInstanceState(@NonNull Bundle outState) {
            forcedKill = true;

            outState.putInt(PAGE_ID, pageId);
        }

        public void onDestroy() {
            if (!forcedKill) {
                clean(pageId);
            }
        }

        @NonNull
        @SuppressWarnings("unused")
        public String put(@NonNull String dataFlag, @NonNull Object data) {
            return DataTransferStation.put(pageId, dataFlag, data);
        }

        @Nullable
        @SuppressWarnings("unused")
        public Object get(@Nullable String key) {
            return DataTransferStation.get(key);
        }

        @Nullable
        @SuppressWarnings("unused")
        public Object remove(@Nullable String key) {
            return DataTransferStation.remove(key);
        }
    }
}
