package me.xiaopan.sketchsample.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

/**
 * 文件扫描器
 */
public class FileScanner {
    private int threadCount = 3;
    private boolean running;
    private boolean canceled;
    private CallbackHandler callbackHandler;

    private FileChecker fileChecker;
    private ScanListener scanListener;
    private DirFilter dirFilter;

    public FileScanner(FileChecker fileChecker, ScanListener scanListener) {
        this.fileChecker = fileChecker;
        this.scanListener = scanListener;
    }

    public void setDirFilter(DirFilter dirFilter) {
        this.dirFilter = dirFilter;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public boolean execute(File[] dirs) {
        if (running || dirs == null || dirs.length == 0 || fileChecker == null || scanListener == null) {
            return false;
        }

        this.running = true;
        this.canceled = false;
        if (callbackHandler == null) {
            callbackHandler = new CallbackHandler(Looper.getMainLooper());
        }

        callbackHandler.callbackStarted();
        new Thread(new MultiThreadScanTask(dirs, this, threadCount)).start();
        return true;
    }

    public boolean execute(String[] dirPaths) {
        if (dirPaths == null || dirPaths.length == 0) {
            return false;
        }

        List<File> dirList = new LinkedList<File>();
        for (String dirPath : dirPaths) {
            dirList.add(new File(dirPath));
        }

        return execute(dirList.toArray(new File[dirList.size()]));
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        canceled = true;
    }

    public boolean isRunning() {
        return running;
    }

    public interface DirFilter{
        boolean accept(File dir);
    }

    public interface ScanListener {
        void onStarted();

        void onFindFile(FileItem fileItem);

        void onUpdateProgress(int totalLength, int completedLength);

        void onCompleted();

        void onCanceled();

        void onScanDir(File dir);
    }

    public interface FileChecker {
        /**
         * 检查这个文件是不是你要的
         *
         * @param pathname 这个文件
         * @return null: 不要这个文件；否则请返回一个实现了FileItem接口的一个对象，你可以在此解析你需要的数据并封装在这个对象中，ScanListener会将这个对象再回调给你
         */
        FileItem accept(File pathname);

        void onFinished();
    }

    public interface FileItem {
        String getFilePath();
        long getFileLength();
    }

    private static class MultiThreadScanTask implements Runnable {
        private File[] dirs;
        private FileScanner fileScanner;

        private int childThreadCount;
        private int mainDirTotalLength;
        private long lastCallScanDirTime;
        private final Queue<File> mainDirQueue = new LinkedList<File>();;
        private final Queue<File> currentDirQueue = new LinkedList<File>();;
        private CountDownLatch countDownLatch;

        private FileChecker fileChecker;
        private DirFilter dirFilter;
        private CallbackHandler callbackHandler;

        public MultiThreadScanTask(File[] dirs, FileScanner fileScanner, int childThreadCount) {
            this.dirs = dirs;
            this.fileScanner = fileScanner;
            this.childThreadCount = childThreadCount;

            countDownLatch = new CountDownLatch(childThreadCount);

            fileChecker = fileScanner.fileChecker;
            dirFilter = fileScanner.dirFilter;
            callbackHandler = fileScanner.callbackHandler;
        }

        @Override
        public void run() {
            // 分主目录和当前目录的原因是要根据主目录的数量来更新进度
            for (File dir : dirs) {
                if (dir == null || !dir.exists()) {
                    continue;
                }

                boolean isDirectory = dir.isDirectory();
                if (isDirectory) {
                    callbackScanDir(dir);
                }

                FileItem fileItem = fileChecker.accept(dir);
                if (fileItem != null) {
                    callbackHandler.callbackFindFile(fileItem);
                }

                if (isDirectory) {
                    File[] childFiles = dir.listFiles();
                    if (childFiles != null && childFiles.length > 0) {
                        for (File childFile : childFiles) {
                            if (childFile != null && childFile.exists()) {
                                fileItem = fileChecker.accept(childFile);
                                if (fileItem != null) {
                                    callbackHandler.callbackFindFile(fileItem);
                                }

                                if (childFile.isDirectory() && (dirFilter == null || dirFilter.accept(childFile))) {
                                    mainDirQueue.add(childFile);
                                }
                            }
                        }
                    }
                }
            }
            mainDirTotalLength = mainDirQueue.size();

            for (int w = 0; w < childThreadCount; w++) {
                new Thread(new ChildScanTask(this)).start();
            }

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                fileScanner.cancel();
            }

            fileChecker.onFinished();

            fileScanner.running = false;
            if (fileScanner.isCanceled()) {
                callbackHandler.callbackCanceled();
            } else {
                callbackHandler.callbackCompleted();
            }
        }

        private void callbackScanDir(File currentDir) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastCallScanDirTime) >= 1000) {
                callbackHandler.callbackScanDir(currentDir);
                lastCallScanDirTime = currentTime;
            }
        }

        public File getNextDir() {
            synchronized (currentDirQueue){
                // 从主目录队列中取出一个目录放到当前目录队列中，如果两个队列都空的就结束
                if (currentDirQueue.isEmpty()) {
                    if (mainDirQueue.isEmpty()) {
                        return null;
                    }

                    currentDirQueue.add(mainDirQueue.poll());

                    // 用主目录的数量来计算进度
                    callbackHandler.callbackUpdateProgress(mainDirTotalLength, mainDirTotalLength - mainDirQueue.size());
                }

                // 严谨的过虑一下null或不存在的情况
                File currentDir = currentDirQueue.poll();
                callbackScanDir(currentDir);
                return currentDir;
            }
        }

        private synchronized void childTaskFinished() {
            countDownLatch.countDown();
        }

        private synchronized void childTaskFindDir(File childDir) {
            synchronized (currentDirQueue){
                currentDirQueue.add(childDir);
            }
        }

        private static class ChildScanTask implements Runnable {
            private MultiThreadScanTask multiThreadScanTask;

            private FileChecker fileChecker;
            private DirFilter dirFilter;
            private CallbackHandler callbackHandler;

            public ChildScanTask(MultiThreadScanTask multiThreadScanTask) {
                this.multiThreadScanTask = multiThreadScanTask;

                fileChecker = multiThreadScanTask.fileScanner.fileChecker;
                dirFilter = multiThreadScanTask.fileScanner.dirFilter;
                callbackHandler = multiThreadScanTask.fileScanner.callbackHandler;
            }

            @Override
            public void run() {
                // 子任务的处理逻辑就是，不停的获取下一个要扫描的文件夹 没有文件夹的话就结束，扫描到子文件夹的话就再加到队列中
                File currentDir;
                while (!multiThreadScanTask.fileScanner.canceled) {
                    currentDir = multiThreadScanTask.getNextDir();
                    if (currentDir == null) {
                        break;
                    }

                    if (!currentDir.exists()) {
                        continue;
                    }

                    File[] childFiles = currentDir.listFiles();
                    if (childFiles == null || childFiles.length == 0) {
                        continue;
                    }

                    for (File childFile : childFiles) {
                        if (multiThreadScanTask.fileScanner.canceled) {
                            break;
                        }

                        FileItem fileItem = fileChecker.accept(childFile);
                        if (fileItem != null) {
                            callbackHandler.callbackFindFile(fileItem);
                        }

                        if (childFile.isDirectory() && (dirFilter == null || dirFilter.accept(childFile))) {
                            multiThreadScanTask.childTaskFindDir(childFile);
                        }
                    }
                }

                multiThreadScanTask.childTaskFinished();
            }
        }
    }

    private class CallbackHandler extends Handler {
        private static final int WHAT_CALLBACK_STARTED = 11101;
        private static final int WHAT_CALLBACK_COMPLETED = 11102;
        private static final int WHAT_CALLBACK_CANCELED = 11103;
        private static final int WHAT_CALLBACK_UPDATE_PROGRESS = 11104;
        private static final int WHAT_CALLBACK_FIND_FILE = 11105;
        private static final int WHAT_CALLBACK_SCAN_DIR = 11106;

        public CallbackHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_CALLBACK_STARTED:
                    if (!canceled) {
                        scanListener.onStarted();
                    }
                    break;
                case WHAT_CALLBACK_COMPLETED:
                    if (!canceled) {
                        scanListener.onCompleted();
                    }
                    break;
                case WHAT_CALLBACK_CANCELED:
                    scanListener.onCanceled();
                    break;
                case WHAT_CALLBACK_UPDATE_PROGRESS:
                    if (!canceled) {
                        scanListener.onUpdateProgress(msg.arg1, msg.arg2);
                    }
                    break;
                case WHAT_CALLBACK_FIND_FILE:
                    if (!canceled) {
                        scanListener.onFindFile((FileItem) msg.obj);
                    }
                    break;
                case WHAT_CALLBACK_SCAN_DIR:
                    if (!canceled) {
                        scanListener.onScanDir((File) msg.obj);
                    }
                    break;
            }
        }

        public void callbackStarted() {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                scanListener.onStarted();
            } else {
                obtainMessage(WHAT_CALLBACK_STARTED).sendToTarget();
            }
        }

        public void callbackScanDir(File file) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                scanListener.onScanDir(file);
            } else {
                obtainMessage(WHAT_CALLBACK_SCAN_DIR, file).sendToTarget();
            }
        }

        public void callbackFindFile(FileItem fileItem) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                scanListener.onFindFile(fileItem);
            } else {
                obtainMessage(WHAT_CALLBACK_FIND_FILE, fileItem).sendToTarget();
            }
        }

        public void callbackUpdateProgress(int totalLength, int completedLength) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                scanListener.onUpdateProgress(totalLength, completedLength);
            } else {
                obtainMessage(WHAT_CALLBACK_UPDATE_PROGRESS, totalLength, completedLength).sendToTarget();
            }
        }

        public void callbackCompleted() {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                scanListener.onCompleted();
            } else {
                obtainMessage(WHAT_CALLBACK_COMPLETED).sendToTarget();
            }
        }

        public void callbackCanceled() {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                scanListener.onCanceled();
            } else {
                obtainMessage(WHAT_CALLBACK_CANCELED).sendToTarget();
            }
        }
    }
}
