package com.github.panpf.sketch.viewability

import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.target.DisplayListenerProvider

/**
 * Provides access services for ViewAbility registration, uninstallation, and event callbacks and properties
 */
interface ViewAbilityContainer : DisplayListenerProvider {

    /**
     * ViewAbility List
     */
    val viewAbilityList: List<ViewAbility>

    /**
     * Add a ViewAbility and run it
     */
    fun addViewAbility(viewAbility: ViewAbility)

    /**
     * Delete a ViewAbility
     */
    fun removeViewAbility(viewAbility: ViewAbility)

    /**
     * Call the parent class's setOnClickListener() method
     */
    fun superSetOnClickListener(listener: OnClickListener?)

    /**
     * Call the parent class's setOnLongClickListener() method
     */
    fun superSetOnLongClickListener(listener: OnLongClickListener?)

    /**
     * Call the parent class's setScaleType() method
     */
    fun superSetScaleType(scaleType: ScaleType)

    /**
     * Call the parent class's getScaleType() method
     */
    fun superGetScaleType(): ScaleType

    /**
     * Call the parent class's setImageMatrix() method
     */
    fun superSetImageMatrix(matrix: Matrix?)

    /**
     * Call the parent class's getImageMatrix() method
     */
    fun superGetImageMatrix(): Matrix?

    /**
     * Get Drawable
     */
    fun getDrawable(): Drawable?

    /**
     * Submit an display image request
     */
    fun submitRequest(request: DisplayRequest)
}