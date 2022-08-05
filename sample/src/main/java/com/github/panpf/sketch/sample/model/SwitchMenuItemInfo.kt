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
package com.github.panpf.sketch.sample.model

class SwitchMenuItemInfo<T>(
    private val values: Array<T>,
    initValue: T,
    private val titles: Array<String>?,
    private val iconResIds: Array<Int>?,
    override val showAsAction: Int,
    private val onChangedListener: (oldValue: T, newValue: T) -> Unit,
) : MenuItemInfo {
    private var currentIndex = values.indexOf(initValue)
    private val nextIndex: Int
        get() = (currentIndex + 1) % values.size

    override val title: String
        get() = titles?.get(nextIndex).orEmpty()

    override val iconResId: Int?
        get() = iconResIds?.get(nextIndex)

    fun click() {
        val nextIndex = nextIndex
        val oldValue = values[currentIndex]
        val newValue = values[nextIndex]
        currentIndex = nextIndex
        onChangedListener(oldValue, newValue)
    }
}