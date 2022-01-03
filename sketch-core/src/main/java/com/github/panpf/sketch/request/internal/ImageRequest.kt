package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.Parameters
import com.github.panpf.sketch.request.RequestDepth

interface ImageRequest {
    val url: String
    val key: String
    val depth: RequestDepth
    val parameters: Parameters?
}

// todo 将三个 Request 全部接口化

//interface A {
//    fun newBuilder(): Builder
//
//    companion object {
//        fun newBuilder(): Builder = Builder()
//    }
//
//    open class Builder {
//        open fun common(): Builder = apply {
//
//        }
//
//        fun a(): Builder = apply {
//
//        }
//
//        open fun build(): A = AImpl()
//    }
//}
//
//class AImpl : A {
//    override fun newBuilder(): Builder = A.Builder()
//}
//
//interface B : A {
//    override fun newBuilder(): Builder
//
//    companion object {
//        fun newBuilder(): Builder = Builder()
//    }
//
//    open class Builder : A.Builder() {
//        override fun common(): Builder = apply {
//            super.common()
//        }
//
//        fun b(): Builder = apply {
//
//        }
//
//        override fun build(): B = BImpl()
//    }
//}
//
//class BImpl : B {
//    override fun newBuilder(): B.Builder = B.Builder()
//}
//
//interface C : B {
//    override fun newBuilder(): Builder
//
//    companion object {
//        fun newBuilder(): Builder = Builder()
//    }
//
//    class Builder : B.Builder() {
//        override fun common(): Builder = apply {
//            super.common()
//        }
//
//        fun c(): A.Builder = apply {
//
//        }
//
//        override fun build(): C = CImpl()
//    }
//}
//
//class CImpl : C {
//    override fun newBuilder(): C.Builder = C.Builder()
//}
//
//fun main() {
//    C.newBuilder().build().newBuilder().build()
//    B.newBuilder().build().newBuilder().build()
//    A.newBuilder().build().newBuilder().build()
//}