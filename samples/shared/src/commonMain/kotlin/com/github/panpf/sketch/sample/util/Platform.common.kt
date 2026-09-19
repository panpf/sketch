package com.github.panpf.sketch.sample.util

expect val Platform.Companion.current: Platform

enum class Platform {
    Android,
    Jvm,
    Js,
    WasmJs,
    Ios,
    Macos, ;

    companion object
}

fun Platform.isAndroid(): Boolean = this == Platform.Android

fun Platform.isIos(): Boolean = this == Platform.Ios

fun Platform.isMacos(): Boolean = this == Platform.Macos

fun Platform.isJvm(): Boolean = this == Platform.Jvm

fun Platform.isJsCommon(): Boolean = this == Platform.Js

fun Platform.isJs(): Boolean = this == Platform.Js

fun Platform.isWasmJs(): Boolean = this == Platform.WasmJs

fun Platform.isMobile(): Boolean =
    this == Platform.Android || this == Platform.Ios