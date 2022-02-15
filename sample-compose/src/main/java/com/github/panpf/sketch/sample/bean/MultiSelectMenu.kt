package com.github.panpf.sketch.sample.bean

class MultiSelectMenu(
    val title: String,
    val desc: String? = null,
    val values: List<String>,
    val value: () -> String,
    val onSelect: (which: Int) -> Unit
)