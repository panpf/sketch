package com.github.panpf.sketch.compose.painter

/**
 * Mark this Painter as coming from Sketch, and it implements equals(), hashCode(), toString() methods
 */
interface SketchPainter {

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String
}