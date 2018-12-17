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
import android.text.TextUtils;

import java.io.File;

import me.panpf.sketch.datasource.DataSource;
import me.panpf.sketch.datasource.FileDataSource;
import me.panpf.sketch.request.DownloadResult;

public class FileUriModel extends UriModel {

    public static final String SCHEME = "/";

    @Override
    protected boolean match(@NonNull String uri) {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME);
    }

    @NonNull
    @Override
    public DataSource getDataSource(@NonNull Context context, @NonNull String uri, DownloadResult downloadResult) {
        return new FileDataSource(new File(uri));
    }
}
