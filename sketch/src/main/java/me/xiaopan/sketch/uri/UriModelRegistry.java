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

import java.util.LinkedList;
import java.util.List;

/**
 * 负责管理和匹配 UriModel
 */
// TODO: 2017/8/30 也许可以替代 ImagePreprocessor 了
public class UriModelRegistry {
    private List<UriModel> uriModelList = new LinkedList<>();

    public UriModelRegistry() {
        this.uriModelList.add(new HttpUriModel());
        this.uriModelList.add(new HttpsUriModel());
        this.uriModelList.add(new FileUriModel());
        this.uriModelList.add(new FileSchemeUriModel());
        this.uriModelList.add(new AssetUriModel());
        this.uriModelList.add(new DrawableUriModel());
        this.uriModelList.add(new ContentUriModel());
        this.uriModelList.add(new Base64UriModel());
        this.uriModelList.add(new Base642UriModel());
//        this.uriModelList.add(new AndroidResourceUriModel());
    }

    public UriModelRegistry add(UriModel uriModel) {
        if (uriModel != null) {
            uriModelList.add(uriModel);
        }
        return this;
    }

    public boolean remove(UriModel uriModel) {
        return uriModelList.remove(uriModel);
    }

    public UriModel match(String uri) {
        for (UriModel uriModel : uriModelList) {
            if (uriModel.match(uri)) {
                return uriModel;
            }
        }
        return null;
    }
}
