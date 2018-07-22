package me.panpf.sketch.sample.base

import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class BindContentView(val value: Int)
