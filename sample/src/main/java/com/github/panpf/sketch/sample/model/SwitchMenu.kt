package com.github.panpf.sketch.sample.model

import androidx.lifecycle.MutableLiveData

class SwitchMenu constructor(
    val title: String,
    val desc: String?,
    private val data: MutableLiveData<Boolean>,
    private val reverse: Boolean = false,
) {

    var isChecked: Boolean
        get() = if (reverse) {
            !(data.value ?: false)
        } else {
            data.value ?: false
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
