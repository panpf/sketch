package com.github.panpf.sketch.viewability.extensions

import android.view.View
import android.widget.ImageView
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.ignoreSaveCellularTraffic
import com.github.panpf.sketch.request.isCausedBySaveCellularTraffic
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.viewability.ClickAbility
import com.github.panpf.sketch.viewability.RequestListenerAbility
import com.github.panpf.sketch.viewability.ViewAbility
import com.github.panpf.sketch.viewability.ViewAbilityContainerOwner

// todo 移到 extensions 模块
class IgnoreSaveCellularTrafficViewAbility : ViewAbility, ClickAbility, RequestListenerAbility {

    private var errorFromSaveCellularTraffic = false
    private var request: DisplayRequest? = null

    override var view: ImageView? = null

    override val canIntercept: Boolean
        get() = view != null && errorFromSaveCellularTraffic && request != null

    override fun onClick(v: View): Boolean {
        if (!canIntercept) return false
        val view = view ?: return false
        val request = request ?: return false
        val newRequest = request.newDisplayRequest {
            ignoreSaveCellularTraffic(true)
            target(view)
        }
        view.context.sketch.enqueueDisplay(newRequest)
        return true
    }

    override fun onRequestStart(request: DisplayRequest) {
        errorFromSaveCellularTraffic = false
        this.request = null
    }

    override fun onRequestError(request: DisplayRequest, result: Error) {
        errorFromSaveCellularTraffic = result.exception.isCausedBySaveCellularTraffic
        if (errorFromSaveCellularTraffic) {
            this.request = request
        } else {
            this.request = null
        }
    }

    override fun onRequestSuccess(request: DisplayRequest, result: Success) {
        errorFromSaveCellularTraffic = false
        this.request = null
    }
}

fun ViewAbilityContainerOwner.setClickRedisplayAndIgnoreSaveCellularTraffic(enabled: Boolean) {
    val viewAbilityContainer = viewAbilityContainer
    viewAbilityContainer.viewAbilityList
        .find { it is IgnoreSaveCellularTrafficViewAbility }
        ?.let { viewAbilityContainer.removeViewAbility(it) }
    if (enabled) {
        viewAbilityContainer.addViewAbility(IgnoreSaveCellularTrafficViewAbility())
    }
}