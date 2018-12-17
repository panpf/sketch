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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 负责管理和匹配 {@link UriModel}，默认支持以下 uri 和 {@link UriModel}
 * <p>
 * <ul>
 * <li>http:// == {@link HttpUriModel}</li>
 * <li>https:// == {@link HttpsUriModel}</li>
 * <li>/sdcard/test.jpg == {@link FileUriModel}</li>
 * <li>file:// == {@link FileVariantUriModel}</li>
 * <li>asset:// == {@link AssetUriModel}</li>
 * <li>drawable:// == {@link DrawableUriModel}</li>
 * <li>content:// == {@link ContentUriModel}</li>
 * <li>android.resource:// == {@link AndroidResUriModel}</li>
 * <li>apk.icon:// == {@link ApkIconUriModel}</li>
 * <li>app.icon:// == {@link AppIconUriModel}</li>
 * <li>data:image/ == {@link Base64UriModel}</li>
 * <li>data:img/ == {@link Base64VariantUriModel}</li>
 * </ul>
 * <p>
 * 还可以通过 {@link #add(UriModel)} 方法扩展支持新的 uri
 * <p>
 * 通过 {@link #match(String)} 方法可以为指定 uri 匹配对应的 {@link UriModel}
 */
public class UriModelManager {

    private List<UriModel> uriModelList = new LinkedList<>();

    public UriModelManager() {
        this.uriModelList.add(new HttpUriModel());
        this.uriModelList.add(new HttpsUriModel());
        this.uriModelList.add(new FileUriModel());
        this.uriModelList.add(new FileVariantUriModel());
        this.uriModelList.add(new AssetUriModel());
        this.uriModelList.add(new DrawableUriModel());
        this.uriModelList.add(new ContentUriModel());
        this.uriModelList.add(new AndroidResUriModel());
        this.uriModelList.add(new ApkIconUriModel());
        this.uriModelList.add(new AppIconUriModel());
        this.uriModelList.add(new Base64UriModel());
        this.uriModelList.add(new Base64VariantUriModel());
    }

    /**
     * 添加一个 {@link UriModel}
     *
     * @param uriModel {@link UriModel}
     * @return {@link UriModelManager}. 为了支持链式调用
     */
    @NonNull
    public UriModelManager add(@NonNull UriModel uriModel) {
        //noinspection ConstantConditions
        if (uriModel != null) {
            uriModelList.add(uriModel);
        }
        return this;
    }

    /**
     * 添加一个 {@link UriModel} 到指定位置
     *
     * @param index    指定位置
     * @param uriModel {@link UriModel}
     * @return {@link UriModelManager}. 为了支持链式调用
     */
    @NonNull
    public UriModelManager add(int index, @NonNull UriModel uriModel) {
        //noinspection ConstantConditions
        if (uriModel != null) {
            uriModelList.add(index, uriModel);
        }
        return this;
    }

    /**
     * 删除一个 {@link UriModel}
     *
     * @param uriModel {@link UriModel}
     * @return true：存在指定 {@link UriModel} 并已删除
     */
    public boolean remove(@NonNull UriModel uriModel) {
        //noinspection ConstantConditions
        return uriModel != null && uriModelList.remove(uriModel);
    }

    /**
     * 寻找一个能解析指定 uri 的 {@link UriModel}
     *
     * @param uri 指定 uri
     * @return {@link UriModel}
     */
    @Nullable
    public UriModel match(@NonNull String uri) {
        if (!TextUtils.isEmpty(uri)) {
            for (UriModel uriModel : uriModelList) {
                if (uriModel.match(uri)) {
                    return uriModel;
                }
            }
        }

        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return "UriModelManager";
    }
}
