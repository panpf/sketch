/*
 * Copyright 2013 Peng fei Pan
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

package me.panpf.sketch.sample.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_only_fragment.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.panpf.sketch.sample.BaseActivity
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.ktx.isPortraitOrientation
import me.panpf.sketch.sample.util.DeviceUtils

@BindContentView(R.layout.activity_only_fragment)
class ImageDetailActivity : BaseActivity(), PageBackgApplyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            layout_onlyFragment_content.setPadding(layout_onlyFragment_content.paddingLeft,
                    layout_onlyFragment_content.paddingTop + DeviceUtils.getStatusBarHeight(resources),
                    layout_onlyFragment_content.paddingRight, layout_onlyFragment_content.paddingBottom)
        }

        //  + DeviceUtils.getNavigationBarHeightByUiVisibility(this) 是为了兼容 MIX 2
        image_onlyFragment_background.layoutParams?.let {
            it.width = resources.displayMetrics.widthPixels
            it.height = resources.displayMetrics.heightPixels
            if (isPortraitOrientation()) {
                it.height += DeviceUtils.getWindowHeightSupplement(this)
            } else {
                it.width += DeviceUtils.getWindowHeightSupplement(this)
            }
            image_onlyFragment_background.layoutParams = it
        }

        image_onlyFragment_background.setOptions(ImageOptions.WINDOW_BACKGROUND)

        toolbar?.visibility = View.GONE

        val imageDetailFragment = ImageDetailFragment()
        imageDetailFragment.arguments = intent.extras

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_onlyFragment_content, imageDetailFragment)
                .commit()
    }

    override fun isDisableSetFitsSystemWindows(): Boolean {
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        var result = true
        try {
            result = super.dispatchTouchEvent(ev)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }

        return result
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.window_pop_enter, R.anim.window_pop_exit)
    }

    override fun onApplyBackground(imageUri: String?) {
        imageUri?.let { image_onlyFragment_background.displayImage(it) }
    }

    companion object {

        fun launch(activity: Activity, dataTransferKey: String, loadingImageOptionsInfo: String?, defaultPosition: Int) {
            val intent = Intent(activity, ImageDetailActivity::class.java)
            intent.putExtra(ImageDetailFragment.PARAM_REQUIRED_STRING_DATA_TRANSFER_KEY, dataTransferKey)
            intent.putExtra(ImageDetailFragment.PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY, loadingImageOptionsInfo)
            intent.putExtra(ImageDetailFragment.PARAM_OPTIONAL_INT_DEFAULT_POSITION, defaultPosition)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.window_push_enter, R.anim.window_push_exit)
        }
    }
}
