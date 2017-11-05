/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.panpf.sketch.util;

import java.util.LinkedList;
import java.util.Queue;

public class ObjectPool<T> {
    private static final int MAX_POOL_SIZE = 10;
    private final Object editLock = new Object();

    private Queue<T> cacheQueue;
    private ObjectFactory<T> objectFactory;
    private int maxPoolSize;

    public ObjectPool(ObjectFactory<T> objectFactory, int maxPoolSize) {
        this.objectFactory = objectFactory;
        this.maxPoolSize = maxPoolSize;
        this.cacheQueue = new LinkedList<T>();
    }

    @SuppressWarnings("unused")
    public ObjectPool(ObjectFactory<T> objectFactory) {
        this(objectFactory, MAX_POOL_SIZE);
    }

    public ObjectPool(final Class<T> classType, int maxPoolSize) {
        this(new ObjectFactory<T>() {
            @Override
            public T newObject() {
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
        this(classType, MAX_POOL_SIZE);
    }

    public T get() {
        synchronized (editLock) {
            T t = !cacheQueue.isEmpty() ? cacheQueue.poll() : objectFactory.newObject();
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

    public interface ObjectFactory<T> {
        T newObject();
    }

    public interface CacheStatus {

        @SuppressWarnings("unused")
        boolean isInCachePool();

        void setInCachePool(boolean inCachePool);
    }
}
