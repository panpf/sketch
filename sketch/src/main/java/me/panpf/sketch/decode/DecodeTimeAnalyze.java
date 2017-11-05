/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.decode;

import java.text.DecimalFormat;

import me.panpf.sketch.SLog;

public class DecodeTimeAnalyze {
    private volatile static long decodeCount;
    private volatile static long useTimeCount;
    private static DecimalFormat decimalFormat;

    public long decodeStart() {
        return System.currentTimeMillis();
    }

    public synchronized void decodeEnd(long startTime, String logName, String key) {
        long useTime = System.currentTimeMillis() - startTime;
        if ((Long.MAX_VALUE - decodeCount) < 1 || (Long.MAX_VALUE - useTimeCount) < useTime) {
            decodeCount = 0;
            useTimeCount = 0;
        }
        decodeCount++;
        useTimeCount += useTime;
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("#.##");
        }
        SLog.d(logName, "decode use time %dms, average %sms. %s",
                useTime, decimalFormat.format((double) useTimeCount / decodeCount), key);
    }
}
