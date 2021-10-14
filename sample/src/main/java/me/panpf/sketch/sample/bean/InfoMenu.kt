package me.panpf.sketch.sample.bean

abstract class InfoMenu(val title: String) {

    open fun getInfo(): String? = null

    abstract fun onClick()
}
