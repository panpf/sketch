package com.github.panpf.sketch.sample.bean

class MenuItemInfo<T>(
    val groupId: Int,
    private val values: Array<T>,
    private val titles: Array<String>?,
    private val iconResIds: Array<Int>?,
    private val onChangedListener: (oldValue: T, newValue: T) -> Unit
) {
    private var currentIndex = 0
    private val nextIndex: Int
        get() = (currentIndex + 1) % values.size

    val title: String
        get() = titles?.get(nextIndex).orEmpty()

    val iconResId: Int?
        get() = iconResIds?.get(nextIndex)

    fun click() {
        val nextIndex = nextIndex
        val oldValue = values[currentIndex]
        val newValue = values[nextIndex]
        currentIndex = nextIndex
        onChangedListener(oldValue, newValue)
    }
}