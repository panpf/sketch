package me.panpf.sketch.sample.bean

import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter

abstract class InfoMenu(val title: String) {
    open fun getInfo(): String? {
        return null
    }
    abstract fun onClick(adapter: AssemblyRecyclerAdapter)
}
