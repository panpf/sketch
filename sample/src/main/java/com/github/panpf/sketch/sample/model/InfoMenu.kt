package com.github.panpf.sketch.sample.model

class InfoMenu(
    val title: String,
    val desc: String? = null,
    val info: String? = null,
    val onClick: () -> Unit,
    val onLongClick: (() -> Unit)? = null,
)
