package com.github.panpf.sketch.sample.model

import com.github.panpf.sketch.sample.util.BooleanMmkvData

interface SwitchMenu {

    val title: String
    val desc: String?
    var isChecked: Boolean
    val onLongClick: (() -> Unit)?
}

class SwitchMenuFlow constructor(
    override val title: String,
    override val desc: String?,
    private val data: BooleanMmkvData,
    private val reverse: Boolean = false,
    override val onLongClick: (() -> Unit)? = null,
) : SwitchMenu {

    override var isChecked: Boolean
        get() = if (reverse) {
            !data.value
        } else {
            data.value
        }
        set(value) {
            val newValue = if (reverse) {
                !value
            } else {
                value
            }
            data.value = newValue
        }
}
