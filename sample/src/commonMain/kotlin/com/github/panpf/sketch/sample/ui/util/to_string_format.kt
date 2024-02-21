package com.github.panpf.sketch.sample.ui.util

fun parseToString(toString: String): Item {
    val startIndex = toString.indexOf('(').takeIf { it != -1 } ?: return Item2(toString)
    val endIndex = toString.lastIndexOf(')').takeIf { it != -1 } ?: return Item2(toString)
    val name = toString.substring(0, startIndex)
    val value = toString.substring(startIndex + 1, endIndex)
    val safeValue = safeValue(value)
    val propertyValues: List<Item> = safeValue.split(",")
        .map { it.trim() }
        .mapNotNull {
            val propertyValues = it.split("=")
            when (propertyValues.size) {
                2 -> {
                    val (propertyName, propertyValue) = propertyValues
                    val restorePropertyValue = restoreValue(propertyValue)
                    Item3(propertyName, parseToString(restorePropertyValue))
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

fun Item.formatToString(deep: Int = 0): String {
    return buildString {
        when (this@formatToString) {
            is Item1 -> {
                append(name)
                append("(")
                val currentDeep = deep + 1
                val onlyContent = properties.size == 1 && properties[0] is Item2
                properties.forEachIndexed { index, property ->
                    if (!onlyContent) {
                        appendLine()
                        repeat(currentDeep) { _ -> append("    ") }
                    }
                    append(property.formatToString(currentDeep))
                    if (properties.size > 1 && index != properties.size - 1) {
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
                append(value)
            }

            is Item3 -> {
                append("${name}=${value.formatToString(deep)}")
            }
        }
    }
}

private fun safeValue(value: String): String {
    val startIndex = value.indexOf('(').takeIf { it != -1 } ?: return value
    val endIndex = value.lastIndexOf(')').takeIf { it != -1 } ?: return value
    val content = value.substring(startIndex + 1, endIndex)
        .replace(",", "乀")
        .replace("=", "乁")
    return value.substring(0, startIndex + 1) + content + value.substring(endIndex)
}

private fun restoreValue(value: String): String {
    return value
        .replace("乀", ",")
        .replace("乁", "=")
}

interface Item
data class Item1(val name: String, val properties: List<Item>) : Item
data class Item2(val value: String) : Item
data class Item3(val name: String, val value: Item) : Item