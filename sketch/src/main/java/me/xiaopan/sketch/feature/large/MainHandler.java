/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.sketch.feature.large;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.util.KeyCounter;

/**
 * 运行在主线程，负责将执行器的结果发送到主线程
 */
class MainHandler extends Handler {
    private static final String NAME = "MainHandler";

    private static final int WHAT_RECYCLE_DECODE_THREAD = 2001;
    private static final int WHAT_INIT_COMPLETED = 2002;
    private static final int WHAT_INIT_FAILED = 2003;
    private static final int WHAT_DECODE_COMPLETED = 2004;
    private static final int WHAT_DECODE_FAILED = 2005;

    private BitmapPool bitmapPool;
    private WeakReference<TileExecutor> executorReference;

    public MainHandler(Looper looper, TileExecutor executor) {
        super(looper);
        executorReference = new WeakReference<>(executor);
        bitmapPool = Sketch.with(executor.callback.getContext()).getConfiguration().getBitmapPool();
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_RECYCLE_DECODE_THREAD:
                recycleDecodeThread();
                break;
            case WHAT_INIT_COMPLETED:
                InitResult initResult = (InitResult) msg.obj;
                initCompleted(initResult.imageRegionDecoder, initResult.imageUrl, msg.arg1, initResult.keyCounter);
                break;
            case WHAT_INIT_FAILED:
                InitErrorResult initErrorResult = (InitErrorResult) msg.obj;
                initError(initErrorResult.exception, initErrorResult.imageUrl, msg.arg1, initErrorResult.keyCounter);
                break;
            case WHAT_DECODE_COMPLETED:
                DecodeResult decodeResult = (DecodeResult) msg.obj;
                decodeCompleted(msg.arg1, decodeResult.tile, decodeResult.bitmap, decodeResult.useTime);
                break;
            case WHAT_DECODE_FAILED:
                DecodeErrorResult decodeErrorResult = (DecodeErrorResult) msg.obj;
                decodeError(msg.arg1, decodeErrorResult.tile, decodeErrorResult.exception);
                break;
        }
    }


    /**
     * 延迟三十秒停止解码线程
     */
    public void postDelayRecycleDecodeThread() {
        cancelDelayDestroyThread();

        Message destroyMessage = obtainMessage(MainHandler.WHAT_RECYCLE_DECODE_THREAD);
        sendMessageDelayed(destroyMessage, 30 * 1000);
    }

    private void recycleDecodeThread() {
        TileExecutor executor = executorReference.get();
        if (executor != null) {
            executor.recycleDecodeThread();
        }
    }

    /**
     * 取消停止解码线程的延迟任务
     */
    public void cancelDelayDestroyThread() {
        removeMessages(MainHandler.WHAT_RECYCLE_DECODE_THREAD);
    }


    public void postInitCompleted(ImageRegionDecoder decoder, String imageUri, int initKey, KeyCounter keyCounter) {
        Message message = obtainMessage(MainHandler.WHAT_INIT_COMPLETED);
        message.arg1 = initKey;
        message.obj = new InitResult(decoder, imageUri, keyCounter);
        message.sendToTarget();
    }

    public void postInitError(Exception e, String imageUri, int key, KeyCounter keyCounter) {
        Message message = obtainMessage(MainHandler.WHAT_INIT_FAILED);
        message.arg1 = key;
        message.obj = new InitErrorResult(e, imageUri, keyCounter);
        message.sendToTarget();
    }

    public void postDecodeCompleted(int key, Tile tile, Bitmap bitmap, int useTime) {
        Message message = obtainMessage(MainHandler.WHAT_DECODE_COMPLETED);
        message.arg1 = key;
        message.obj = new DecodeResult(bitmap, tile, useTime);
        message.sendToTarget();
    }

    public void postDecodeError(int key, Tile tile, DecodeHandler.DecodeErrorException exception) {
        Message message = obtainMessage(MainHandler.WHAT_DECODE_FAILED);
        message.arg1 = key;
        message.obj = new DecodeErrorResult(tile, exception);
        message.sendToTarget();
    }




    private void initCompleted(ImageRegionDecoder decoder, String imageUri, int key, KeyCounter keyCounter) {
        TileExecutor executor = executorReference.get();
        if (executor == null) {
            if (SLogType.LARGE.isEnabled()) {
                SLog.w(SLogType.LARGE, NAME, "weak reference break. initCompleted. key: %d, imageUri: %s", key, decoder.getImageUri());
            }
            decoder.recycle();
            return;
        }

        int newKey = keyCounter.getKey();
        if (key != newKey) {
            if (SLogType.LARGE.isEnabled()) {
                SLog.w(SLogType.LARGE, NAME, "init key expired. initCompleted. key: %d. newKey: %d, imageUri: %s", key, newKey, decoder.getImageUri());
            }
            decoder.recycle();
            return;
        }

        executor.callback.onInitCompleted(imageUri, decoder);
    }

    private void initError(Exception exception, String imageUri, int key, KeyCounter keyCounter) {
        TileExecutor executor = executorReference.get();
        if (executor == null) {
            if (SLogType.LARGE.isEnabled()) {
                SLog.w(SLogType.LARGE, NAME, "weak reference break. initError. key: %d, imageUri: %s", key, imageUri);
            }
            return;
        }

        int newKey = keyCounter.getKey();
        if (key != newKey) {
            if (SLogType.LARGE.isEnabled()) {
                SLog.w(SLogType.LARGE, NAME, "key expire. initError. key: %d. newKey: %d, imageUri: %s", key, newKey, imageUri);
            }
            return;
        }

        executor.callback.onInitError(imageUri, exception);
    }

    private void decodeCompleted(int key, Tile tile, Bitmap bitmap, int useTime) {
        TileExecutor executor = executorReference.get();
        if (executor == null) {
            if (SLogType.LARGE.isEnabled()) {
                SLog.w(SLogType.LARGE, NAME, "weak reference break. decodeCompleted. key: %d, tile=%s", key, tile.getInfo());
            }
            BitmapPoolUtils.freeBitmapToPoolForRegionDecoder(bitmap, bitmapPool);
            return;
        }

        if (!tile.isExpired(key)) {
            executor.callback.onDecodeCompleted(tile, bitmap, useTime);
        } else {
            BitmapPoolUtils.freeBitmapToPoolForRegionDecoder(bitmap, bitmapPool);
            executor.callback.onDecodeError(tile,
                    new DecodeHandler.DecodeErrorException(DecodeHandler.DecodeErrorException.CAUSE_CALLBACK_KEY_EXPIRED));
        }
    }

    private void decodeError(int key, Tile tile, DecodeHandler.DecodeErrorException exception) {
        TileExecutor executor = executorReference.get();
        if (executor == null) {
            if (SLogType.LARGE.isEnabled()) {
                SLog.w(SLogType.LARGE, NAME, "weak reference break. decodeError. key: %d, tile=%s", key, tile.getInfo());
            }
            return;
        }

        executor.callback.onDecodeError(tile, exception);
    }

    private static final class DecodeResult {
        public Tile tile;
        public Bitmap bitmap;
        public int useTime;

        public DecodeResult(Bitmap bitmap, Tile tile, int useTime) {
            this.bitmap = bitmap;
            this.tile = tile;
            this.useTime = useTime;
        }
    }

    private static final class DecodeErrorResult {
        public Tile tile;
        public DecodeHandler.DecodeErrorException exception;

        public DecodeErrorResult(Tile tile, DecodeHandler.DecodeErrorException exception) {
            this.tile = tile;
            this.exception = exception;
        }
    }

    private static final class InitResult{
        public String imageUrl;
        public ImageRegionDecoder imageRegionDecoder;
        public KeyCounter keyCounter;

        public InitResult(ImageRegionDecoder imageRegionDecoder, String imageUrl, KeyCounter keyCounter) {
            this.imageRegionDecoder = imageRegionDecoder;
            this.imageUrl = imageUrl;
            this.keyCounter = keyCounter;
        }
    }

    private static final class InitErrorResult {
        public String imageUrl;
        public Exception exception;
        public KeyCounter keyCounter;

        public InitErrorResult(Exception exception, String imageUrl, KeyCounter keyCounter) {
            this.exception = exception;
            this.imageUrl = imageUrl;
            this.keyCounter = keyCounter;
        }
    }
}
