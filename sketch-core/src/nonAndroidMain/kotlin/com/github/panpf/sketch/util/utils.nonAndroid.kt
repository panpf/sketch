package com.github.panpf.sketch.util


internal actual fun isMainThread(): Boolean {
    // JVM EventQueue.isDispatchThread()
    // iOS
//    import Foundation
//
//    func isMainThread() -> Bool {
//        return Thread.isMainThread
//    }
    return true
}

internal actual fun requiredMainThread() {
}

internal actual fun requiredWorkThread() {

}