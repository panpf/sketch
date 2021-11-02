/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.uri

import android.text.TextUtils
import java.util.*

/**
 * 负责管理和匹配 [UriModel]，默认支持以下 uri 和 [UriModel]
 *
 *
 *
 *  * http:// == [HttpUriModel]
 *  * https:// == [HttpsUriModel]
 *  * /sdcard/test.jpg == [FileUriModel]
 *  * file:// == [FileVariantUriModel]
 *  * asset:// == [AssetUriModel]
 *  * drawable:// == [DrawableUriModel]
 *  * content:// == [ContentUriModel]
 *  * android.resource:// == [AndroidResUriModel]
 *  * apk.icon:// == [ApkIconUriModel]
 *  * app.icon:// == [AppIconUriModel]
 *  * data:image/ == [Base64UriModel]
 *  * data:img/ == [Base64VariantUriModel]
 *
 *
 *
 * 还可以通过 [.add] 方法扩展支持新的 uri
 *
 *
 * 通过 [.match] 方法可以为指定 uri 匹配对应的 [UriModel]
 */
class UriModelManager {
    private val uriModelList: MutableList<UriModel> = LinkedList()

    init {
        uriModelList.add(HttpUriModel())
        uriModelList.add(HttpsUriModel())
        uriModelList.add(FileUriModel())
        uriModelList.add(FileVariantUriModel())
        uriModelList.add(AssetUriModel())
        uriModelList.add(DrawableUriModel())
        uriModelList.add(ContentUriModel())
        uriModelList.add(AndroidResUriModel())
        uriModelList.add(ApkIconUriModel())
        uriModelList.add(AppIconUriModel())
        uriModelList.add(Base64UriModel())
        uriModelList.add(Base64VariantUriModel())
    }

    /**
     * 添加一个 [UriModel]
     *
     * @param uriModel [UriModel]
     * @return [UriModelManager]. 为了支持链式调用
     */
    fun add(uriModel: UriModel): UriModelManager {
        uriModelList.add(uriModel)
        return this
    }

    /**
     * 添加一个 [UriModel] 到指定位置
     *
     * @param index    指定位置
     * @param uriModel [UriModel]
     * @return [UriModelManager]. 为了支持链式调用
     */
    fun add(index: Int, uriModel: UriModel): UriModelManager {
        uriModelList.add(index, uriModel)
        return this
    }

    /**
     * 删除一个 [UriModel]
     *
     * @param uriModel [UriModel]
     * @return true：存在指定 [UriModel] 并已删除
     */
    fun remove(uriModel: UriModel): Boolean {
        return uriModelList.remove(uriModel)
    }

    /**
     * 寻找一个能解析指定 uri 的 [UriModel]
     *
     * @param uri 指定 uri
     * @return [UriModel]
     */
    fun match(uri: String): UriModel? {
        if (!TextUtils.isEmpty(uri)) {
            for (uriModel in uriModelList) {
                if (uriModel.match(uri)) {
                    return uriModel
                }
            }
        }
        return null
    }

    override fun toString(): String {
        return "UriModelManager"
    }
}