package com.github.panpf.sketch.extensions.view.ability.test

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView.ScaleType
import android.widget.ImageView.ScaleType.FIT_CENTER
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import com.github.panpf.sketch.ability.AbsAbilityImageView
import com.github.panpf.sketch.ability.AttachObserver
import com.github.panpf.sketch.ability.ClickObserver
import com.github.panpf.sketch.ability.DrawForegroundObserver
import com.github.panpf.sketch.ability.DrawObserver
import com.github.panpf.sketch.ability.DrawableObserver
import com.github.panpf.sketch.ability.Host
import com.github.panpf.sketch.ability.ImageMatrixObserver
import com.github.panpf.sketch.ability.InstanceStateObserver
import com.github.panpf.sketch.ability.LayoutObserver
import com.github.panpf.sketch.ability.LongClickObserver
import com.github.panpf.sketch.ability.RequestListenerObserver
import com.github.panpf.sketch.ability.RequestProgressListenerObserver
import com.github.panpf.sketch.ability.ScaleTypeObserver
import com.github.panpf.sketch.ability.SizeChangedObserver
import com.github.panpf.sketch.ability.TouchEventObserver
import com.github.panpf.sketch.ability.ViewAbility
import com.github.panpf.sketch.ability.VisibilityChangedObserver
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.RequestState
import kotlin.test.assertTrue

class ViewAbilityTestActivity : Activity() {

    val viewAbility = TestViewAbility()

    lateinit var abilityView: AbsAbilityImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val abilityView = object : AbsAbilityImageView(this) {
            override val requestState: RequestState
                get() = throw UnsupportedOperationException()
        }.apply {
            id = com.github.panpf.sketch.test.utils.core.R.id.test_view
            addViewAbility(viewAbility)
            setImageResource(android.R.drawable.ic_delete)

            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                foreground = ColorDrawable(ColorUtils.setAlphaComponent(Color.GREEN, 100))
            }
            this@ViewAbilityTestActivity.abilityView = this
        }
        setContentView(abilityView)
    }

    class TestViewAbility : ViewAbility,
        AttachObserver,
        LayoutObserver,
        DrawObserver,
        DrawForegroundObserver,
        SizeChangedObserver,
        VisibilityChangedObserver,
        TouchEventObserver,
        ClickObserver,
        LongClickObserver,
        DrawableObserver,
        ScaleTypeObserver,
        ImageMatrixObserver,
        RequestListenerObserver,
        RequestProgressListenerObserver,
        InstanceStateObserver {

        override var host: Host? = null
        override val canIntercept: Boolean = true
        private var scaleType: ScaleType = FIT_CENTER
        private var imageMatrix: Matrix? = null

        val attachActions = mutableListOf<String>()
        val layoutActions = mutableListOf<String>()
        val drawActions = mutableListOf<String>()
        val sizeActions = mutableListOf<String>()
        val visibilityActions = mutableListOf<String>()
        val touchEventActions = mutableListOf<String>()
        val clickActions = mutableListOf<String>()
        val longClickActions = mutableListOf<String>()
        val drawableActions = mutableListOf<String>()
        val scaleTypeActions = mutableListOf<String>()
        val imageMatrixActions = mutableListOf<String>()
        val requestListenerActions = mutableListOf<String>()
        val requestProgressListenerActions = mutableListOf<String>()
        val instanceStateActions = mutableListOf<String>()

        override fun onAttachedToWindow() {
            attachActions.add("onAttachedToWindow")
        }

        override fun onDetachedFromWindow() {
            attachActions.add("onDetachedFromWindow")
        }

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            layoutActions.add("onLayout")
        }

        override fun onDrawBefore(canvas: Canvas) {
            drawActions.add("onDrawBefore")
        }

        override fun onDraw(canvas: Canvas) {
            drawActions.add("onDraw")
        }

        override fun onDrawForegroundBefore(canvas: Canvas) {
            drawActions.add("onDrawForegroundBefore")
        }

        override fun onDrawForeground(canvas: Canvas) {
            drawActions.add("onDrawForeground")
        }

        override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
            sizeActions.add("onSizeChanged:${width}x${height}")
        }

        override fun onVisibilityChanged(changedView: View, visibility: Int) {
            visibilityActions.add("onVisibilityChanged:${changedView.isVisible}")
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            touchEventActions.add("onTouchEvent")
            return false
        }

        override fun onClick(v: View): Boolean {
            clickActions.add("onClick")
            return true
        }

        override fun onLongClick(v: View): Boolean {
            longClickActions.add("onLongClick")
            return true
        }

        override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
            drawableActions.add("onDrawableChanged:${if (oldDrawable != null) "NotNull" else "Null"}->${if (newDrawable != null) "NotNull" else "Null"}")
        }

        override fun setScaleType(scaleType: ScaleType): Boolean {
            this.scaleType = scaleType
            scaleTypeActions.add("setScaleType:$scaleType")
            return false
        }

        override fun getScaleType(): ScaleType {
            scaleTypeActions.add("getScaleType:$scaleType")
            return scaleType
        }

        override fun setImageMatrix(imageMatrix: Matrix?): Boolean {
            this.imageMatrix = imageMatrix
            imageMatrixActions.add("setImageMatrix:$imageMatrix")
            return false
        }

        override fun getImageMatrix(): Matrix? {
            imageMatrixActions.add("getImageMatrix:$imageMatrix")
            return imageMatrix
        }

        override fun onRequestStart(request: ImageRequest) {
            requestListenerActions.add("onRequestStart")
        }

        override fun onRequestError(request: ImageRequest, error: Error) {
            requestListenerActions.add("onRequestError")
        }

        override fun onRequestSuccess(request: ImageRequest, result: Success) {
            requestListenerActions.add("onRequestSuccess")
        }

        override fun onUpdateRequestProgress(request: ImageRequest, progress: Progress) {
            requestProgressListenerActions.add("onUpdateRequestProgress")
        }

        override fun onSaveInstanceState(): Bundle {
            instanceStateActions.add("onSaveInstanceState")
            return Bundle().apply {
                putString("InstanceStateViewAbility", "true")
            }
        }

        override fun onRestoreInstanceState(state: Bundle?) {
            instanceStateActions.add("onRestoreInstanceState")
            if (state != null) {
                assertTrue(state.getString("InstanceStateViewAbility")!!.toBoolean())
            }
        }
    }

//    private class AttachViewAbility : BaseViewAbility(), AttachObserver {
//        val attachActions = mutableListOf<String>()
//
//        override fun onAttachedToWindow() {
//            attachActions.add("onAttachedToWindow")
//        }
//
//        override fun onDetachedFromWindow() {
//            attachActions.add("onDetachedFromWindow")
//        }
//    }
//
//    private class LayoutViewAbility : BaseViewAbility(), LayoutObserver {
//        val layoutActions = mutableListOf<String>()
//
//        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
//            layoutActions.add("onLayout")
//        }
//    }
//
//    private class DrawViewAbility : BaseViewAbility(), DrawObserver, DrawForegroundObserver {
//        val drawActions = mutableListOf<String>()
//
//        override fun onDrawBefore(canvas: Canvas) {
//            drawActions.add("onDrawBefore")
//        }
//
//        override fun onDraw(canvas: Canvas) {
//            drawActions.add("onDraw")
//        }
//
//        override fun onDrawForegroundBefore(canvas: Canvas) {
//            drawActions.add("onDrawForegroundBefore")
//        }
//
//        override fun onDrawForeground(canvas: Canvas) {
//            drawActions.add("onDrawForeground")
//        }
//    }
//
//    private class SizeChangedViewAbility : BaseViewAbility(), SizeChangedObserver {
//        val sizeActions = mutableListOf<String>()
//
//        override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
//            sizeActions.add("onSizeChanged:${width}x${height}")
//        }
//    }
//
//    private class VisibilityChangedViewAbility : BaseViewAbility(), VisibilityChangedObserver {
//        val visibilityActions = mutableListOf<String>()
//
//        override fun onVisibilityChanged(changedView: View, visibility: Int) {
//            visibilityActions.add("onVisibilityChanged:${changedView.isVisible}")
//        }
//    }
//
//    private class TouchEventViewAbility : BaseViewAbility(), TouchEventObserver {
//        val touchEventActions = mutableListOf<String>()
//
//        override fun onTouchEvent(event: MotionEvent): Boolean {
//            touchEventActions.add("onTouchEvent")
//            return false
//        }
//    }
//
//    private class ClickViewAbility : BaseViewAbility(), ClickObserver {
//        val clickActions = mutableListOf<String>()
//
//        override val canIntercept: Boolean = true
//
//        override fun onClick(v: View): Boolean {
//            clickActions.add("onClick")
//            return true
//        }
//    }
//
//    private class LongClickViewAbility : BaseViewAbility(), LongClickObserver {
//        val longClickActions = mutableListOf<String>()
//
//        override val canIntercept: Boolean = true
//
//        override fun onLongClick(v: View): Boolean {
//            longClickActions.add("onLongClick")
//            return true
//        }
//    }
//
//    private class DrawableViewAbility : BaseViewAbility(), DrawableObserver {
//        val drawableActions = mutableListOf<String>()
//
//        override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
//            drawableActions.add("onDrawableChanged:${if (oldDrawable != null) "NotNull" else "Null"}->${if (newDrawable != null) "NotNull" else "Null"}")
//        }
//    }
//
//    private class ScaleTypeViewAbility : BaseViewAbility(), ScaleTypeObserver {
//        val scaleTypeActions = mutableListOf<String>()
//
//        private var scaleType: ScaleType = FIT_CENTER
//
//        override fun setScaleType(scaleType: ScaleType): Boolean {
//            this.scaleType = scaleType
//            scaleTypeActions.add("setScaleType:$scaleType")
//            return false
//        }
//
//        override fun getScaleType(): ScaleType {
//            scaleTypeActions.add("getScaleType:$scaleType")
//            return scaleType
//        }
//    }
//
//    private class ImageMatrixViewAbility : BaseViewAbility(), ImageMatrixObserver {
//        val imageMatrixActions = mutableListOf<String>()
//
//        private var imageMatrix: Matrix? = null
//
//        override fun setImageMatrix(imageMatrix: Matrix?): Boolean {
//            this.imageMatrix = imageMatrix
//            imageMatrixActions.add("setImageMatrix:$imageMatrix")
//            return false
//        }
//
//        override fun getImageMatrix(): Matrix? {
//            imageMatrixActions.add("getImageMatrix:$imageMatrix")
//            return imageMatrix
//        }
//    }
//
//    private class RequestListenerViewAbility : BaseViewAbility(), RequestListenerObserver {
//        val requestListenerActions = mutableListOf<String>()
//
//        override fun onRequestStart(request: ImageRequest) {
//            requestListenerActions.add("onRequestStart")
//        }
//
//        override fun onRequestError(request: ImageRequest, error: Error) {
//            requestListenerActions.add("onRequestError")
//        }
//
//        override fun onRequestSuccess(request: ImageRequest, result: Success) {
//            requestListenerActions.add("onRequestSuccess")
//        }
//    }
//
//    private class RequestProgressListenerViewAbility : BaseViewAbility(),
//        RequestProgressListenerObserver {
//        val requestProgressListenerActions = mutableListOf<String>()
//
//        override fun onUpdateRequestProgress(request: ImageRequest, progress: Progress) {
//            requestProgressListenerActions.add("onUpdateRequestProgress")
//        }
//    }
}