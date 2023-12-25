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
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.github.panpf.sketch.sample.databinding.ViewSmallStateBinding
import com.github.panpf.tools4a.network.ktx.isNetworkConnected
import org.apache.http.conn.ConnectTimeoutException
import java.io.FileNotFoundException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SmallStateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding = ViewSmallStateBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        if (isInEditMode) {
            binding.loadingLayout.isVisible = true
            binding.loadingLayout.alpha = 0.3f
            binding.emptyLayout.isVisible = false
            binding.errorLayout.isVisible = false
        } else {
            isVisible = false
        }
    }

    fun loading() {
        binding.loadingLayout.isVisible = true
        binding.emptyLayout.isVisible = false
        binding.errorLayout.isVisible = false
        isVisible = true
    }

    fun error(block: (ErrorConfig.() -> Unit)? = null) {
        ErrorConfig(binding).apply {
            message("Load failed")
            block?.invoke(this)
        }
        binding.loadingLayout.isVisible = false
        binding.emptyLayout.isVisible = false
        binding.errorLayout.isVisible = true
        isVisible = true
    }

    fun empty(block: (EmptyConfig.() -> Unit)? = null) {
        EmptyConfig(binding).apply {
            message("No Content")
            block?.invoke(this)
        }
        binding.loadingLayout.isVisible = false
        binding.emptyLayout.isVisible = true
        binding.errorLayout.isVisible = false
        isVisible = true
    }

    fun gone() {
        binding.loadingLayout.isVisible = false
        binding.emptyLayout.isVisible = false
        binding.errorLayout.isVisible = false
        isVisible = false
    }

    class EmptyConfig(private val binding: ViewSmallStateBinding) {

        init {
            binding.emptyMessageText.isVisible = false
            binding.emptyActionButton.isVisible = false
        }

        fun message(name: String) {
            binding.emptyMessageText.text = name
            binding.emptyMessageText.isVisible = true
        }

        fun message(@StringRes resId: Int) {
            message(binding.root.context.resources.getString(resId))
        }

        fun action(name: String, onClick: () -> Unit) {
            binding.emptyActionButton.text = name
            binding.emptyActionButton.setOnClickListener {
                onClick()
            }
            binding.emptyActionButton.isVisible = true
        }

        fun action(@StringRes resId: Int, onClick: () -> Unit) {
            action(binding.root.context.resources.getString(resId), onClick)
        }
    }

    class ErrorConfig(private val binding: ViewSmallStateBinding) {

        init {
            binding.errorMessageText.isVisible = false
            binding.errorActionButton.isVisible = false
        }

        fun message(message: String) {
            binding.errorMessageText.text = message
            binding.errorMessageText.isVisible = true
        }

        fun message(@StringRes resId: Int) {
            message(binding.root.context.resources.getString(resId))
        }

        fun message(e: Throwable) {
            val message = when (e) {
                is SecurityException -> "Network permission error：${e.message}"
                is UnknownHostException -> if (binding.root.context.isNetworkConnected())
                    "Unknown Host error" else "No network connection"

                is SocketTimeoutException, is ConnectTimeoutException -> "Network connection timeout"
                is FileNotFoundException -> "Incorrect URL"
                else -> "Unknown error: ${e.message}"
            }
            message(message)
        }

        fun action(name: String, onClick: () -> Unit) {
            binding.errorActionButton.text = name
            binding.errorActionButton.setOnClickListener {
                onClick()
            }
            binding.errorActionButton.isVisible = true
        }

        fun action(@StringRes resId: Int, onClick: () -> Unit) {
            action(binding.root.context.resources.getString(resId), onClick)
        }

        fun retryAction(onClick: () -> Unit) {
            action("Retry", onClick)
        }
    }
}