package me.xiaopan.sketchsample;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.xiaopan.sketch.SLogTracker;
import me.xiaopan.sketch.util.ObjectPool;
import me.xiaopan.sketch.util.SketchUtils;

public class SampleLogTracker implements SLogTracker {
    private Context context;
    private OutLog2SDCard outLog2SDCard;
    private ObjectPool<LogEntry> logEntryObjectPool;
    private SimpleDateFormat logTimeDateFormat;

    private boolean closed;

    public SampleLogTracker(Context context) {
        this.context = context.getApplicationContext();
    }

    private void install() {
        if (outLog2SDCard == null) {
            synchronized (this) {
                if (outLog2SDCard == null) {
                    outLog2SDCard = new OutLog2SDCard(context);
                }
            }
        }

        if (logEntryObjectPool == null) {
            synchronized (this) {
                if (logEntryObjectPool == null) {
                    logEntryObjectPool = new ObjectPool<LogEntry>(new ObjectPool.ObjectFactory<LogEntry>() {
                        @Override
                        public LogEntry newObject() {
                            return new LogEntry(logEntryObjectPool);
                        }
                    });
                }
            }
        }

        if (logTimeDateFormat == null) {
            synchronized (this) {
                if (logTimeDateFormat == null) {

                    logTimeDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US);
                }
            }
        }
    }

    public void close() {
        closed = true;

        if (outLog2SDCard != null) {
            outLog2SDCard.close();
            outLog2SDCard = null;
        }

        if (logEntryObjectPool != null) {
            logEntryObjectPool.clear();
            logEntryObjectPool = null;
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void v(String tag, String msg) {
        if (closed) {
            return;
        }

        install();
        String time = logTimeDateFormat.format(new Date());

        LogEntry logEntry = logEntryObjectPool.get();
        logEntry.set(time, "V", tag, msg);
        outLog2SDCard.outLog(logEntry);
    }

    @Override
    public void i(String tag, String msg) {
        if (closed) {
            return;
        }

        install();
        String time = logTimeDateFormat.format(new Date());

        LogEntry logEntry = logEntryObjectPool.get();
        logEntry.set(time, "I", tag, msg);
        outLog2SDCard.outLog(logEntry);
    }

    @Override
    public void d(String tag, String msg) {
        if (closed) {
            return;
        }

        install();
        String time = logTimeDateFormat.format(new Date());

        LogEntry logEntry = logEntryObjectPool.get();
        logEntry.set(time, "D", tag, msg);
        outLog2SDCard.outLog(logEntry);
    }

    @Override
    public void w(String tag, String msg) {
        if (closed) {
            return;
        }

        install();
        String time = logTimeDateFormat.format(new Date());

        LogEntry logEntry = logEntryObjectPool.get();
        logEntry.set(time, "W", tag, msg);
        outLog2SDCard.outLog(logEntry);
    }

    @Override
    public void e(String tag, String msg) {
        if (closed) {
            return;
        }

        install();
        String time = logTimeDateFormat.format(new Date());

        LogEntry logEntry = logEntryObjectPool.get();
        logEntry.set(time, "E", tag, msg);
        outLog2SDCard.outLog(logEntry);
    }

    private static class OutLog2SDCard {
        private Context context;

        private Handler handler;
        private HandlerThread handlerThread;
        private SimpleDateFormat fileNameDateFormat;

        private String logFileName;
        private FileWriter fileWriter;

        private boolean closed;

        public OutLog2SDCard(Context context) {
            this.context = context.getApplicationContext();
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
        }

        public void outLog(LogEntry entry) {
            if (closed) {
                return;
            }

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

            handler.obtainMessage(0, entry).sendToTarget();
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
                fileWriter.write(" ");
                fileWriter.write(entry.message);
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

        private ObjectPool<LogEntry> logEntryObjectPool;

        public LogEntry(ObjectPool<LogEntry> logEntryObjectPool) {
            this.logEntryObjectPool = logEntryObjectPool;
        }

        public void set(String time, String level, String tag, String message) {
            this.time = time;
            this.level = level;
            this.tag = tag;
            this.message = message;
        }

        public void recycle() {
            set(null, null, null, null);
            logEntryObjectPool.put(this);
        }
    }
}
