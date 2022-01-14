package com.github.panpf.sketch.extensions

import android.view.View
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.viewability.internal.Host
import com.github.panpf.sketch.viewability.internal.ViewAbility
import com.github.panpf.sketch.viewability.internal.ViewAbility.ClickObserver
import com.github.panpf.sketch.viewability.internal.ViewAbility.RequestListenerObserver
import com.github.panpf.sketch.viewability.internal.ViewAbilityContainerOwner

class SaveCellularTrafficClickForceIgnoreViewAbility
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
        host.submitRequest(newRequest)
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
        .find { it is SaveCellularTrafficClickForceIgnoreViewAbility }
        ?.let { viewAbilityContainer.removeViewAbility(it) }
    if (enabled) {
        viewAbilityContainer.addViewAbility(SaveCellularTrafficClickForceIgnoreViewAbility())
    }
}