package com.github.panpf.sketch.sample.util


expect fun isDebugMode(): Boolean

expect val Platform.Companion.current: Platform

enum class Platform {
    Android,
    Desktop,
    Js,
    Ios, ;

    companion object
}

fun Platform.isAndroid(): Boolean = this == Platform.Android

fun Platform.isIos(): Boolean = this == Platform.Ios

fun Platform.isDesktop(): Boolean = this == Platform.Desktop

fun Platform.isJs(): Boolean = this == Platform.Js

fun Platform.isMobile(): Boolean =
    this == Platform.Android || this == Platform.Ios