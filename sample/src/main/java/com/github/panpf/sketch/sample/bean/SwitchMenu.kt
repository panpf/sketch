package com.github.panpf.sketch.sample.bean

import androidx.lifecycle.MutableLiveData

class SwitchMenu(
    val title: String,
    private val data: MutableLiveData<Boolean>,
    val desc: String? = null,
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
