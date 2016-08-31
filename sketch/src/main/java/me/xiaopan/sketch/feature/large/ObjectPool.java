package me.xiaopan.sketch.feature.large;

import java.util.LinkedList;
import java.util.Queue;

public class ObjectPool<T> {
    private final Object editLock = new Object();

    private Queue<T> cacheQueue;
    private NewItemCallback<T> callback;
    private int maxPoolSize;

    public ObjectPool(NewItemCallback<T> callback, int maxPoolSize) {
        this.callback = callback;
        this.maxPoolSize = maxPoolSize;
        this.cacheQueue = new LinkedList<T>();
    }

    public ObjectPool(NewItemCallback<T> callback) {
        this(callback, 20);
    }

    public ObjectPool(final Class<T> classType, int maxPoolSize) {
        this(new NewItemCallback<T>() {
            @Override
            public T newItem() {
                try {
                    return classType.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, maxPoolSize);
    }

    @SuppressWarnings("unused")
    public ObjectPool(final Class<T> classType) {
        this(classType, 50);
    }

    public T get() {
        synchronized (editLock) {
            T t = !cacheQueue.isEmpty() ? cacheQueue.poll() : callback.newItem();
            if (t instanceof CacheStatus) {
                ((CacheStatus) t).setInCachePool(false);
            }
            return t;
        }
    }

    public void put(T t) {
        synchronized (editLock) {
            if (cacheQueue.size() < maxPoolSize) {
                if (t instanceof CacheStatus) {
                    ((CacheStatus) t).setInCachePool(true);
                }
                cacheQueue.add(t);
            }
        }
    }

    public void clear() {
        synchronized (editLock) {
            cacheQueue.clear();
        }
    }

    @SuppressWarnings("unused")
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    @SuppressWarnings("unused")
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;

        synchronized (editLock) {
            if (cacheQueue.size() > maxPoolSize) {
                int number = maxPoolSize - cacheQueue.size();
                int count = 0;
                while (count++ < number) {
                    cacheQueue.poll();
                }
            }
        }
    }

    public int size() {
        synchronized (editLock) {
            return cacheQueue.size();
        }
    }

    public interface NewItemCallback<T> {
        T newItem();
    }

    public interface CacheStatus {

        @SuppressWarnings("unused")
        boolean isInCachePool();

        void setInCachePool(boolean inCachePool);
    }
}
