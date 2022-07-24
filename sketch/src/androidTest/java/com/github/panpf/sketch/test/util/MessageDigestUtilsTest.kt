package com.github.panpf.sketch.test.util

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