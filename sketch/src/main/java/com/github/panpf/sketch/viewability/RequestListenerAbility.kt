package com.github.panpf.sketch.viewability

import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success

interface RequestListenerAbility : Ability {

    fun onRequestStart(request: DisplayRequest)

    fun onRequestError(request: DisplayRequest, result: Error)

    fun onRequestSuccess(request: DisplayRequest, result: Success)
}