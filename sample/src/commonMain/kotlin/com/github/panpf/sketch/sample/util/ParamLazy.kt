/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.sample.util

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

class ParamLazy<P, R>(private val callback: Callback<P, R>) {

    private var instance: R? = null
    private val lock = SynchronizedObject()

    fun get(p: P): R {
        return synchronized(lock) {
            instance ?: synchronized(lock) {
                instance ?: callback.createInstantiate(p).apply {
                    instance = this
                }
            }
        }
    }

    fun interface Callback<P, R> {
        fun createInstantiate(p: P): R
    }
}