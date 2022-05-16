package com.github.panpf.sketch.sample.model

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface SwitchMenu{

    val title: String
    val desc: String?
    var isChecked: Boolean
}

class SwitchMenuLiveData constructor(
    override val title: String,
    override val desc: String?,
    private val data: MutableLiveData<Boolean>,
    private val reverse: Boolean = false,
): SwitchMenu {

    override var isChecked: Boolean
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

class SwitchMenuFlow constructor(
    override val title: String,
    override val desc: String?,
    private val data: MutableStateFlow<Boolean>,
    private val reverse: Boolean = false,
): SwitchMenu {

    override var isChecked: Boolean
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
