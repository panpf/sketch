package com.github.panpf.sketch.sample.util

expect val Platform.Companion.current: Platform

enum class Platform {
    Android,
    Desktop,
    Js,
    WasmJs,
    Ios, ;

    companion object
}

fun Platform.isAndroid(): Boolean = this == Platform.Android

fun Platform.isIos(): Boolean = this == Platform.Ios

fun Platform.isDesktop(): Boolean = this == Platform.Desktop

fun Platform.isJsCommon(): Boolean = this == Platform.Js

fun Platform.isJs(): Boolean = this == Platform.Js

fun Platform.isWasmJs(): Boolean = this == Platform.WasmJs

fun Platform.isMobile(): Boolean =
    this == Platform.Android || this == Platform.Ios