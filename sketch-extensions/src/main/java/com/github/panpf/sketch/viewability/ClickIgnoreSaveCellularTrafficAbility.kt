package com.github.panpf.sketch.viewability

import android.view.View
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.SaveCellularTrafficDisplayInterceptor
import com.github.panpf.sketch.request.ignoreSaveCellularTraffic
import com.github.panpf.sketch.request.isCausedBySaveCellularTraffic

/**
 * Set to enable click View to force ignore the data saving function
 */
fun ViewAbilityContainer.setClickIgnoreSaveCellularTrafficEnabled(enable: Boolean = true) {
    val enabled = isClickIgnoreSaveCellularTrafficEnabled
    if (enable && !enabled) {
        addViewAbility(ClickIgnoreSaveCellularTrafficAbility())
    } else if (!enable && enabled) {
        viewAbilityList
            .find { it is ClickIgnoreSaveCellularTrafficAbility }
            ?.let { removeViewAbility(it) }
    }
}

/**
 * Returns true if click View force ignore data saving feature is enabled
 */
val ViewAbilityContainer.isClickIgnoreSaveCellularTrafficEnabled: Boolean
    get() = viewAbilityList.find { it is ClickIgnoreSaveCellularTrafficAbility } != null

/**
 * Click View to force ignoring the data saving function, generally used with [SaveCellularTrafficDisplayInterceptor]
 */
class ClickIgnoreSaveCellularTrafficAbility
    : ViewAbility, ClickObserver, RequestListenerObserver {

    private var errorFromSaveCellularTraffic = false
    private var request: DisplayRequest? = null

    override var host: Host? = null

    override val canIntercept: Boolean
        get() = host != null && errorFromSaveCellularTraffic && request != null

    override fun onClick(v: View): Boolean {
        if (!canIntercept) return false
        val host = host ?: return false
        val request = request ?: return false
        val newRequest = request.newDisplayRequest {
            ignoreSaveCellularTraffic(true)
        }
        host.container.submitRequest(newRequest)
        return true
    }

    override fun onRequestStart(request: DisplayRequest) {
        errorFromSaveCellularTraffic = false
        this.request = null
    }

    override fun onRequestError(request: DisplayRequest, result: Error) {
        errorFromSaveCellularTraffic =
            isCausedBySaveCellularTraffic(result.request, result.exception)
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