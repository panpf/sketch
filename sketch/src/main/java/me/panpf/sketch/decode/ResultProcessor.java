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

import me.panpf.sketch.request.LoadRequest;

/**
 * 解码完成后处理图片
 */
public interface ResultProcessor {

    /**
     * 后期处理
     *
     * @param request {@link LoadRequest}
     * @param result  {@link DecodeResult}
     * @throws ProcessException 后期处理失败了
     */
    void process(LoadRequest request, DecodeResult result) throws ProcessException;
}
