//package com.github.panpf.sketch.test.utils
//
//import kotlinx.coroutines.CancellationException
//import kotlinx.coroutines.ChildHandle
//import kotlinx.coroutines.ChildJob
//import kotlinx.coroutines.CompletionHandler
//import kotlinx.coroutines.Deferred
//import kotlinx.coroutines.DisposableHandle
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.InternalCoroutinesApi
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.selects.SelectClause0
//import kotlinx.coroutines.selects.SelectClause1
//import kotlin.coroutines.CoroutineContext
//
//class TestDeferred<T>(val job: Job) : Deferred<T> {
//    override val children: Sequence<Job>
//        get() = job.children
//    override val isActive: Boolean
//        get() = job.isActive
//    override val isCancelled: Boolean
//        get() = job.isCancelled
//    override val isCompleted: Boolean
//        get() = job.isCompleted
//    override val key: CoroutineContext.Key<*>
//        get() = job.key
//    override val onAwait: SelectClause1<T>
//        get() = TODO("Not yet implemented")
//    override val onJoin: SelectClause0
//        get() = job.onJoin
//
//    @ExperimentalCoroutinesApi
//    override val parent: Job?
//        get() = job.parent
//
//    @InternalCoroutinesApi
//    override fun attachChild(child: ChildJob): ChildHandle {
//        job.attachChild(child)
//    }
//
//    override suspend fun await(): T {
//        TODO("Not yet implemented")
//    }
//
//    override fun cancel(cause: Throwable?): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun cancel(cause: CancellationException?) {
//        TODO("Not yet implemented")
//    }
//
//    @InternalCoroutinesApi
//    override fun getCancellationException(): CancellationException {
//        TODO("Not yet implemented")
//    }
//
//    @ExperimentalCoroutinesApi
//    override fun getCompleted(): T {
//        TODO("Not yet implemented")
//    }
//
//    @ExperimentalCoroutinesApi
//    override fun getCompletionExceptionOrNull(): Throwable? {
//        TODO("Not yet implemented")
//    }
//
//    @InternalCoroutinesApi
//    override fun invokeOnCompletion(
//        onCancelling: Boolean,
//        invokeImmediately: Boolean,
//        handler: CompletionHandler
//    ): DisposableHandle {
//        TODO("Not yet implemented")
//    }
//
//    override fun invokeOnCompletion(handler: CompletionHandler): DisposableHandle {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun join() {
//        TODO("Not yet implemented")
//    }
//
//    override fun start(): Boolean {
//        TODO("Not yet implemented")
//    }
//
//
//}