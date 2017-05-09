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

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.atomic.AtomicInteger;

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.util.KeyCounter;

/**
 * 碎片解码执行器，负责初始化解码器以及管理解码线程
 */
class TileExecutor {
    private static final String NAME = "TileExecutor";
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger();

    private final Object handlerThreadLock = new Object();

    Callback callback;

    private HandlerThread handlerThread;

    private InitHandler initHandler;
    MainHandler mainHandler;
    private DecodeHandler decodeHandler;

    public TileExecutor(Callback callback) {
        this.callback = callback;
        this.mainHandler = new MainHandler(Looper.getMainLooper(), this);
    }

    /**
     * 安装解码线程
     */
    private void installHandlerThread() {
        if (handlerThread == null) {
            synchronized (handlerThreadLock) {
                if (handlerThread == null) {
                    if (THREAD_NUMBER.get() >= Integer.MAX_VALUE) {
                        THREAD_NUMBER.set(0);
                    }
                    handlerThread = new HandlerThread("ImageRegionDecodeThread" + THREAD_NUMBER.addAndGet(1));
                    handlerThread.start();

                    SLog.i(SLogType.LARGE, NAME, "image region decode thread %s started", handlerThread.getName());

                    decodeHandler = new DecodeHandler(handlerThread.getLooper(), this);
                    initHandler = new InitHandler(handlerThread.getLooper(), this);

                    mainHandler.postDelayRecycleDecodeThread();
                }
            }
        }
    }

    /**
     * 初始化解码器，初始化结果会通过Callback的onInitCompleted()或onInitError(Exception)方法回调
     */
    public void submitInit(String imageUri, KeyCounter keyCounter, boolean correctImageOrientation) {
        installHandlerThread();
        initHandler.postInit(imageUri, correctImageOrientation, keyCounter.getKey(), keyCounter);
    }

    /**
     * 提交一个解码请求
     */
    public void submitDecodeTile(int key, Tile tile) {
        installHandlerThread();
        decodeHandler.postDecode(key, tile);
    }

    /**
     * 取消所有的解码任务
     */
    public void cleanDecode(String why) {
        if (decodeHandler != null) {
            decodeHandler.clean(why);
        }
    }

    /**
     * 回收所有资源
     */
    public void recycle(String why) {
        if (initHandler != null) {
            initHandler.clean(why);
        }

        if (decodeHandler != null) {
            decodeHandler.clean(why);
        }

        recycleDecodeThread();
    }

    void recycleDecodeThread() {
        if (initHandler != null) {
            initHandler.clean("recycleDecodeThread");
        }

        if (decodeHandler != null) {
            decodeHandler.clean("recycleDecodeThread");
        }

        synchronized (handlerThreadLock) {
            if (handlerThread != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    handlerThread.quitSafely();
                } else {
                    handlerThread.quit();
                }

                SLog.w(SLogType.LARGE, NAME, "image region decode thread %s quit", handlerThread.getName());

                handlerThread = null;
            }
        }
    }

    public interface Callback {
        Context getContext();

        void onInitCompleted(String imageUri, ImageRegionDecoder decoder);

        void onInitError(String imageUri, Exception e);

        void onDecodeCompleted(Tile tile, Bitmap bitmap, int useTime);

        void onDecodeError(Tile tile, DecodeHandler.DecodeErrorException exception);
    }
}
