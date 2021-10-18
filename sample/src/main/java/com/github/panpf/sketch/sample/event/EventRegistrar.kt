@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.sample.event

import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class RegisterEvent

