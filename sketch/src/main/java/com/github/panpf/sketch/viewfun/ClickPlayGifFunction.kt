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
package com.github.panpf.sketch.viewfun

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.drawable.SketchGifDrawable
import com.github.panpf.sketch.request.DisplayOptions
import com.github.panpf.sketch.request.RedisplayListener
import com.github.panpf.sketch.state.OldStateImage
import com.github.panpf.sketch.util.SketchUtils

/**
 * 点击播放 gif 功能
 */
class ClickPlayGifFunction(private val view: FunctionCallbackView) : ViewFunction() {

    private var playIconDrawable: Drawable? = null
    var isClickable = false
        private set
    private var lastDrawable: Drawable? = null
    private var cacheViewWidth = 0
    private var cacheViewHeight = 0
    private var iconDrawLeft = 0
    private var iconDrawTop = 0
    private var redisplayListener: PlayGifRedisplayListener? = null

    override fun onDraw(canvas: Canvas) {
        val playIconDrawable = playIconDrawable ?: return
        val drawable = view.drawable
        if (drawable !== lastDrawable) {
            isClickable = canClickPlay(drawable)
            lastDrawable = drawable
        }
        if (!isClickable) {
            return
        }
        if (cacheViewWidth != view.width || cacheViewHeight != view.height) {
            cacheViewWidth = view.width
            cacheViewHeight = view.height
            val availableWidth =
                view.width - view.paddingLeft - view.paddingRight - playIconDrawable.bounds.width()
            val availableHeight =
                view.height - view.paddingTop - view.paddingBottom - playIconDrawable.bounds.height()
            iconDrawLeft = view.paddingLeft + availableWidth / 2
            iconDrawTop = view.paddingTop + availableHeight / 2
        }
        canvas.save()
        canvas.translate(iconDrawLeft.toFloat(), iconDrawTop.toFloat())
        playIconDrawable.draw(canvas)
        canvas.restore()
    }

    /**
     * 点击事件
     *
     * @return true：已经消费了，不必往下传了
     */
    fun onClick(): Boolean {
        if (isClickable) {
            if (redisplayListener == null) {
                redisplayListener = PlayGifRedisplayListener()
            }
            view.redisplay(redisplayListener)
            return true
        }
        return false
    }

    private fun canClickPlay(newDrawable: Drawable?): Boolean {
        if (newDrawable == null) {
            return false
        }
        val endDrawable = SketchUtils.getLastDrawable(newDrawable)
        return SketchUtils.isGifImage(endDrawable) && endDrawable !is SketchGifDrawable
    }

    fun setPlayIconDrawable(playIconDrawable: Drawable): Boolean {
        if (this.playIconDrawable === playIconDrawable) {
            return false
        }
        playIconDrawable.setBounds(
            0,
            0,
            playIconDrawable.intrinsicWidth,
            playIconDrawable.intrinsicHeight
        )
        this.playIconDrawable = playIconDrawable
        return true
    }

    private class PlayGifRedisplayListener : RedisplayListener {
        override fun onPreCommit(cacheUri: String, cacheOptions: DisplayOptions) {
            cacheOptions.loadingImage = OldStateImage()
            cacheOptions.isDecodeGifImage = true
        }
    }
}