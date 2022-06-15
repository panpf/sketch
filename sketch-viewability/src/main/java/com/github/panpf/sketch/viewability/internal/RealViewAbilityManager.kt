package com.github.panpf.sketch.viewability.internal

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.widget.ImageView.ScaleType
import androidx.core.view.ViewCompat
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.viewability.AttachObserver
import com.github.panpf.sketch.viewability.ClickObserver
import com.github.panpf.sketch.viewability.DrawForegroundObserver
import com.github.panpf.sketch.viewability.DrawObserver
import com.github.panpf.sketch.viewability.DrawableObserver
import com.github.panpf.sketch.viewability.Host
import com.github.panpf.sketch.viewability.ImageMatrixObserver
import com.github.panpf.sketch.viewability.InstanceStateObserver
import com.github.panpf.sketch.viewability.LayoutObserver
import com.github.panpf.sketch.viewability.LongClickObserver
import com.github.panpf.sketch.viewability.RequestListenerObserver
import com.github.panpf.sketch.viewability.RequestProgressListenerObserver
import com.github.panpf.sketch.viewability.ScaleTypeObserver
import com.github.panpf.sketch.viewability.SizeChangeObserver
import com.github.panpf.sketch.viewability.TouchEventObserver
import com.github.panpf.sketch.viewability.ViewAbility
import com.github.panpf.sketch.viewability.ViewAbilityContainer
import com.github.panpf.sketch.viewability.ViewAbilityManager
import com.github.panpf.sketch.viewability.VisibilityChangedObserver
import java.lang.ref.WeakReference

class RealViewAbilityManager(
    private val container: ViewAbilityContainer,
    view: View
) : ViewAbilityManager {

    private val displayRequestListener: DisplayRequestListener by lazy {
        DisplayRequestListener(WeakReference(this@RealViewAbilityManager))
    }
    private val host = Host(view, container)

    private var clickListenerWrapper: OnClickListener? = null
    private var longClickListenerWrapper: OnLongClickListener? = null

    private val _viewAbilityList = LinkedHashSet<ViewAbility>()
    private var _viewAbilityImmutableList: List<ViewAbility> = _viewAbilityList.toList()

    private var attachObserverList: List<AttachObserver>? = null
    private var layoutAbilityList: List<LayoutObserver>? = null
    private var sizeChangeAbilityList: List<SizeChangeObserver>? = null
    private var drawObserverList: List<DrawObserver>? = null
    private var drawForegroundObserverList: List<DrawForegroundObserver>? = null
    private var touchEventObserverList: List<TouchEventObserver>? = null
    private var clickObserverList: List<ClickObserver>? = null
    private var longClickAbilityList: List<LongClickObserver>? = null
    private var visibilityChangedObserverList: List<VisibilityChangedObserver>? = null
    private var drawableObserverList: List<DrawableObserver>? = null
    private var scaleTypeAbilityList: List<ScaleTypeObserver>? = null
    private var imageMatrixAbilityList: List<ImageMatrixObserver>? = null
    private var requestListenerAbilityList: List<RequestListenerObserver>? = null
    private var requestProgressListenerAbilityList: List<RequestProgressListenerObserver>? = null
    private var instanceStateObserverAbilityList: List<InstanceStateObserver>? = null

    override val viewAbilityList: List<ViewAbility>
        get() = _viewAbilityImmutableList

    private fun onAbilityListChanged() {
        attachObserverList =
            _viewAbilityList.filterIsInstance(AttachObserver::class.java).takeIf { it.isNotEmpty() }
        layoutAbilityList =
            _viewAbilityList.filterIsInstance(LayoutObserver::class.java).takeIf { it.isNotEmpty() }
        sizeChangeAbilityList =
            _viewAbilityList.filterIsInstance(SizeChangeObserver::class.java)
                .takeIf { it.isNotEmpty() }
        drawObserverList =
            _viewAbilityList.filterIsInstance(DrawObserver::class.java).takeIf { it.isNotEmpty() }
        drawForegroundObserverList =
            _viewAbilityList.filterIsInstance(DrawForegroundObserver::class.java)
                .takeIf { it.isNotEmpty() }
        touchEventObserverList =
            _viewAbilityList.filterIsInstance(TouchEventObserver::class.java)
                .takeIf { it.isNotEmpty() }
        clickObserverList =
            _viewAbilityList.filterIsInstance(ClickObserver::class.java).takeIf { it.isNotEmpty() }
        longClickAbilityList =
            _viewAbilityList.filterIsInstance(LongClickObserver::class.java)
                .takeIf { it.isNotEmpty() }
        visibilityChangedObserverList = _viewAbilityList
            .filterIsInstance(VisibilityChangedObserver::class.java).takeIf { it.isNotEmpty() }
        drawableObserverList = _viewAbilityList.filterIsInstance(DrawableObserver::class.java)
            .takeIf { it.isNotEmpty() }
        scaleTypeAbilityList = _viewAbilityList.filterIsInstance(ScaleTypeObserver::class.java)
            .takeIf { it.isNotEmpty() }
        imageMatrixAbilityList = _viewAbilityList.filterIsInstance(ImageMatrixObserver::class.java)
            .takeIf { it.isNotEmpty() }
        requestListenerAbilityList =
            _viewAbilityList.filterIsInstance(RequestListenerObserver::class.java)
                .takeIf { it.isNotEmpty() }
        requestProgressListenerAbilityList =
            _viewAbilityList.filterIsInstance(RequestProgressListenerObserver::class.java)
                .takeIf { it.isNotEmpty() }
        instanceStateObserverAbilityList =
            _viewAbilityList.filterIsInstance(InstanceStateObserver::class.java)
                .takeIf { it.isNotEmpty() }
        _viewAbilityImmutableList = _viewAbilityList.toList()
        refreshOnClickListener()
        refreshOnLongClickListener()
    }

    override fun addViewAbility(viewAbility: ViewAbility): ViewAbilityManager = apply {
        if (viewAbility is ScaleTypeObserver) {
            require(scaleTypeAbilityList?.isNotEmpty() != true) {
                "Only one ScaleTypeObserver can be added"
            }
        } else if (viewAbility is ImageMatrixObserver) {
            require(imageMatrixAbilityList?.isNotEmpty() != true) {
                "Only one ImageMatrixObserver can be added"
            }
        }
        _viewAbilityList.add(viewAbility)
        onAbilityListChanged()

        viewAbility.host = host

        val view = host.view
        if (ViewCompat.isAttachedToWindow(view)) {
            if (viewAbility is AttachObserver) {
                viewAbility.onAttachedToWindow()
            }
            if (viewAbility is VisibilityChangedObserver) {
                viewAbility.onVisibilityChanged(view, view.visibility)
            }
            if (view.visibility != View.GONE) {
                if (viewAbility is LayoutObserver && (view.width > 0 || view.height > 0)) {
                    viewAbility.onLayout(false, view.left, view.top, view.right, view.bottom)
                }
            }
        }

        host.view.invalidate()
    }

    override fun removeViewAbility(viewAbility: ViewAbility): ViewAbilityManager = apply {
        if (viewAbility is AttachObserver) {
            viewAbility.onDetachedFromWindow()
        }

        viewAbility.host = null

        _viewAbilityList.remove(viewAbility)
        onAbilityListChanged()

        host.view.invalidate()
    }

    override fun onAttachedToWindow() {
        attachObserverList?.forEach {
            it.onAttachedToWindow()
        }
    }

    override fun onDetachedFromWindow() {
        attachObserverList?.forEach {
            it.onDetachedFromWindow()
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        visibilityChangedObserverList?.forEach {
            it.onVisibilityChanged(changedView, visibility)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        layoutAbilityList?.forEach {
            it.onLayout(changed, left, top, right, bottom)
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        sizeChangeAbilityList?.forEach {
            it.onSizeChanged(width, height, oldWidth, oldHeight)
        }
    }

    override fun onDrawBefore(canvas: Canvas) {
        drawObserverList?.forEach {
            it.onDrawBefore(canvas)
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawObserverList?.forEach {
            it.onDraw(canvas)
        }
    }

    override fun onDrawForegroundBefore(canvas: Canvas) {
        drawForegroundObserverList?.forEach {
            it.onDrawForegroundBefore(canvas)
        }
    }

    override fun onDrawForeground(canvas: Canvas) {
        drawForegroundObserverList?.forEach {
            it.onDrawForeground(canvas)
        }
    }

    override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
        drawableObserverList?.forEach {
            it.onDrawableChanged(oldDrawable, newDrawable)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return touchEventObserverList?.fold(false) { acc, touchEventObserver ->
            acc || touchEventObserver.onTouchEvent(event)
        } ?: false
    }

    private fun onRequestStart(request: DisplayRequest) {
        requestListenerAbilityList?.forEach {
            it.onRequestStart(request)
        }
        refreshOnClickListener()
        refreshOnLongClickListener()
    }

    private fun onRequestError(request: DisplayRequest, result: Error) {
        requestListenerAbilityList?.forEach {
            it.onRequestError(request, result)
        }
        refreshOnClickListener()
        refreshOnLongClickListener()
    }

    private fun onRequestSuccess(request: DisplayRequest, result: Success) {
        requestListenerAbilityList?.forEach {
            it.onRequestSuccess(request, result)
        }
        refreshOnClickListener()
        refreshOnLongClickListener()
    }

    private fun onUpdateRequestProgress(
        request: DisplayRequest, totalLength: Long, completedLength: Long
    ) {
        requestProgressListenerAbilityList?.forEach {
            it.onUpdateRequestProgress(request, totalLength, completedLength)
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        this.clickListenerWrapper = l
        refreshOnClickListener()
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        this.longClickListenerWrapper = l
        refreshOnLongClickListener()
    }

    override fun setScaleType(scaleType: ScaleType): Boolean {
        return scaleTypeAbilityList?.firstOrNull()?.setScaleType(scaleType) == true
    }

    override fun getScaleType(): ScaleType? {
        return scaleTypeAbilityList?.firstOrNull()?.getScaleType()
    }

    override fun setImageMatrix(imageMatrix: Matrix?): Boolean {
        return imageMatrixAbilityList?.firstOrNull()?.setImageMatrix(imageMatrix) == true
    }

    override fun getImageMatrix(): Matrix? {
        return imageMatrixAbilityList?.firstOrNull()?.getImageMatrix()
    }

    override fun getRequestListener(): Listener<DisplayRequest, Success, Error>? {
        return if (requestListenerAbilityList?.isNotEmpty() == true) {
            displayRequestListener
        } else {
            null
        }
    }

    override fun getRequestProgressListener(): ProgressListener<DisplayRequest>? {
        return if (requestProgressListenerAbilityList?.isNotEmpty() == true) {
            displayRequestListener
        } else {
            null
        }
    }

    override fun onSaveInstanceState(): Bundle? {
        val instanceStateObserverAbilityList =
            instanceStateObserverAbilityList?.takeIf { it.isNotEmpty() } ?: return null
        val bundle = Bundle()
        instanceStateObserverAbilityList.forEach {
            val childBundle = it.onSaveInstanceState()
            if (childBundle != null) {
                val key = it::class.java.name + "_onSaveInstanceState"
                bundle.putBundle(key, childBundle)
            }
        }
        return bundle.takeIf { !it.isEmpty }
    }

    override fun onRestoreInstanceState(state: Bundle?) {
        val instanceStateObserverAbilityList =
            instanceStateObserverAbilityList?.takeIf { it.isNotEmpty() } ?: return
        val state1 = state?.takeIf { !it.isEmpty } ?: return
        instanceStateObserverAbilityList.forEach {
            val key = it::class.java.name + "_onSaveInstanceState"
            val childBundle = state1.getBundle(key)
            it.onRestoreInstanceState(childBundle)
        }
    }

    private fun refreshOnClickListener() {
        val clickListenerWrapper = clickListenerWrapper
        val clickAbilityList = clickObserverList
        if (clickListenerWrapper != null || clickAbilityList?.any { it.canIntercept } == true) {
            container.superSetOnClickListener { view ->
                if (clickAbilityList != null) {
                    for (item in clickAbilityList) {
                        if (item.onClick(view)) {
                            return@superSetOnClickListener
                        }
                    }
                }
                clickListenerWrapper?.onClick(view)
            }
        } else {
            container.superSetOnClickListener(null)
        }
    }

    private fun refreshOnLongClickListener() {
        val longClickListenerWrapper = longClickListenerWrapper
        val longClickAbilityList = longClickAbilityList
        if (longClickListenerWrapper != null || longClickAbilityList?.any { it.canIntercept } == true) {
            container.superSetOnLongClickListener { view ->
                if (longClickAbilityList != null) {
                    for (item in longClickAbilityList) {
                        if (item.onLongClick(view)) {
                            return@superSetOnLongClickListener true
                        }
                    }
                }
                longClickListenerWrapper?.onLongClick(view) == true
            }
        } else {
            container.superSetOnLongClickListener(null)
        }
    }

    private class DisplayRequestListener(private val realView: WeakReference<RealViewAbilityManager>) :
        Listener<DisplayRequest, Success, Error>,
        ProgressListener<DisplayRequest> {

        override fun onStart(request: DisplayRequest) {
            super.onStart(request)
            realView.get()?.onRequestStart(request)
        }

        override fun onError(request: DisplayRequest, result: Error) {
            super.onError(request, result)
            realView.get()?.onRequestError(request, result)
        }

        override fun onSuccess(request: DisplayRequest, result: Success) {
            super.onSuccess(request, result)
            realView.get()?.onRequestSuccess(request, result)
        }

        override fun onUpdateProgress(
            request: DisplayRequest, totalLength: Long, completedLength: Long
        ) {
            realView.get()?.onUpdateRequestProgress(request, totalLength, completedLength)
        }
    }
}