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
package com.github.panpf.sketch.decode

import com.github.panpf.sketch.SLog
import java.text.DecimalFormat

class DecodeTimeAnalyze {
    fun decodeStart(): Long {
        return System.currentTimeMillis()
    }

    @Synchronized
    fun decodeEnd(startTime: Long, logName: String, key: String?) {
        val useTime = System.currentTimeMillis() - startTime
        if (Long.MAX_VALUE - decodeCount < 1 || Long.MAX_VALUE - useTimeCount < useTime) {
            decodeCount = 0
            useTimeCount = 0
        }
        decodeCount++
        useTimeCount += useTime
        val decimalFormat = decimalFormat ?: DecimalFormat("#.##").apply {
            DecodeTimeAnalyze.decimalFormat = this
        }
        SLog.dmf(
            logName, "decode use time %dms, average %sms. %s",
            useTime, decimalFormat.format(useTimeCount.toDouble() / decodeCount), key!!
        )
    }

    companion object {
        @Volatile
        private var decodeCount: Long = 0

        @Volatile
        private var useTimeCount: Long = 0
        private var decimalFormat: DecimalFormat? = null
    }
}