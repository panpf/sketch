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

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest

object SketchMD5Utils {
    /**
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
     */
    private val hexDigits =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    private val digestObjectPool = ObjectPool({ MessageDigest.getInstance("MD5") }, 3)

    @Throws(IOException::class)
    fun md5(file: File?): String {
        val digest = digestObjectPool.get()
        var inputStream: FileInputStream? = null
        return try {
            inputStream = FileInputStream(file)
            val reads = ByteArray(8192)
            var length: Int
            while (inputStream.read(reads).also { length = it } != -1) {
                digest.update(reads, 0, length)
            }
            val bytes = digest.digest()
            bufferToHex(bytes, 0, bytes.size)
        } finally {
            SketchUtils.close(inputStream)
            digest.reset()
            digestObjectPool.put(digest)
        }
    }

    @JvmStatic
    fun md5(txt: String): String {
        val digest = digestObjectPool.get()
        val textBytes = txt.toByteArray()
        digest.update(textBytes)
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
        digestObjectPool.put(digest)
        return result
    }

    private fun bufferToHex(bytes: ByteArray, m: Int, n: Int): String {
        val stringbuffer = StringBuffer(2 * n)
        val k = m + n
        for (l in m until k) {
            appendHexPair(bytes[l], stringbuffer)
        }
        return stringbuffer.toString()
    }

    private fun appendHexPair(bt: Byte, stringbuffer: StringBuffer) {
        // 取字节中高 4 位的数字转换, >>>
        val c0 = hexDigits[bt.toInt() and 0xf0 shr 4]
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        // 取字节中低 4 位的数字转换
        val c1 = hexDigits[bt.toInt() and 0xf]
        stringbuffer.append(c0)
        stringbuffer.append(c1)
    }
}