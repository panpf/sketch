/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("WeakerAccess")
public class ObjectPool<T> {
    private static final int MAX_POOL_SIZE = 10;
    private final Object editLock = new Object();

    @NonNull
    private Queue<T> cacheQueue;
    @NonNull
    private ObjectFactory<T> objectFactory;
    private int maxPoolSize;

    public ObjectPool(@NonNull ObjectFactory<T> objectFactory, int maxPoolSize) {
        this.objectFactory = objectFactory;
        this.maxPoolSize = maxPoolSize;
        this.cacheQueue = new LinkedList<T>();
    }

    public ObjectPool(@NonNull ObjectFactory<T> objectFactory) {
        this(objectFactory, MAX_POOL_SIZE);
    }

    public ObjectPool(@NonNull final Class<T> classType, int maxPoolSize) {
        this(new ObjectFactory<T>() {
            @NonNull
            @Override
            public T newObject() {
                try {
                    return classType.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }, maxPoolSize);
    }

    public ObjectPool(@NonNull final Class<T> classType) {
        this(classType, MAX_POOL_SIZE);
    }

    @NonNull
    public T get() {
        synchronized (editLock) {
            T t = !cacheQueue.isEmpty() ? cacheQueue.poll() : objectFactory.newObject();
            if (t instanceof CacheStatus) {
                ((CacheStatus) t).setInCachePool(false);
            }
            return t;
        }
    }

    public void put(@NonNull T t) {
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

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

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
        @NonNull
        T newObject();
    }

    public interface CacheStatus {

        boolean isInCachePool();

        void setInCachePool(boolean inCachePool);
    }
}
