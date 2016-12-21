package me.xiaopan.sketch.util;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockPool {

    //    private Map<String, ReentrantLock> loadLockMap;
    private Map<String, ReentrantLock> diskCacheEditLockMap;

//    public synchronized ReentrantLock getLoadLock(String memoryCacheKey) {
//        ReentrantLock lock = loadLockMap.get(memoryCacheKey);
//        if (lock == null) {
//            lock = new ReentrantLock();
//            loadLockMap.put(memoryCacheKey, lock);
//        }
//        return lock;
//    }

    public synchronized ReentrantLock getDiskCacheEditLock(String downloadUrl) {
        if (diskCacheEditLockMap == null) {
            synchronized (this) {
                if (diskCacheEditLockMap == null) {
                    diskCacheEditLockMap = new WeakHashMap<String, ReentrantLock>();
                }
            }
        }

        ReentrantLock lock = diskCacheEditLockMap.get(downloadUrl);
        if (lock == null) {
            lock = new ReentrantLock();
            diskCacheEditLockMap.put(downloadUrl, lock);
        }
        return lock;
    }
}
