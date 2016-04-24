package me.xiaopan.sketchsample.widget;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DecodeExector {
    private ThreadPoolExecutor executor;
    private LargeImageView largeImageView;
    private Handler handler;

    public DecodeExector(LargeImageView largeImageView) {
        this.largeImageView = largeImageView;
        this.handler = new Handler(Looper.getMainLooper(), new MessageCallback());
        this.executor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public void loadThumbnail(){

    }

    private class MessageCallback implements Handler.Callback{

        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    }

    private static class LoadThumbnailTask implements Runnable{
//        private static

        @Override
        public void run() {

        }
    }
}
