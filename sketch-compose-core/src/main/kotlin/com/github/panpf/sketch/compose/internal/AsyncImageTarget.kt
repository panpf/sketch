///*
// * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.github.panpf.sketch.compose.internal
//
//import com.github.panpf.sketch.request.Image
//import com.github.panpf.sketch.request.internal.RequestContext
//import com.github.panpf.sketch.target.Target
//
///**
// * Just to show that it's [Target] from AsyncImage
// */
//class AsyncImageTarget(val wrapped: Target) : Target {
//
//    override val supportDisplayCount: Boolean = wrapped.supportDisplayCount
//
//    override fun onStart(requestContext: RequestContext, placeholder: Image?) {
//        wrapped.onStart(requestContext, placeholder)
//    }
//
//    override fun onSuccess(requestContext: RequestContext, result: Image) {
//        wrapped.onSuccess(requestContext, result)
//    }
//
//    override fun onError(requestContext: RequestContext, error: Image?) {
//        wrapped.onError(requestContext, error)
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//        other as AsyncImageTarget
//        if (wrapped != other.wrapped) return false
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return wrapped.hashCode()
//    }
//
//    override fun toString(): String {
//        return "AsyncImageTarget($wrapped)"
//    }
//}