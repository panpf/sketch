package me.panpf.sketch.sample

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.text.TextUtils
import android.util.Log
import me.panpf.sketch.SLog
import me.panpf.sketch.util.ObjectPool
import me.panpf.sketch.util.SketchUtils
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

class MySketchLogProxy(context: Context) : SLog.Proxy {
    private val outLog2SDCard: OutLog2SDCard

    init {
        outLog2SDCard = OutLog2SDCard(context)
    }

    override fun v(tag: String, msg: String): Int {
        outLog2SDCard.out("V", tag, msg, null)
        return Log.v(tag, msg)
    }

    override fun v(tag: String, msg: String, tr: Throwable): Int {
        outLog2SDCard.out("V", tag, msg, tr)
        return Log.v(tag, msg, tr)
    }

    override fun d(tag: String, msg: String): Int {
        outLog2SDCard.out("D", tag, msg, null)
        return Log.d(tag, msg)
    }

    override fun d(tag: String, msg: String, tr: Throwable): Int {
        outLog2SDCard.out("D", tag, msg, tr)
        return Log.d(tag, msg, tr)
    }

    override fun i(tag: String, msg: String): Int {
        outLog2SDCard.out("I", tag, msg, null)
        return Log.i(tag, msg)
    }

    override fun i(tag: String, msg: String, tr: Throwable): Int {
        outLog2SDCard.out("I", tag, msg, tr)
        return Log.i(tag, msg, tr)
    }

    override fun w(tag: String, msg: String): Int {
        outLog2SDCard.out("W", tag, msg, null)
        return Log.w(tag, msg)
    }

    override fun w(tag: String, msg: String, tr: Throwable): Int {
        outLog2SDCard.out("W", tag, msg, tr)
        return Log.w(tag, msg, tr)
    }

    override fun w(tag: String, tr: Throwable): Int {
        outLog2SDCard.out("W", tag, null, tr)
        return Log.w(tag, tr)
    }

    override fun e(tag: String, msg: String): Int {
        outLog2SDCard.out("E", tag, msg, null)
        return Log.e(tag, msg)
    }

    override fun e(tag: String, msg: String, tr: Throwable): Int {
        outLog2SDCard.out("E", tag, msg, tr)
        return Log.e(tag, msg, tr)
    }

    override fun onReplaced() {
        outLog2SDCard.close()
    }

    private class OutLog2SDCard(context: Context) {

        private var logEntryObjectPool: ObjectPool<LogEntry>? = null
        private val logTimeDateFormat: SimpleDateFormat

        private val context = context.applicationContext

        private var handler: Handler? = null
        private var handlerThread: HandlerThread? = null
        private var fileNameDateFormat: SimpleDateFormat? = null

        private var logFileName: String? = null
        private var fileWriter: FileWriter? = null

        private var closed: Boolean = false

        init {
            logEntryObjectPool = ObjectPool(ObjectPool.ObjectFactory { LogEntry(logEntryObjectPool!!) })
            logTimeDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US)
        }

        fun out(level: String, tag: String, msg: String?, tr: Throwable?) {
            if (closed) {
                return
            }

            val time = logTimeDateFormat.format(Date())

            val logEntry = logEntryObjectPool!!.get()
            logEntry[time, level, tag, msg] = tr

            if (handler == null) {
                synchronized(this) {
                    if (handler == null) {
                        handlerThread = HandlerThread("OutLogThread")
                        handlerThread!!.start()
                        handler = Handler(handlerThread!!.looper, Handler.Callback { msg ->
                            if (msg.obj is LogEntry) {
                                writeLog(msg.obj as LogEntry)
                                return@Callback true
                            }
                            false
                        })
                    }
                }
            }

            handler!!.obtainMessage(0, logEntry).sendToTarget()
        }

        fun close() {
            closed = true

            if (handlerThread != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    handlerThread!!.quitSafely()
                } else {
                    handlerThread!!.quit()
                }
                handlerThread = null
            }

            if (fileWriter != null) {
                try {
                    fileWriter!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                fileWriter = null
            }

            if (logEntryObjectPool != null) {
                logEntryObjectPool!!.clear()
                logEntryObjectPool = null
            }
        }

        private fun makeLogFile(newLogFileName: String): File? {
            val dir = context.externalCacheDir ?: return null

            return File(dir, "sketch_log" + File.separator + newLogFileName)
        }

        private fun writeLog(entry: LogEntry) {
            if (closed) {
                return
            }

            if (fileNameDateFormat == null) {
                fileNameDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            }

            val date = Date()
            val newLogFileName = fileNameDateFormat!!.format(date) + ".log"

            if (newLogFileName != logFileName || fileWriter == null) {
                if (fileWriter != null) {
                    SketchUtils.close(fileWriter)
                }

                val logFile = makeLogFile(newLogFileName)
                if (logFile == null) {
                    IllegalStateException("Not found sdcard").printStackTrace()
                    return
                }

                if (!logFile.exists()) {
                    logFile.parentFile.mkdirs()
                    try {
                        logFile.createNewFile()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    if (!logFile.exists()) {
                        IllegalStateException("Create file failed. " + logFile.path).printStackTrace()
                        return
                    }
                }

                try {
                    fileWriter = FileWriter(logFile, true)
                } catch (e: IOException) {
                    e.printStackTrace()
                    return
                }

                logFileName = newLogFileName
            }

            try {
                fileWriter!!.write(entry.time)
                fileWriter!!.write(" ")
                fileWriter!!.write(entry.level)
                fileWriter!!.write(" ")
                fileWriter!!.write(entry.tag)
                if (!TextUtils.isEmpty(entry.message)) {
                    fileWriter!!.write(" ")
                    fileWriter!!.write(entry.message)
                }
                if (entry.tr != null) {
                    fileWriter!!.write("\n")
                    entry.tr!!.printStackTrace(PrintWriter(fileWriter!!))
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                fileWriter!!.write("\n")
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (closed) {
                if (fileWriter != null) {
                    try {
                        fileWriter!!.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    fileWriter = null
                }
            } else {
                entry.recycle()
            }
        }
    }

    private class LogEntry(private val logEntryObjectPool: ObjectPool<LogEntry>) {
        var time: String? = null
        var level: String? = null
        var tag: String? = null
        var message: String? = null
        var tr: Throwable? = null

        operator fun set(time: String?, level: String?, tag: String?, message: String?, tr: Throwable?) {
            this.time = time
            this.level = level
            this.tag = tag
            this.message = message
            this.tr = tr
        }

        fun recycle() {
            set(null, null, null, null, null)
            logEntryObjectPool.put(this)
        }
    }
}
