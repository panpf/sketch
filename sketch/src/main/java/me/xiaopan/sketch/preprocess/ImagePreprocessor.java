/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

import java.util.LinkedList;
import java.util.List;

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.request.UriInfo;

/**
 * 图片预处理器，可对特殊类型的图片先转换再读取
 * <br>
 * <br>现支持以下几种特殊文件预处理
 * <ul>
 * <li>1. 读取APK文件的图标以及根据包名和版本号</li>
 * <li>2. 读取已安装APP的图标</li>
 * <li>3. 解析Base64格式的图片</li>
 * </ul>
 */
public class ImagePreprocessor implements Identifier {

    protected String key = "ImagePreprocessor";

    private List<Preprocessor> preprocessorList = new LinkedList<>();

    public ImagePreprocessor() {
        preprocessorList.add(new ApkIconPreprocessor());
        preprocessorList.add(new InstalledAppIconPreprocessor());
        preprocessorList.add(new Base64ImagePreprocessor());
    }

    /**
     * 添加一个预处理器
     *
     * @param index        在指定位置添加
     * @param preprocessor Preprocessor
     * @return false：已存在
     */
    @SuppressWarnings("unused")
    public boolean addPreprocessor(int index, Preprocessor preprocessor) {
        if (preprocessorList.contains(preprocessor)) {
            return false;
        }

        preprocessorList.add(index, preprocessor);
        return true;
    }

    /**
     * 添加一个预处理器
     *
     * @param preprocessor Preprocessor
     * @return false：已存在
     */
    @SuppressWarnings("unused")
    public boolean addPreprocessor(Preprocessor preprocessor) {
        if (preprocessorList.contains(preprocessor)) {
            return false;
        }

        preprocessorList.add(preprocessor);
        return true;
    }

    /**
     * 根据uri判断是否需要预处理
     *
     * @param context Context
     * @param uriInfo 图片uri
     * @return true：需要预处理，紧接着会调用 {@link #process(Context, UriInfo)} 方法处理
     */
    public boolean match(Context context, UriInfo uriInfo) {
        for (Preprocessor preprocessor : preprocessorList) {
            if (preprocessor.match(context, uriInfo)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 执行预处理，返回处理结果
     *
     * @param context Context
     * @param uriInfo 图片uri
     * @return 预处理结果
     */
    public PreProcessResult process(Context context, UriInfo uriInfo) {
        for (Preprocessor preprocessor : preprocessorList) {
            if (preprocessor.match(context, uriInfo)) {
                return preprocessor.process(context, uriInfo);
            }
        }

        return null;
    }

    @Override
    public String getKey() {
        return key;
    }
}
