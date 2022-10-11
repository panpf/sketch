/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.SmallStateViewBinding
import com.github.panpf.tools4a.network.ktx.isNetworkConnected
import org.apache.http.conn.ConnectTimeoutException
import java.io.FileNotFoundException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SmallStateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding = SmallStateViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        if (isInEditMode) {
            binding.smallStateLoading.isVisible = true
            binding.smallStateLoading.alpha = 0.3f
            binding.smallStateError.isVisible = false
        } else {
            isVisible = false
        }
    }

    fun loading() {
        binding.smallStateError.isVisible = false
        binding.smallStateLoading.isVisible = true
        isVisible = true
    }

    fun error(message: String? = null, block: (ErrorConfig.() -> Unit)? = null) {
        ErrorConfig(binding).apply {
            icon(R.drawable.ic_error)
            message(message ?: "Load failed")
            block?.invoke(this)
        }
        binding.smallStateLoading.isVisible = false
        binding.smallStateError.isVisible = true
        isVisible = true
    }

    fun error(@StringRes messageResId: Int? = null, block: (ErrorConfig.() -> Unit)? = null) {
        error(messageResId?.let { resources.getString(it) }, block)
    }

    fun error(e: Throwable, block: (ErrorConfig.() -> Unit)? = null) {
        val message = when (e) {
            is SecurityException -> "Network permission error：${e.message}"
            is UnknownHostException -> if (context.isNetworkConnected()) "Unknown Host error" else "No network connection"
            is SocketTimeoutException, is ConnectTimeoutException -> "Network connection timeout"
            is FileNotFoundException -> "Incorrect URL"
            else -> "Unknown error: ${e.message}"
        }
        error(message, block)
    }

    fun error(block: (ErrorConfig.() -> Unit)? = null) {
        error(null as String?, block)
    }

    fun errorWithRetry(message: String? = null, onClick: () -> Unit) {
        error(message) {
            action("Retry") {
                onClick()
            }
        }
    }

    fun errorWithRetry(@StringRes messageResId: Int? = null, onClick: () -> Unit) {
        error(messageResId) {
            action("Retry") {
                onClick()
            }
        }
    }

    fun errorWithRetry(e: Throwable, onClick: () -> Unit) {
        error(e) {
            action("Retry") {
                onClick()
            }
        }
    }

    fun errorWithRetry(onClick: () -> Unit) {
        error {
            action("Retry") {
                onClick()
            }
        }
    }

    fun empty(message: String? = null, block: (ErrorConfig.() -> Unit)? = null) {
        ErrorConfig(binding).apply {
            icon(R.drawable.ic_cloudy)
            message(message ?: "No content")
            block?.invoke(this)
        }
        binding.smallStateLoading.isVisible = false
        binding.smallStateError.isVisible = true
        isVisible = true
    }

    fun empty(@StringRes messageResId: Int? = null, block: (ErrorConfig.() -> Unit)? = null) {
        empty(messageResId?.let { resources.getString(it) }, block)
    }

    fun empty(block: (ErrorConfig.() -> Unit)? = null) {
        empty(null as String?, block)
    }

    fun gone() {
        binding.smallStateLoading.isVisible = false
        binding.smallStateError.isVisible = false
        isVisible = false
    }

    class ErrorConfig(private val binding: SmallStateViewBinding) {

        init {
            binding.smallStateErrorIcon.isVisible = false
            binding.smallStateErrorText.isVisible = false
            binding.smallStateErrorAction.isVisible = false
        }

        fun icon(drawable: Drawable) {
            binding.smallStateErrorIcon.setImageDrawable(drawable)
            binding.smallStateErrorIcon.isVisible = true
        }

        fun icon(@DrawableRes resId: Int) {
            binding.smallStateErrorIcon.setImageResource(resId)
            binding.smallStateErrorIcon.isVisible = true
        }

        fun message(name: String) {
            binding.smallStateErrorText.text = name
            binding.smallStateErrorText.isVisible = true
        }

        fun message(@StringRes nameResId: Int) {
            binding.smallStateErrorText.setText(nameResId)
            binding.smallStateErrorText.isVisible = true
        }

        fun action(name: String, onClick: () -> Unit) {
            binding.smallStateErrorAction.text = name
            binding.smallStateErrorAction.setOnClickListener {
                onClick()
            }
            binding.smallStateErrorAction.isVisible = true
        }

        fun action(@StringRes nameResId: Int, onClick: () -> Unit) {
            binding.smallStateErrorAction.setText(nameResId)
            binding.smallStateErrorAction.setOnClickListener {
                onClick()
            }
            binding.smallStateErrorAction.isVisible = true
        }
    }
}