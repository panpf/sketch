/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.preprocess;

import android.content.Context;

import me.xiaopan.sketch.request.UriInfo;

public interface Preprocessor {
    /**
     * 根据uri判断是否需要预处理
     *
     * @param context Context
     * @param uriInfo 图片uri
     * @return true：需要预处理，紧接着会调用 {@link #process(Context, UriInfo)} 方法处理
     */
    boolean match(Context context, UriInfo uriInfo);

    /**
     * 执行预处理，返回处理结果
     *
     * @param context Context
     * @param uriInfo 图片uri
     * @return 预处理结果
     */
    PreProcessResult process(Context context, UriInfo uriInfo);
}
