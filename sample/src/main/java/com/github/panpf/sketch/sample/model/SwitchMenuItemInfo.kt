package com.github.panpf.sketch.sample.model

class SwitchMenuItemInfo<T>(
    private val values: Array<T>,
    initValue: T,
    private val titles: Array<String>?,
    private val iconResIds: Array<Int>?,
    override val showAsAction: Int,
    private val onChangedListener: (oldValue: T, newValue: T) -> Unit,
) : MenuItemInfo {
    private var currentIndex = values.indexOf(initValue)
    private val nextIndex: Int
        get() = (currentIndex + 1) % values.size

    override val title: String
        get() = titles?.get(nextIndex).orEmpty()

    override val iconResId: Int?
        get() = iconResIds?.get(nextIndex)

    fun click() {
        val nextIndex = nextIndex
        val oldValue = values[currentIndex]
        val newValue = values[nextIndex]
        currentIndex = nextIndex
        onChangedListener(oldValue, newValue)
    }
}