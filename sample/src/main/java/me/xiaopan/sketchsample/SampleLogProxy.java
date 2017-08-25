package me.xiaopan.sketchsample;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.xiaopan.sketch.SLogProxy;
import me.xiaopan.sketch.util.ObjectPool;
import me.xiaopan.sketch.util.SketchUtils;

public class SampleLogProxy implements SLogProxy {
    private OutLog2SDCard outLog2SDCard;

    public SampleLogProxy(Context context) {
        outLog2SDCard = new OutLog2SDCard(context);
    }

    @Override
    public int v(String tag, String msg) {
        outLog2SDCard.out("V", tag, msg, null);
        return Log.v(tag, msg);
    }

    @Override
    public int v(String tag, String msg, Throwable tr) {
        outLog2SDCard.out("V", tag, msg, tr);
        return Log.v(tag, msg, tr);
    }

    @Override
    public int d(String tag, String msg) {
        outLog2SDCard.out("D", tag, msg, null);
        return Log.d(tag, msg);
    }

    @Override
    public int d(String tag, String msg, Throwable tr) {
        outLog2SDCard.out("D", tag, msg, tr);
        return Log.d(tag, msg, tr);
    }

    @Override
    public int i(String tag, String msg) {
        outLog2SDCard.out("I", tag, msg, null);
        return Log.i(tag, msg);
    }

    @Override
    public int i(String tag, String msg, Throwable tr) {
        outLog2SDCard.out("I", tag, msg, tr);
        return Log.i(tag, msg, tr);
    }

    @Override
    public int w(String tag, String msg) {
        outLog2SDCard.out("W", tag, msg, null);
        return Log.w(tag, msg);
    }

    @Override
    public int w(String tag, String msg, Throwable tr) {
        outLog2SDCard.out("W", tag, msg, tr);
        return Log.w(tag, msg, tr);
    }

    @Override
    public int w(String tag, Throwable tr) {
        outLog2SDCard.out("W", tag, null, tr);
        return Log.w(tag, tr);
    }

    @Override
    public int e(String tag, String msg) {
        outLog2SDCard.out("E", tag, msg, null);
        return Log.e(tag, msg);
    }

    @Override
    public int e(String tag, String msg, Throwable tr) {
        outLog2SDCard.out("E", tag, msg, tr);
        return Log.e(tag, msg, tr);
    }

    @Override
    public void onReplaced() {
        outLog2SDCard.close();
    }

    private static class OutLog2SDCard {

        private ObjectPool<LogEntry> logEntryObjectPool;
        private SimpleDateFormat logTimeDateFormat;

        private Context context;

        private Handler handler;
        private HandlerThread handlerThread;
        private SimpleDateFormat fileNameDateFormat;

        private String logFileName;
        private FileWriter fileWriter;

        private boolean closed;

        public OutLog2SDCard(Context context) {
            this.context = context.getApplicationContext();
            logEntryObjectPool = new ObjectPool<>(new ObjectPool.ObjectFactory<LogEntry>() {
                @Override
                public LogEntry newObject() {
                    return new LogEntry(logEntryObjectPool);
                }
            });
            logTimeDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US);
        }

        private void out(String level, String tag, String msg, Throwable tr) {
            if (closed) {
                return;
            }

            String time = logTimeDateFormat.format(new Date());

            LogEntry logEntry = logEntryObjectPool.get();
            logEntry.set(time, level, tag, msg, tr);

            if (handler == null) {
                synchronized (this) {
                    if (handler == null) {
                        handlerThread = new HandlerThread("OutLogThread");
                        handlerThread.start();
                        handler = new Handler(handlerThread.getLooper(), new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message msg) {
                                if (msg.obj instanceof LogEntry) {
                                    writeLog((LogEntry) msg.obj);
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                }
            }

            handler.obtainMessage(0, logEntry).sendToTarget();
        }

        private void close() {
            closed = true;

            if (handlerThread != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    handlerThread.quitSafely();
                } else {
                    handlerThread.quit();
                }
                handlerThread = null;
            }

            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileWriter = null;
            }

            if (logEntryObjectPool != null) {
                logEntryObjectPool.clear();
                logEntryObjectPool = null;
            }
        }

        private File makeLogFile(String newLogFileName) {
            File dir = context.getExternalCacheDir();
            if (dir == null) {
                return null;
            }

            return new File(dir, "sketch_log" + File.separator + newLogFileName);
        }

        private void writeLog(LogEntry entry) {
            if (closed) {
                return;
            }

            if (fileNameDateFormat == null) {
                fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            }

            Date date = new Date();
            String newLogFileName = fileNameDateFormat.format(date) + ".log";

            if (!newLogFileName.equals(logFileName) || fileWriter == null) {
                if (fileWriter != null) {
                    SketchUtils.close(fileWriter);
                }

                File logFile = makeLogFile(newLogFileName);
                if (logFile == null) {
                    new IllegalStateException("Not found sdcard").printStackTrace();
                    return;
                }

                if (!logFile.exists()) {
                    logFile.getParentFile().mkdirs();
                    try {
                        logFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!logFile.exists()) {
                        new IllegalStateException("Create file failed. " + logFile.getPath()).printStackTrace();
                        return;
                    }
                }

                try {
                    fileWriter = new FileWriter(logFile, true);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                logFileName = newLogFileName;
            }

            try {
                fileWriter.write(entry.time);
                fileWriter.write(" ");
                fileWriter.write(entry.level);
                fileWriter.write(" ");
                fileWriter.write(entry.tag);
                if (!TextUtils.isEmpty(entry.message)) {
                    fileWriter.write(" ");
                    fileWriter.write(entry.message);
                }
                if (entry.tr != null) {
                    fileWriter.write("\n");
                    entry.tr.printStackTrace(new PrintWriter(fileWriter));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileWriter.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (closed) {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fileWriter = null;
                }
            } else {
                entry.recycle();
            }
        }
    }

    private static class LogEntry {
        public String time;
        public String level;
        public String tag;
        public String message;
        public Throwable tr;

        private ObjectPool<LogEntry> logEntryObjectPool;

        public LogEntry(ObjectPool<LogEntry> logEntryObjectPool) {
            this.logEntryObjectPool = logEntryObjectPool;
        }

        public void set(String time, String level, String tag, String message, Throwable tr) {
            this.time = time;
            this.level = level;
            this.tag = tag;
            this.message = message;
            this.tr = tr;
        }

        public void recycle() {
            set(null, null, null, null, null);
            logEntryObjectPool.put(this);
        }
    }
}
