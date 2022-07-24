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
package com.github.panpf.sketch.util

import java.security.MessageDigest

private val hexDigits = charArrayOf(
    '0', '1', '2', '3', '4', '5', '6', '7',
    '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
)

internal fun md5(txt: String): String {
    val digest = MessageDigest.getInstance("MD5").apply {
        update(txt.toByteArray())
    }
    val md = digest.digest()
    val j = md.size
    val str = CharArray(j * 2)
    var k = 0
    for (byte0 in md) {
        str[k++] = hexDigits[byte0.toInt() ushr 4 and 0xf]
        str[k++] = hexDigits[byte0.toInt() and 0xf]
    }
    val result = String(str)
    digest.reset()
    return result
}