package com.github.panpf.sketch.sample.ui.util

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resumeWithException


inline fun <reified T, R> stateCombine(
    sources: Iterable<StateFlow<T>>,
    crossinline transform: (Array<T>) -> R
): StateFlow<R> = object : StateFlow<R> {

    override val replayCache: List<R>
        get() = sources.map { it.replayCache }
            .generateCombinations()
            .map { transform(it.toTypedArray()) }

    override val value: R
        get() = transform(sources.map { it.value }.toTypedArray())

    override suspend fun collect(collector: FlowCollector<R>): Nothing = coroutineScope {
        suspendCancellableCoroutine<Nothing> { continuation ->
            val flow: Flow<R> = combine(flows = sources, transform)
            val job = launch {
                try {
                    flow.collect(collector)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                continuation.resumeWithException(CancellationException("Flow collection completed"))
            }
            continuation.invokeOnCancellation { job.cancel() }
        }
    }
}

fun <T> List<List<T>>.generateCombinations(): List<List<T>> {
    val lists = this
    if (lists.isEmpty()) return listOf(emptyList())

    val result = mutableListOf<List<T>>()
    val firstList = lists[0]
    val remainingLists = lists.subList(1, lists.size)

    for (item in firstList) {
        val combinations = remainingLists.generateCombinations()
        for (combination in combinations) {
            result.add(listOf(item) + combination)
        }
    }

    return result
}