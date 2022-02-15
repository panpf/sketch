package com.github.panpf.sketch.sample.bean

class InfoMenu(
    val title: String,
    val desc: String? = null,
    val getInfo: () -> String?,
    val onClick: () -> Unit
)
