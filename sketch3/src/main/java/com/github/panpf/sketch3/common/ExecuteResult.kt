//package com.github.panpf.sketch3.common
//
//class ExecuteResult<T>(
//    private val data: T?,
//    private val error: Throwable?,
//    private val canceled: Boolean
//) {
//    fun <R> ifSuccessOrNull(block: (T) -> R): R? {
//        val data = data
//        return if (data != null) {
//            block(data)
//        } else {
//            null
//        }
//    }
//
//    fun <R> ifErrorOrNull(block: (Throwable) -> R): R? {
//        val error = error
//        return if (error != null) {
//            block(error)
//        } else {
//            null
//        }
//    }
//
//    fun <R> ifErrorOrNull(block: () -> R): R? {
//        return if (canceled) {
//            block()
//        } else {
//            null
//        }
//    }
//
//    fun <R> convert(
//        successBlock: (T) -> R,
//        errorBlock: (Throwable) -> R,
//        cancelBlock: () -> R
//    ): R {
//        val data = data
//        val error = error
//        return when {
//            data != null -> successBlock(data)
//            error != null -> errorBlock(error)
//            canceled -> cancelBlock()
//            else -> {
//                throw IllegalStateException("ExecuteResult state error")
//            }
//        }
//    }
//}