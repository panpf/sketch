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
package com.github.panpf.sketch.zoom.tile

import android.content.Context
import java.util.LinkedList

/**
 * 碎片管理器
 */
// 将
// 如何确定每个 block 的大小?, 根据 blockBaseNumber 将 visibleRect 区域横向竖向等分
// 然后根据 block 大小，确定将原图分成多少块
class TileManager(
    context: Context,
    val blockBaseNumber: Int = 3
) {


    val tileList = LinkedList<Tile>()

    interface Callback {
        fun decodeBlock(block: Tile)
        fun recycleBlock(block: Tile)
    }
}