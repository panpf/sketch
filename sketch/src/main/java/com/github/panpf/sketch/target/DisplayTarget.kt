package com.github.panpf.sketch.target

import android.graphics.drawable.Drawable
import androidx.annotation.MainThread

interface DisplayTarget : Target {

    /**
     * Called when the request starts.
     */
    @MainThread
    fun onStart(placeholder: Drawable?) {}

    /**
     * Called if the request completes successfully.
     */
    @MainThread
    fun onSuccess(result: Drawable) {}

    /**
     * Called if an error occurs while executing the request.
     */
    @MainThread
    fun onError(error: Drawable?) {}
}