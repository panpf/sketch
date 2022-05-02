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
import com.github.panpf.sketch.sample.databinding.StateViewBinding
import com.github.panpf.tools4a.network.ktx.isNetworkConnected
import org.apache.http.conn.ConnectTimeoutException
import java.io.FileNotFoundException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class StateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding = StateViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        if (isInEditMode) {
            binding.stateLoading.isVisible = true
            binding.stateLoading.alpha = 0.3f
            binding.stateError.isVisible = false
        } else {
            isVisible = false
        }
    }

    fun loading() {
        binding.stateError.isVisible = false
        binding.stateLoading.isVisible = true
        isVisible = true
    }

    fun error(message: String? = null, block: (ErrorConfig.() -> Unit)? = null) {
        ErrorConfig(binding).apply {
            icon(R.drawable.ic_error)
            message(message ?: "Load failed")
            block?.invoke(this)
        }
        binding.stateLoading.isVisible = false
        binding.stateError.isVisible = true
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
        binding.stateLoading.isVisible = false
        binding.stateError.isVisible = true
        isVisible = true
    }

    fun empty(@StringRes messageResId: Int? = null, block: (ErrorConfig.() -> Unit)? = null) {
        empty(messageResId?.let { resources.getString(it) }, block)
    }

    fun empty(block: (ErrorConfig.() -> Unit)? = null) {
        empty(null as String?, block)
    }

    fun gone() {
        binding.stateLoading.isVisible = false
        binding.stateError.isVisible = false
        isVisible = false
    }

    class ErrorConfig(private val binding: StateViewBinding) {

        init {
            binding.stateErrorIcon.isVisible = false
            binding.stateErrorText.isVisible = false
            binding.stateErrorAction.isVisible = false
        }

        fun icon(drawable: Drawable) {
            binding.stateErrorIcon.setImageDrawable(drawable)
            binding.stateErrorIcon.isVisible = true
        }

        fun icon(@DrawableRes resId: Int) {
            binding.stateErrorIcon.setImageResource(resId)
            binding.stateErrorIcon.isVisible = true
        }

        fun message(name: String) {
            binding.stateErrorText.text = name
            binding.stateErrorText.isVisible = true
        }

        fun message(@StringRes nameResId: Int) {
            binding.stateErrorText.setText(nameResId)
            binding.stateErrorText.isVisible = true
        }

        fun action(name: String, onClick: () -> Unit) {
            binding.stateErrorAction.text = name
            binding.stateErrorAction.setOnClickListener {
                onClick()
            }
            binding.stateErrorAction.isVisible = true
        }

        fun action(@StringRes nameResId: Int, onClick: () -> Unit) {
            binding.stateErrorAction.setText(nameResId)
            binding.stateErrorAction.setOnClickListener {
                onClick()
            }
            binding.stateErrorAction.isVisible = true
        }
    }
}