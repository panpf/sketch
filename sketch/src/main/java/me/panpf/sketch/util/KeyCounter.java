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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Key 计数器
 */
public class KeyCounter {
    private AtomicInteger number;

    public KeyCounter() {
        this.number = new AtomicInteger();
    }

    /**
     * 刷新 key
     */
    public void refresh() {
        if (number.get() == Integer.MAX_VALUE) {
            number.set(0);
        } else {
            number.addAndGet(1);
        }
    }

    public int getKey() {
        return number.get();
    }
}
