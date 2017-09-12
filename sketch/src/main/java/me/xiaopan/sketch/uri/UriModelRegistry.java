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

package me.xiaopan.sketch.uri;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

import me.xiaopan.sketch.Identifier;

/**
 * 负责管理和匹配 UriModel
 */
public class UriModelRegistry implements Identifier{
    private List<UriModel> uriModelList = new LinkedList<>();

    public UriModelRegistry() {
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

    @NonNull
    public UriModelRegistry add(@NonNull UriModel uriModel) {
        //noinspection ConstantConditions
        if (uriModel != null) {
            uriModelList.add(uriModel);
        }
        return this;
    }

    @NonNull
    public UriModelRegistry add(int index, @NonNull UriModel uriModel) {
        //noinspection ConstantConditions
        if (uriModel != null) {
            uriModelList.add(index, uriModel);
        }
        return this;
    }

    public boolean remove(@NonNull UriModel uriModel) {
        //noinspection ConstantConditions
        return uriModel != null && uriModelList.remove(uriModel);
    }

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
    public String getKey() {
        return "UriModelRegistry";
    }
}
