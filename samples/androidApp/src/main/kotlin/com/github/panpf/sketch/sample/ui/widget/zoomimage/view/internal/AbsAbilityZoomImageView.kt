/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.zoomimage.view.sketch.internal

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Keep
import com.github.panpf.sketch.ability.ViewAbility
import com.github.panpf.sketch.ability.ViewAbilityContainer
import com.github.panpf.sketch.ability.ViewAbilityManager
import com.github.panpf.sketch.ability.internal.RealViewAbilityManager
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.zoomimage.ZoomImageView

/**
 * ImageView base class that supports [ViewAbility]
 *
 * Copy from Sketch
 */
abstract class AbsAbilityZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ZoomImageView(context, attrs, defStyle), ViewAbilityContainer {

    private var viewAbilityManager: ViewAbilityManager? = null

    override val viewAbilityList: List<ViewAbility>
        get() = viewAbilityManager?.viewAbilityList ?: emptyList()

    init {
        @Suppress("LeakingThis")
        viewAbilityManager = RealViewAbilityManager(this, this)
    }

    final override fun addViewAbility(viewAbility: ViewAbility) {
        viewAbilityManager?.addViewAbility(viewAbility)
    }

    override fun removeViewAbility(viewAbility: ViewAbility) {
        viewAbilityManager?.removeViewAbility(viewAbility)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewAbilityManager?.onAttachedToWindow()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        viewAbilityManager?.onVisibilityChanged(changedView, visibility)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewAbilityManager?.onLayout(changed, left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewAbilityManager?.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        viewAbilityManager?.onDrawBefore(canvas)
        super.onDraw(canvas)
        viewAbilityManager?.onDraw(canvas)
    }

    override fun onDrawForeground(canvas: Canvas) {
        viewAbilityManager?.onDrawForegroundBefore(canvas)
        super.onDrawForeground(canvas)
        viewAbilityManager?.onDrawForeground(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return viewAbilityManager?.onTouchEvent(event) == true || super.onTouchEvent(event)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewAbilityManager?.onDetachedFromWindow()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        val oldDrawable = this.drawable
        super.setImageDrawable(drawable)
        val newDrawable = this.drawable
        if (oldDrawable !== newDrawable) {
            viewAbilityManager?.onDrawableChanged(oldDrawable, newDrawable)
        }
    }

    override fun setImageURI(uri: Uri?) {
        val oldDrawable = this.drawable
        super.setImageURI(uri)
        val newDrawable = this.drawable
        if (oldDrawable !== newDrawable) {
            viewAbilityManager?.onDrawableChanged(oldDrawable, newDrawable)
        }
    }

    final override fun setOnClickListener(l: OnClickListener?) {
        viewAbilityManager?.setOnClickListener(l)
    }

    final override fun setOnLongClickListener(l: OnLongClickListener?) {
        viewAbilityManager?.setOnLongClickListener(l)
    }

    final override fun superSetOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener(listener)
        if (listener == null) {
            isClickable = false
        }
    }

    final override fun superSetOnLongClickListener(listener: OnLongClickListener?) {
        super.setOnLongClickListener(listener)
        if (listener == null) {
            isLongClickable = false
        }
    }

    final override fun superSetScaleType(scaleType: ScaleType) {
        super.setScaleType(scaleType)
    }

    final override fun superGetScaleType(): ScaleType {
        return super.getScaleType()
    }

    final override fun setScaleType(scaleType: ScaleType) {
        if (viewAbilityManager?.setScaleType(scaleType) != true) {
            super.setScaleType(scaleType)
        }
    }

    final override fun getScaleType(): ScaleType {
        return viewAbilityManager?.getScaleType() ?: super.getScaleType()
    }

    final override fun superSetImageMatrix(matrix: Matrix?) {
        super.setImageMatrix(matrix)
    }

    final override fun superGetImageMatrix(): Matrix {
        return super.getImageMatrix()
    }

    final override fun setImageMatrix(matrix: Matrix?) {
        if (viewAbilityManager?.setImageMatrix(matrix) != true) {
            super.setImageMatrix(matrix)
        }
    }

    final override fun getImageMatrix(): Matrix {
        return viewAbilityManager?.getImageMatrix() ?: super.getImageMatrix()
    }

    override fun getListener(): Listener? {
        return viewAbilityManager?.getRequestListener()
    }

    override fun getProgressListener(): ProgressListener? {
        return viewAbilityManager?.getRequestProgressListener()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superParcelable = super.onSaveInstanceState()
        val abilityListStateBundle1 =
            viewAbilityManager?.onSaveInstanceState() ?: return superParcelable
        return SavedState(superParcelable).apply {
            abilityListStateBundle = abilityListStateBundle1
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        viewAbilityManager?.onRestoreInstanceState(state.abilityListStateBundle)
    }

    class SavedState : BaseSavedState {
        var abilityListStateBundle: Bundle? = null

        internal constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeBundle(abilityListStateBundle)
        }

        override fun toString(): String = "AbsAbilityZoomImageView"

        private constructor(`in`: Parcel) : super(`in`) {
            abilityListStateBundle = `in`.readBundle(SavedState::class.java.classLoader)
        }

        companion object {
            @Keep
            @JvmField
            @Suppress("unused")
            val CREATOR: Creator<SavedState> = object : Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}