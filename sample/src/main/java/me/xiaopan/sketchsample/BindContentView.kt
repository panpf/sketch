package me.xiaopan.sketchsample

import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class BindContentView(val value: Int)
