package com.github.panpf.sketch.viewability

import com.github.panpf.sketch.request.DisplayRequest

interface RequestProgressListenerAbility : Ability {
    fun onUpdateRequestProgress(request: DisplayRequest, totalLength: Long, completedLength: Long)
}