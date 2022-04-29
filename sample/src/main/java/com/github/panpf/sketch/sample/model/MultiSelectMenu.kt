package com.github.panpf.sketch.sample.model

class MultiSelectMenu(
    val title: String,
    val desc: String? = null,
    val values: List<String>,
    val getValue: () -> String,
    val onSelect: (which: Int, value: String) -> Unit
)