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
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.util.KeyCounter;

/**
 * 碎片解码执行器，负责初始化解码器以及管理解码线程
 */
// TODO: 16/9/16 将解码器和执行器分开
class TileExecutor {
    private static final String NAME = "TileDecodeExecutor";
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger();

    private final Object handlerThreadLock = new Object();
    private final Object decoderLock = new Object();

    Callback callback;

    private HandlerThread handlerThread;

    private boolean running;
    private boolean initializing;
    private InitHandler initHandler;
    MainHandler mainHandler;
    private DecodeHandler decodeHandler;
    TileDecoder decoder;
    KeyCounter initKeyCounter;
    private String imageUri;

    public TileExecutor(Callback callback) {
        this.callback = callback;
        this.initKeyCounter = new KeyCounter();
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

                    if (Sketch.isDebugMode()) {
                        Log.i(Sketch.TAG, NAME + ". image region decode thread " + handlerThread.getName() + " started. " + imageUri);
                    }

                    decodeHandler = new DecodeHandler(handlerThread.getLooper(), this);
                    initHandler = new InitHandler(handlerThread.getLooper(), this);

                    mainHandler.postDelayRecycleDecodeThread();
                }
            }
        }
    }

    /**
     * 初始化解码器，初始化结果会通过Callback的onInitCompleted()或onInitFailed(Exception)方法回调
     */
    public void initDecoder(String imageUri) {
        cleanAll("initDecoder");

        synchronized (decoderLock) {
            if (decoder != null) {
                decoder.recycle();
                decoder = null;
            }
        }

        this.imageUri = imageUri;

        if (!TextUtils.isEmpty(imageUri)) {
            running = true;
            initializing = true;
            installHandlerThread();
            initHandler.postInit(imageUri, initKeyCounter.getKey());
        } else {
            running = false;
            initializing = false;
        }
    }

    /**
     * 提交一个解码请求
     */
    public void submit(int key, Tile tile) {
        if (!running) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". stop running. submit. " + imageUri);
            }
            return;
        }

        installHandlerThread();
        decodeHandler.postDecode(key, tile);
    }

    /**
     * 取消所有的解码任务
     */
    public void cleanDecode(String why) {
        if (decodeHandler != null) {
            decodeHandler.clean(why, imageUri);
        }
    }

    /**
     * 取消所有的待办任务
     */
    public void cleanAll(String why){
        initKeyCounter.refresh();
        if (initHandler != null) {
            initHandler.clean(why, imageUri);
        }

        if (decodeHandler != null) {
            decodeHandler.clean(why, imageUri);
        }

        if (mainHandler != null) {
            mainHandler.cleanAll(why, imageUri);
        }
    }

    /**
     * 回收所有资源
     */
    public void recycle(String why) {
        running = false;
        cleanAll(why);
        recycleDecodeThread();
    }

    public void recycleDecodeThread() {
        if (initHandler != null) {
            initHandler.clean("recycleDecodeThread", imageUri);
        }

        if (decodeHandler != null) {
            decodeHandler.clean("recycleDecodeThread", imageUri);
        }

        synchronized (handlerThreadLock) {
            if (handlerThread != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    handlerThread.quitSafely();
                } else {
                    handlerThread.quit();
                }

                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". image region decode thread " + handlerThread.getName() + " quit. " + imageUri);
                }

                handlerThread = null;
            }
        }
    }

    void initCompleted(TileDecoder decoder) {
        if (running) {
            synchronized (decoderLock) {
                TileExecutor.this.decoder = decoder;
            }
            initializing = false;
        } else {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". stop running. initCompleted. " + imageUri);
            }
            decoder.recycle();
        }

        callback.onInitCompleted();
    }

    void initFailed(Exception e) {
        if (running) {
            initializing = false;
        } else {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". stop running. initFailed. " + imageUri);
            }
        }

        callback.onInitFailed(e);
    }

    void decodeCompleted(Tile tile, Bitmap bitmap) {
        callback.onDecodeCompleted(tile, bitmap);
    }

    void decodeFailed(Tile tile, DecodeHandler.DecodeFailedException exception) {
        callback.onDecodeFailed(tile, exception);
    }

    /**
     * 是否已经准备好可以使用？
     */
    public boolean isReady() {
        synchronized (decoderLock) {
            return running && decoder != null && !initializing;
        }
    }

    /**
     * 正在初始化？
     */
    public boolean isInitializing() {
        return running && initializing;
    }

    public interface Callback {
        Context getContext();

        void onInitCompleted();

        void onInitFailed(Exception e);

        void onDecodeCompleted(Tile tile, Bitmap bitmap);

        void onDecodeFailed(Tile tile, DecodeHandler.DecodeFailedException exception);
    }
}
