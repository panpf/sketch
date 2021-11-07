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
package com.github.panpf.sketch

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.Sketch.Companion.with
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RedisplayListener
import com.github.panpf.sketch.viewfun.FunctionPropertyView

open class SketchImageView : FunctionPropertyView {

    /**
     * 获取选项 KEY，可用于组装缓存 KEY
     *
     * @see com.github.panpf.sketch.util.SketchUtils.makeRequestKey
     */
    val optionsKey: String
        get() {
            return displayCache?.options?.makeKey() ?: options.makeKey()
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int)
            : super(context, attrs, defStyle)

    override fun displayImage(uri: String?): DisplayRequest? {
        return with(context).display(uri.orEmpty(), this).commit()
    }

    override fun displayResourceImage(@DrawableRes drawableResId: Int?): DisplayRequest? {
        return if (drawableResId != null) {
            with(context).displayFromResource(drawableResId, this).commit()
        } else {
            with(context).display("", this).commit()
        }
    }

    override fun displayAssetImage(assetFileName: String?): DisplayRequest? {
        return if (assetFileName?.isNotEmpty() == true) {
            with(context).displayFromAsset(assetFileName.orEmpty(), this).commit()
        } else {
            with(context).display("", this).commit()
        }
    }

    override fun displayContentImage(uri: String?): DisplayRequest? {
        return with(context).displayFromContent(uri.orEmpty(), this).commit()
    }

    override fun redisplay(listener: RedisplayListener?): Boolean {
        val displayCache = displayCache
        if (displayCache?.uri == null) {
            return false
        }
        listener?.onPreCommit(displayCache.uri!!, displayCache.options)
        with(context)
            .display(displayCache.uri!!, this)
            .options(displayCache.options)
            .commit()
        return true
    }
}