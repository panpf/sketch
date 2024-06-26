package com.github.panpf.sketch.painter

/**
 * Interface that painters supporting animations should implement.
 */
interface AnimatablePainter {
    /**
     * Starts the drawable's animation.
     */
    fun start()

    /**
     * Stops the drawable's animation.
     */
    fun stop()

    /**
     * Indicates whether the animation is running.
     *
     * @return True if the animation is running, false otherwise.
     */
    fun isRunning(): Boolean
}