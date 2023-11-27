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
package com.github.panpf.sketch.core.test.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.util.md5
import com.github.panpf.tools4j.security.MessageDigestx
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessageDigestUtilsTest {

    @Test
    fun test() {
        val message = "MessageDigestUtilsTest"
        Assert.assertEquals(MessageDigestx.getMD5(message), md5(message))

        val message1 = "MessageDigestUtilsTest\$toString"
        Assert.assertEquals(MessageDigestx.getMD5(message1), md5(message1))
    }
}