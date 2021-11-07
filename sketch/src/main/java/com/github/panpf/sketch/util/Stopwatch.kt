/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.util

import com.github.panpf.sketch.SLog
import java.text.DecimalFormat

class Stopwatch {
    private var startTime: Long = 0
    private var lastTime: Long = 0
    private var decodeCount: Long = 0
    private var useTimeCount: Long = 0
    private var builder: StringBuilder? = null
    private var logName: String? = null
    private val decimalFormat = DecimalFormat("#.##")

    fun start(logName: String) {
        this.logName = logName
        startTime = System.currentTimeMillis()
        lastTime = startTime
        builder = StringBuilder()
    }

    fun record(nodeName: String) {
        val builder = builder
        if (builder != null) {
            val currentTime = System.currentTimeMillis()
            val useTime = currentTime - lastTime
            lastTime = currentTime
            if (builder.isNotEmpty()) {
                builder.append(", ")
            }
            builder.append(nodeName).append(":").append(useTime).append("ms")
        }
    }

    fun print(requestId: String) {
        val builder = builder
        if (builder != null) {
            val totalTime = System.currentTimeMillis() - startTime
            if (builder.isNotEmpty()) {
                builder.append(". ")
            }
            builder.append("useTime=").append(totalTime).append("ms")
            if (Long.MAX_VALUE - decodeCount < 1 || Long.MAX_VALUE - useTimeCount < totalTime) {
                decodeCount = 0
                useTimeCount = 0
            }
            decodeCount++
            useTimeCount += totalTime
            if (SLog.isLoggable(SLog.VERBOSE)) {
                SLog.vmf(
                    logName!!,
                    "%s, average=%sms. %s",
                    builder.toString(),
                    decimalFormat.format(useTimeCount.toDouble() / decodeCount),
                    requestId
                )
            }
            this@Stopwatch.builder = null
        }
    }
}