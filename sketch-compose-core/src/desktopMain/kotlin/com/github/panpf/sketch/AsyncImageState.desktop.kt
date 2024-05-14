//package com.github.panpf.sketch
//
//import com.github.panpf.sketch.request.ImageRequest
//import com.github.panpf.sketch.request.ImageRequest.Builder
//import com.github.panpf.sketch.request.internal.JvmBitmapToComposeBitmapRequestInterceptor
//import com.github.panpf.sketch.request.internal.SkiaBitmapToComposeBitmapRequestInterceptor
//
//actual fun updateRequestBuilder(request: ImageRequest, builder: Builder) {
//    builder.mergeComponents {
//        addRequestInterceptor(JvmBitmapToComposeBitmapRequestInterceptor())
//        addRequestInterceptor(SkiaBitmapToComposeBitmapRequestInterceptor())
//    }
//}