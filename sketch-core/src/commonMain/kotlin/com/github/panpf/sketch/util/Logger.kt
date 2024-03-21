package com.github.panpf.sketch.util

import androidx.annotation.IntDef

expect fun platformLogPipeline(): Logger.Pipeline

/**
 * Used to print log
 */
class Logger(
    /**
     * The tag of the log
     */
    val tag: String = "Sketch",

    /**
     * The module name of the log
     */
    val module: String? = null,

    /**
     * Initial Level
     */
    @Level
    level: Int? = null,

    /**
     * Specifies the output pipeline of the log
     */
    private val pipeline: Pipeline = platformLogPipeline(),

    /**
     * The root logger, in order for all derived loggers to use the same level and pipeline
     */
    private val rootLogger: Logger? = null,
) {

    /**
     * The level of the log. The level of the root logger will be modified directly
     */
    var level: Int = rootLogger?.level ?: level ?: INFO
        get() = rootLogger?.level ?: field
        set(value) {
            val rootLogger = rootLogger
            if (rootLogger != null) {
                rootLogger.level = value
                if (field != value) {
                    field = value
                }
            } else if (field != value) {
                val oldLevel = field
                field = value
                val oldLevelName = levelName(oldLevel)
                val newLevelName = levelName(value)
                pipeline.log(
                    level = WARN,
                    tag = tag,
                    msg = "Logger@${this.toHexString()}. setLevel. $oldLevelName -> $newLevelName",
                    tr = null
                )
            }
        }

    /**
     * To create a new logger based on the current logger, you can only modify the values of module and showThreadName
     */
    fun newLogger(module: String? = this.module): Logger = Logger(
        tag = tag,
        module = module,
        level = level,
        pipeline = pipeline,
        rootLogger = rootLogger ?: this
    )


    /**
     * Print a log with the VERBOSE level
     */
    fun v(msg: String) {
        if (isLoggable(VERBOSE)) {
            pipeline.log(VERBOSE, tag, assembleMessage(msg), null)
        }
    }

    /**
     * Print a log with the VERBOSE level
     */
    fun v(lazyMessage: () -> String) {
        if (isLoggable(VERBOSE)) {
            pipeline.log(VERBOSE, tag, assembleMessage(lazyMessage()), null)
        }
    }

    /**
     * Print a log with the VERBOSE level
     */
    fun v(throwable: Throwable?, msg: String) {
        if (isLoggable(VERBOSE)) {
            pipeline.log(VERBOSE, tag, assembleMessage(msg), throwable)
        }
    }

    /**
     * Print a log with the VERBOSE level
     */
    fun v(throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(VERBOSE)) {
            pipeline.log(VERBOSE, tag, assembleMessage(lazyMessage()), throwable)
        }
    }


    /**
     * Print a log with the DEBUG level
     */
    fun d(msg: String) {
        if (isLoggable(DEBUG)) {
            pipeline.log(DEBUG, tag, assembleMessage(msg), null)
        }
    }

    /**
     * Print a log with the DEBUG level
     */
    fun d(lazyMessage: () -> String) {
        if (isLoggable(DEBUG)) {
            pipeline.log(DEBUG, tag, assembleMessage(lazyMessage()), null)
        }
    }

    /**
     * Print a log with the DEBUG level
     */
    fun d(throwable: Throwable?, msg: String) {
        if (isLoggable(DEBUG)) {
            pipeline.log(DEBUG, tag, assembleMessage(msg), throwable)
        }
    }

    /**
     * Print a log with the DEBUG level
     */
    fun d(throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(DEBUG)) {
            pipeline.log(DEBUG, tag, assembleMessage(lazyMessage()), throwable)
        }
    }


    /**
     * Print a log with the INFO level
     */
    fun i(msg: String) {
        if (isLoggable(INFO)) {
            pipeline.log(INFO, tag, assembleMessage(msg), null)
        }
    }

    /**
     * Print a log with the INFO level
     */
    fun i(lazyMessage: () -> String) {
        if (isLoggable(INFO)) {
            pipeline.log(INFO, tag, assembleMessage(lazyMessage()), null)
        }
    }

    /**
     * Print a log with the INFO level
     */
    fun i(throwable: Throwable?, msg: String) {
        if (isLoggable(INFO)) {
            pipeline.log(INFO, tag, assembleMessage(msg), throwable)
        }
    }

    /**
     * Print a log with the INFO level
     */
    fun i(throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(INFO)) {
            pipeline.log(INFO, tag, assembleMessage(lazyMessage()), throwable)
        }
    }


    /**
     * Print a log with the WARN level
     */
    fun w(msg: String) {
        if (isLoggable(WARN)) {
            pipeline.log(WARN, tag, assembleMessage(msg), null)
        }
    }

    /**
     * Print a log with the WARN level
     */
    fun w(lazyMessage: () -> String) {
        if (isLoggable(WARN)) {
            pipeline.log(WARN, tag, assembleMessage(lazyMessage()), null)
        }
    }

    /**
     * Print a log with the WARN level
     */
    fun w(throwable: Throwable?, msg: String) {
        if (isLoggable(WARN)) {
            pipeline.log(WARN, tag, assembleMessage(msg), throwable)
        }
    }

    /**
     * Print a log with the WARN level
     */
    fun w(throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(WARN)) {
            pipeline.log(WARN, tag, assembleMessage(lazyMessage()), throwable)
        }
    }


    /**
     * Print a log with the ERROR level
     */
    fun e(msg: String) {
        if (isLoggable(ERROR)) {
            pipeline.log(ERROR, tag, assembleMessage(msg), null)
        }
    }

    /**
     * Print a log with the ERROR level
     */
    fun e(lazyMessage: () -> String) {
        if (isLoggable(ERROR)) {
            pipeline.log(ERROR, tag, assembleMessage(lazyMessage()), null)
        }
    }

    /**
     * Print a log with the ERROR level
     */
    fun e(throwable: Throwable?, msg: String) {
        if (isLoggable(ERROR)) {
            pipeline.log(ERROR, tag, assembleMessage(msg), throwable)
        }
    }

    /**
     * Print a log with the ERROR level
     */
    fun e(throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(ERROR)) {
            pipeline.log(ERROR, tag, assembleMessage(lazyMessage()), throwable)
        }
    }


    /**
     * Print a log with the specified level
     */
    fun log(@Level level: Int, msg: String) {
        if (isLoggable(level)) {
            pipeline.log(level, tag, assembleMessage(msg), null)
        }
    }

    /**
     * Print a log with the specified level
     */
    fun log(@Level level: Int, lazyMessage: () -> String) {
        if (isLoggable(level)) {
            pipeline.log(level, tag, assembleMessage(lazyMessage()), null)
        }
    }

    /**
     * Print a log with the specified level
     */
    fun log(@Level level: Int, throwable: Throwable?, msg: String) {
        if (isLoggable(level)) {
            pipeline.log(level, tag, assembleMessage(msg), throwable)
        }
    }

    /**
     * Print a log with the specified level
     */
    fun log(@Level level: Int, throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(level)) {
            pipeline.log(level, tag, assembleMessage(lazyMessage()), throwable)
        }
    }

    fun isLoggable(level: Int): Boolean {
        val logger = rootLogger ?: this
        return level >= logger.level
    }


    /**
     * Flush the log pipeline
     */
    fun flush() {
        val logger = rootLogger ?: this
        logger.pipeline.flush()
    }

    private fun assembleMessage(msg: String): String =
        if (module?.isNotEmpty() == true) "$module. $msg" else msg

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Logger
        if (tag != other.tag) return false
        if (module != other.module) return false
        return true
    }

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + (module?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Logger(tag='$tag', module=$module, level=${levelName(level)}, pipeline=$pipeline)"
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT)
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
    annotation class Level

    companion object {
        /**
         * Priority constant for the println method; use Log.v.
         */
        const val VERBOSE = 2

        /**
         * Priority constant for the println method; use Log.d.
         */
        const val DEBUG = 3

        /**
         * Priority constant for the println method; use Log.i.
         */
        const val INFO = 4

        /**
         * Priority constant for the println method; use Log.w.
         */
        const val WARN = 5

        /**
         * Priority constant for the println method; use Log.e.
         */
        const val ERROR = 6

        /**
         * Priority constant for the println method.
         */
        const val ASSERT = 7

        /**
         * Get the name of the level
         */
        fun levelName(level: Int): String = when (level) {
            VERBOSE -> "VERBOSE"
            DEBUG -> "DEBUG"
            INFO -> "INFO"
            WARN -> "WARN"
            ERROR -> "ERROR"
            ASSERT -> "ASSERT"
            else -> "UNKNOWN"
        }

        /**
         * Get the level of the name
         */
        fun level(levelName: String): Int = when (levelName) {
            "VERBOSE" -> VERBOSE
            "DEBUG" -> DEBUG
            "INFO" -> INFO
            "WARN" -> WARN
            "ERROR" -> ERROR
            "ASSERT" -> ASSERT
            else -> throw IllegalArgumentException("Unknown level name: $levelName")
        }

        val levels = arrayOf(VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT)
    }

    /**
     * The pipeline of the log
     */
    interface Pipeline {
        fun log(level: Int, tag: String, msg: String, tr: Throwable?)
        fun flush()
    }
}