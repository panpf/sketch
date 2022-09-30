package com.github.panpf.sketch.request


private const val FROM_COMPOSE_KEY = "sketch#fromCompose"

fun DisplayRequest.Builder.setFromCompose(enabled: Boolean): DisplayRequest.Builder = apply {
    if (enabled) {
        setParameter(key = FROM_COMPOSE_KEY, value = true, cacheKey = null)
    } else {
        removeParameter(key = FROM_COMPOSE_KEY)
    }
}

fun ImageOptions.Builder.setFromCompose(enabled: Boolean): ImageOptions.Builder = apply {
    if (enabled) {
        setParameter(key = FROM_COMPOSE_KEY, value = true, cacheKey = null)
    } else {
        removeParameter(key = FROM_COMPOSE_KEY)
    }
}

fun DisplayRequest.isFromCompose(): Boolean =
    (parameters?.get(FROM_COMPOSE_KEY) as Boolean?) == true

fun ImageOptions.isFromCompose(): Boolean =
    (parameters?.get(FROM_COMPOSE_KEY) as Boolean?) == true