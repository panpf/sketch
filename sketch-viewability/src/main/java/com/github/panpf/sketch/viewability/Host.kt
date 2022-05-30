package com.github.panpf.sketch.viewability

import android.content.Context
import android.view.View

/**
 * ViewAbility attached host
 */
class Host(val view: View, val container: ViewAbilityContainer) {
    val context: Context = view.context
}