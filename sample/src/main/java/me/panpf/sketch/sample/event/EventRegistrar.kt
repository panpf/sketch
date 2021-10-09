@file:Suppress("DEPRECATION")

package me.panpf.sketch.sample.event

import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class RegisterEvent

