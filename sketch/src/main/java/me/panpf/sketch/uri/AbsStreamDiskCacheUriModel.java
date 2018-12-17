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

package me.panpf.sketch.uri;

import android.content.Context;
import androidx.annotation.NonNull;

import java.io.InputStream;
import java.io.OutputStream;

import me.panpf.sketch.util.SketchUtils;

public abstract class AbsStreamDiskCacheUriModel extends AbsDiskCacheUriModel<InputStream> {

    @Override
    protected final void outContent(@NonNull InputStream inputStream, @NonNull OutputStream outputStream) throws Exception {
        byte[] buffer = new byte[8 * 1024];
        int realLength;
        while (true) {
            realLength = inputStream.read(buffer);
            if (realLength < 0) {
                break;
            }
            outputStream.write(buffer, 0, realLength);
        }
    }

    @Override
    protected final void closeContent(@NonNull InputStream inputStream, @NonNull Context context) {
        SketchUtils.close(inputStream);
    }
}
