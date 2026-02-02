package com.github.panpf.sketch.sample.util

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

fun <T, R> StateFlow<T>.stateMap(transform: (value: T) -> R): StateFlow<R> =
    MappedStateFlow(this, transform)

class MappedStateFlow<T, R>(
    private val source: StateFlow<T>,
    private val transform: (value: T) -> R
) : StateFlow<R> {

    override val replayCache: List<R>
        get() = source.replayCache.map { transform(it) }

    override val value: R
        get() = transform(source.value)

    override suspend fun collect(collector: FlowCollector<R>): Nothing {
        source.collect {
            collector.emit(transform(it))
        }
    }
}