package com.github.panpf.sketch.sample.ui.util

/*
 * "PainterImage(painter=ResizeAnimatablePainter(painter=DrawableAnimatablePainter(drawable=AnimatableDrawable(drawable=ScaledAnimatedImageDrawable(drawable=AnimatedImageDrawable(240x240), fitScale=true))), size=Size(346.0x346.0), scale=CENTER_CROP), shareable=false)"
 */

fun Any.toStringFormat(): String =
    formatItemToString(parseToStringToItem(this@toStringFormat.toString()))

private fun parseToStringToItem(toString: String): Item {
    val startIndex = toString.indexOf('(').takeIf { it != -1 } ?: return Item2(toString)
    val endIndex = toString.lastIndexOf(')').takeIf { it != -1 } ?: return Item2(toString)
    val name = toString.substring(0, startIndex)
    val value = toString.substring(startIndex + 1, endIndex)
    val safeValue = encodeToStringString(value)
    val propertyValues: List<Item> = safeValue.split(",")
        .map { it.trim() }
        .mapNotNull {
            val propertyValues = it.split("=")
            when (propertyValues.size) {
                2 -> {
                    val (propertyName, propertyValue) = propertyValues
                    val restorePropertyValue = decodeToStringString(propertyValue)
                    Item3(propertyName, parseToStringToItem(restorePropertyValue))
                }

                1 -> {
                    Item2(propertyValues[0])
                }

                else -> {
                    null
                }
            }
        }
    return Item1(name, propertyValues)
}

private fun formatItemToString(item: Item, deep: Int = 0): String {
    return buildString {
        when (item) {
            is Item1 -> {
                append(item.name)
                append("(")
                val currentDeep = deep + 1
                val onlyContent = item.properties.size == 1 && item.properties[0] is Item2
                item.properties.forEachIndexed { index, property ->
                    if (!onlyContent) {
                        appendLine()
                        repeat(currentDeep) { _ -> append("    ") }
                    }
                    append(formatItemToString(property, currentDeep))
                    if (item.properties.size > 1 && index != item.properties.size - 1) {
                        append(",")
                    }
                }
                if (!onlyContent) {
                    appendLine()
                    repeat(deep) { _ -> append("    ") }
                }
                append(")")
            }

            is Item2 -> {
                append(item.value)
            }

            is Item3 -> {
                append("${item.name}=${formatItemToString(item.value, deep)}")
            }
        }
    }
}

private fun encodeToStringString(value: String): String {
    return buildString {
        var bracketCount = 0
        value.forEach { char ->
            if (char == '(') {
                bracketCount++
                append(char)
            } else if (char == ')') {
                bracketCount--
                append(char)
            } else if (char == ',') {
                if (bracketCount > 0) {
                    append("乀")
                } else {
                    append(char)
                }
            } else if (char == '=') {
                if (bracketCount > 0) {
                    append("乁")
                } else {
                    append(char)
                }
            } else {
                append(char)
            }
        }
    }
}

private fun decodeToStringString(value: String): String {
    return buildString {
        var bracketCount = 0
        value.forEach { char ->
            if (char == '(') {
                bracketCount++
                append(char)
            } else if (char == ')') {
                bracketCount--
                append(char)
            } else if (char == '乀') {
                if (bracketCount > 0) {
                    append(",")
                } else {
                    append(char)
                }
            } else if (char == '乁') {
                if (bracketCount > 0) {
                    append("=")
                } else {
                    append(char)
                }
            } else {
                append(char)
            }
        }
    }
}

private interface Item
private data class Item1(val name: String, val properties: List<Item>) : Item
private data class Item2(val value: String) : Item
private data class Item3(val name: String, val value: Item) : Item