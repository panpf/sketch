package com.github.panpf.sketch.util

import com.github.panpf.sketch.annotation.IntDef

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
    var level: Int = rootLogger?.level ?: level ?: Info
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
                    level = Warn,
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
        if (isLoggable(Verbose)) {
            pipeline.log(Verbose, tag, assembleMessage(msg), null)
        }
    }

    /**
     * Print a log with the VERBOSE level
     */
    fun v(lazyMessage: () -> String) {
        if (isLoggable(Verbose)) {
            pipeline.log(Verbose, tag, assembleMessage(lazyMessage()), null)
        }
    }

    /**
     * Print a log with the VERBOSE level
     */
    fun v(throwable: Throwable?, msg: String) {
        if (isLoggable(Verbose)) {
            pipeline.log(Verbose, tag, assembleMessage(msg), throwable)
        }
    }

    /**
     * Print a log with the VERBOSE level
     */
    fun v(throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Verbose)) {
            pipeline.log(Verbose, tag, assembleMessage(lazyMessage()), throwable)
        }
    }


    /**
     * Print a log with the DEBUG level
     */
    fun d(msg: String) {
        if (isLoggable(Debug)) {
            pipeline.log(Debug, tag, assembleMessage(msg), null)
        }
    }

    /**
     * Print a log with the DEBUG level
     */
    fun d(lazyMessage: () -> String) {
        if (isLoggable(Debug)) {
            pipeline.log(Debug, tag, assembleMessage(lazyMessage()), null)
        }
    }

    /**
     * Print a log with the DEBUG level
     */
    fun d(throwable: Throwable?, msg: String) {
        if (isLoggable(Debug)) {
            pipeline.log(Debug, tag, assembleMessage(msg), throwable)
        }
    }

    /**
     * Print a log with the DEBUG level
     */
    fun d(throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Debug)) {
            pipeline.log(Debug, tag, assembleMessage(lazyMessage()), throwable)
        }
    }


    /**
     * Print a log with the INFO level
     */
    fun i(msg: String) {
        if (isLoggable(Info)) {
            pipeline.log(Info, tag, assembleMessage(msg), null)
        }
    }

    /**
     * Print a log with the INFO level
     */
    fun i(lazyMessage: () -> String) {
        if (isLoggable(Info)) {
            pipeline.log(Info, tag, assembleMessage(lazyMessage()), null)
        }
    }

    /**
     * Print a log with the INFO level
     */
    fun i(throwable: Throwable?, msg: String) {
        if (isLoggable(Info)) {
            pipeline.log(Info, tag, assembleMessage(msg), throwable)
        }
    }

    /**
     * Print a log with the INFO level
     */
    fun i(throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Info)) {
            pipeline.log(Info, tag, assembleMessage(lazyMessage()), throwable)
        }
    }


    /**
     * Print a log with the WARN level
     */
    fun w(msg: String) {
        if (isLoggable(Warn)) {
            pipeline.log(Warn, tag, assembleMessage(msg), null)
        }
    }

    /**
     * Print a log with the WARN level
     */
    fun w(lazyMessage: () -> String) {
        if (isLoggable(Warn)) {
            pipeline.log(Warn, tag, assembleMessage(lazyMessage()), null)
        }
    }

    /**
     * Print a log with the WARN level
     */
    fun w(throwable: Throwable?, msg: String) {
        if (isLoggable(Warn)) {
            pipeline.log(Warn, tag, assembleMessage(msg), throwable)
        }
    }

    /**
     * Print a log with the WARN level
     */
    fun w(throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Warn)) {
            pipeline.log(Warn, tag, assembleMessage(lazyMessage()), throwable)
        }
    }


    /**
     * Print a log with the ERROR level
     */
    fun e(msg: String) {
        if (isLoggable(Error)) {
            pipeline.log(Error, tag, assembleMessage(msg), null)
        }
    }

    /**
     * Print a log with the ERROR level
     */
    fun e(lazyMessage: () -> String) {
        if (isLoggable(Error)) {
            pipeline.log(Error, tag, assembleMessage(lazyMessage()), null)
        }
    }

    /**
     * Print a log with the ERROR level
     */
    fun e(throwable: Throwable?, msg: String) {
        if (isLoggable(Error)) {
            pipeline.log(Error, tag, assembleMessage(msg), throwable)
        }
    }

    /**
     * Print a log with the ERROR level
     */
    fun e(throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Error)) {
            pipeline.log(Error, tag, assembleMessage(lazyMessage()), throwable)
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
        if (other !is Logger) return false
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
    @IntDef(Verbose, Debug, Info, Warn, Error, Assert)
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
    annotation class Level

    @Suppress("ConstPropertyName")
    companion object {
        /**
         * Priority constant for the println method; use Log.v.
         */
        const val Verbose = 2

        /**
         * Priority constant for the println method; use Log.d.
         */
        // The name cannot be in all uppercase 'DEBUG'. This will cause the ComposeApp.h file to fail to compile in Kotlin/Native.
        const val Debug = 3

        /**
         * Priority constant for the println method; use Log.i.
         */
        const val Info = 4

        /**
         * Priority constant for the println method; use Log.w.
         */
        const val Warn = 5

        /**
         * Priority constant for the println method; use Log.e.
         */
        const val Error = 6

        /**
         * Priority constant for the println method.
         */
        const val Assert = 7

        /**
         * Get the name of the level
         */
        fun levelName(level: Int): String = when (level) {
            Verbose -> "VERBOSE"
            Debug -> "DEBUG"
            Info -> "INFO"
            Warn -> "WARN"
            Error -> "ERROR"
            Assert -> "ASSERT"
            else -> "UNKNOWN"
        }

        /**
         * Get the level of the name
         */
        fun level(levelName: String): Int = when (levelName) {
            "VERBOSE" -> Verbose
            "DEBUG" -> Debug
            "INFO" -> Info
            "WARN" -> Warn
            "ERROR" -> Error
            "ASSERT" -> Assert
            else -> throw IllegalArgumentException("Unknown level name: $levelName")
        }

        val levels = arrayOf(Verbose, Debug, Info, Warn, Error, Assert)
    }

    /**
     * The pipeline of the log
     */
    interface Pipeline {
        fun log(level: Int, tag: String, msg: String, tr: Throwable?)
        fun flush()
    }
}