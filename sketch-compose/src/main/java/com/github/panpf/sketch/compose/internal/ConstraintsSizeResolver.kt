package com.github.panpf.sketch.compose.internal

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Constraints
import com.github.panpf.sketch.resize.SizeResolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import com.github.panpf.sketch.util.Size as SketchSize

internal class ConstraintsSizeResolver : SizeResolver {

    private val constraints = MutableStateFlow(ZeroConstraints)

    override suspend fun size() = constraints.mapNotNull { it.toSizeOrNull() }.first()

    fun setConstraints(constraints: Constraints) {
        this.constraints.value = constraints
    }
}

internal val ZeroConstraints = Constraints(0, 0, 0, 0)

@Stable
internal fun Constraints.toSizeOrNull(): SketchSize? {
    return if (isZero) {
        null
    } else {
        val width = if (hasBoundedWidth) maxWidth else -1
        val height = if (hasBoundedHeight) maxHeight else -1
        SketchSize(width, height)
    }
}