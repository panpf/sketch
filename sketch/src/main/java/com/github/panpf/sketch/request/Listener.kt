/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.request

/**
 * 请求监听器，可监听开始、失败、取消
 */
// todo 提供 SimpleListener
interface Listener {
    fun onStarted()

    /**
     * 失败
     *
     * @param cause 原因
     */
    fun onError(cause: ErrorCause)

    /**
     * 取消
     *
     * @param cause 原因
     */
    fun onCanceled(cause: CancelCause)
}