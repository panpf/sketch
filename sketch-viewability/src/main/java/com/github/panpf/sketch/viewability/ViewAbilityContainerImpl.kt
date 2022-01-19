package com.github.panpf.sketch.viewability

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.util.isAttachedToWindowCompat
import com.github.panpf.sketch.viewability.ViewAbility.AttachObserver
import com.github.panpf.sketch.viewability.ViewAbility.ClickObserver
import com.github.panpf.sketch.viewability.ViewAbility.DrawObserver
import com.github.panpf.sketch.viewability.ViewAbility.DrawableObserver
import com.github.panpf.sketch.viewability.ViewAbility.LayoutObserver
import com.github.panpf.sketch.viewability.ViewAbility.LongClickObserver
import com.github.panpf.sketch.viewability.ViewAbility.RequestListenerObserver
import com.github.panpf.sketch.viewability.ViewAbility.RequestProgressListenerObserver
import com.github.panpf.sketch.viewability.ViewAbility.VisibilityChangedObserver
import java.lang.ref.WeakReference

class ViewAbilityContainerImpl(
    private val owner: ViewAbilityOwner,
    view: View
) : ViewAbilityContainer {

    private val displayRequestListener: DisplayRequestListener by lazy {
        DisplayRequestListener(WeakReference(this@ViewAbilityContainerImpl))
    }
    private val host = Host(view, owner)

    private var clickListenerWrapper: OnClickListener? = null
    private var longClickListenerWrapper: OnLongClickListener? = null

    private val _viewAbilityList = LinkedHashSet<ViewAbility>()
    private var _viewAbilityImmutableList: List<ViewAbility> = _viewAbilityList.toList()
    private var attachObserverList: List<AttachObserver>? = null
    private var visibilityChangedObserverList: List<VisibilityChangedObserver>? = null
    private var layoutAbilityList: List<LayoutObserver>? = null
    private var drawObserverList: List<DrawObserver>? = null
    private var drawableObserverList: List<DrawableObserver>? = null
    private var clickObserverList: List<ClickObserver>? = null
    private var longClickAbilityList: List<LongClickObserver>? = null
    private var requestListenerAbilityList: List<RequestListenerObserver>? = null
    private var requestProgressListenerAbilityList: List<RequestProgressListenerObserver>? = null

    override val viewAbilityList: List<ViewAbility>
        get() = _viewAbilityImmutableList

    private fun onAbilityListChanged() {
        attachObserverList = _viewAbilityList.mapNotNull { if (it is AttachObserver) it else null }
        visibilityChangedObserverList =
            _viewAbilityList.mapNotNull { if (it is VisibilityChangedObserver) it else null }
        layoutAbilityList = _viewAbilityList.mapNotNull { if (it is LayoutObserver) it else null }
        drawObserverList = _viewAbilityList.mapNotNull { if (it is DrawObserver) it else null }
        drawableObserverList =
            _viewAbilityList.mapNotNull { if (it is DrawableObserver) it else null }
        clickObserverList = _viewAbilityList.mapNotNull { if (it is ClickObserver) it else null }
        longClickAbilityList =
            _viewAbilityList.mapNotNull { if (it is LongClickObserver) it else null }
        requestListenerAbilityList =
            _viewAbilityList.mapNotNull { if (it is RequestListenerObserver) it else null }
        requestProgressListenerAbilityList =
            _viewAbilityList.mapNotNull { if (it is RequestProgressListenerObserver) it else null }
        _viewAbilityImmutableList = _viewAbilityList.toList()
        refreshOnClickListener()
        refreshOnLongClickListener()
    }

    override fun addViewAbility(viewAbility: ViewAbility): ViewAbilityContainer = apply {
        _viewAbilityList.add(viewAbility)
        onAbilityListChanged()

        _viewAbilityList.forEach {
            it.host = host
        }
        val view = host.view
        if (view.isAttachedToWindowCompat) {
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

        host.invalidate()
    }

    override fun removeViewAbility(viewAbility: ViewAbility): ViewAbilityContainer = apply {
        if (viewAbility is AttachObserver) {
            viewAbility.onDetachedFromWindow()
        }
        _viewAbilityList.forEach {
            it.host = null
        }

        _viewAbilityList.remove(viewAbility)
        onAbilityListChanged()

        host.invalidate()
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
        drawObserverList?.forEach {
            it.onDrawForegroundBefore(canvas)
        }
    }

    override fun onDrawForeground(canvas: Canvas) {
        drawObserverList?.forEach {
            it.onDrawForeground(canvas)
        }
    }

    override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
        drawableObserverList?.forEach {
            it.onDrawableChanged(oldDrawable, newDrawable)
        }
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

    private fun refreshOnClickListener() {
        val clickListenerWrapper = clickListenerWrapper
        val clickAbilityList = clickObserverList
        if (clickListenerWrapper != null || clickAbilityList?.any { it.canIntercept } == true) {
            owner.superSetOnClickListener { view ->
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
            owner.superSetOnClickListener(null)
        }
    }

    private fun refreshOnLongClickListener() {
        val longClickListenerWrapper = longClickListenerWrapper
        val longClickAbilityList = longClickAbilityList
        if (longClickListenerWrapper != null || longClickAbilityList?.any { it.canIntercept } == true) {
            owner.superSetOnLongClickListener { view ->
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
            owner.superSetOnLongClickListener(null)
        }
    }

    private class DisplayRequestListener(private val view: WeakReference<ViewAbilityContainerImpl>) :
        Listener<DisplayRequest, Success, Error>,
        ProgressListener<DisplayRequest> {

        override fun onStart(request: DisplayRequest) {
            super.onStart(request)
            view.get()?.onRequestStart(request)
        }

        override fun onError(request: DisplayRequest, result: Error) {
            super.onError(request, result)
            view.get()?.onRequestError(request, result)
        }

        override fun onSuccess(request: DisplayRequest, result: Success) {
            super.onSuccess(request, result)
            view.get()?.onRequestSuccess(request, result)
        }

        override fun onUpdateProgress(
            request: DisplayRequest, totalLength: Long, completedLength: Long
        ) {
            view.get()?.onUpdateRequestProgress(request, totalLength, completedLength)
        }
    }
}