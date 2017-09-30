package me.xiaopan.sketchsample.util;

import java.util.HashMap;
import java.util.Map;

public class DataTransferStation {
    private static final Map<String, Object> OBJECT_MAP = new HashMap<>();

    private DataTransferStation() {
    }

    public static String put(Object o) {
        if (o == null) {
            return null;
        }

        String key = "DATA_TRANSFER_KEY_" + System.currentTimeMillis();
        OBJECT_MAP.put(key, o);
        return key;
    }

    public static Object remove(String key) {
        return OBJECT_MAP.remove(key);
    }
}
