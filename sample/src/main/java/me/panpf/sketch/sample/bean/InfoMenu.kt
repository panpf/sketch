package me.panpf.sketch.sample.bean

import me.panpf.adapter.AssemblyAdapter

abstract class InfoMenu(val title: String) {
    open fun getInfo(): String? {
        return null
    }
    abstract fun onClick(adapter: AssemblyAdapter?)
}
