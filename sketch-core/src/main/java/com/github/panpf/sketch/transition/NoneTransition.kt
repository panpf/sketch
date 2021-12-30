//package com.github.panpf.sketch.transition
//
//import com.github.panpf.sketch.request.DisplayResult
//import com.github.panpf.sketch.request.ExecuteResult
//import com.github.panpf.sketch.request.internal.ImageResult
//
///**
// * A transition that applies the [ImageResult] on the [TransitionTarget] without animating.
// */
//internal class NoneTransition(
//    private val target: TransitionTarget,
//    private val result: ExecuteResult<DisplayResult>
//) : Transition {
//
//    override fun transition() {
//        when (result) {
//            is ExecuteResult.Success -> target.onSuccess(result.data.drawable)
//            is ExecuteResult.Error -> target.onError(result.throwable)
//        }
//    }
//
//    class Factory : Transition.Factory {
//
//        override fun create(target: TransitionTarget, result: ImageResult): Transition {
//            return NoneTransition(target, result)
//        }
//
//        override fun equals(other: Any?) = other is Factory
//
//        override fun hashCode() = javaClass.hashCode()
//    }
//}
