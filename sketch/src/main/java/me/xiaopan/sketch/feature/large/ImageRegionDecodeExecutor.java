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
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import me.xiaopan.sketch.Sketch;

class ImageRegionDecodeExecutor {
    private static final String NAME = "ImageRegionDecodeExecutor";
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger();

    private final Object handlerThreadLock = new Object();
    private final Object decoderLock = new Object();
    private final DecodeParams decodeParams = new DecodeParams();

    private Callback callback;

    private HandlerThread handlerThread;

    private boolean running;
    private boolean initializing;
    private InitHandler initHandler;
    private MainHandler mainHandler;
    private DecodeHandler decodeHandler;
    private ImageRegionDecoder decoder;
    private KeyNumber initKeyNumber;
    private KeyNumber decodeKeyNumber;

    public ImageRegionDecodeExecutor(Callback callback) {
        this.callback = callback;
        this.initKeyNumber = new KeyNumber();
        this.decodeKeyNumber = new KeyNumber();
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
                        Log.i(Sketch.TAG, NAME + ". image region decode thread " + handlerThread.getName() + " started");
                    }

                    decodeHandler = new DecodeHandler(handlerThread.getLooper(), this);
                    initHandler = new InitHandler(handlerThread.getLooper(), this);

                    mainHandler.postDelayRecycleDecodeThread();
                }
            }
        }
    }

    /**
     * 取消所有的待办任务
     */
    public void clean() {
        initKeyNumber.refresh();
        decodeKeyNumber.refresh();

        if (initHandler != null) {
            initHandler.clean();
        }

        if (decodeHandler != null) {
            decodeHandler.clean();
        }

        if (mainHandler != null) {
            mainHandler.clean();
        }
    }

    /**
     * 初始化解码器，初始化结果会通过Callback的onInitCompleted()或onInitFailed(Exception)方法回调
     */
    public void initDecoder(String imageUri) {
        clean();

        synchronized (decoderLock) {
            if (decoder != null) {
                decoder.recycle();
                decoder = null;
            }
        }

        if (!TextUtils.isEmpty(imageUri)) {
            running = true;
            initializing = true;
            installHandlerThread();
            initHandler.postInit(imageUri, initKeyNumber.getKey());
        } else {
            running = false;
            initializing = false;
        }
    }

    /**
     * 提交一个解码请求
     */
    public void submit(Rect srcRect, RectF drawRectF, int inSampleSize, RectF visibleRect, float scale) {
        if (!running) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". stop running. submit");
            }
            return;
        }

        installHandlerThread();
        synchronized (this.decodeParams) {
            this.decodeParams.set(srcRect, drawRectF, inSampleSize, visibleRect, scale);
            decodeHandler.postDecode(decodeKeyNumber.getKey());
        }
    }

    /**
     * 回收所有资源
     */
    public void recycle() {
        running = false;
        clean();
        recycleDecodeThread();
    }

    public void recycleDecodeThread(){
        if (initHandler != null) {
            initHandler.clean();
        }

        if (decodeHandler != null) {
            decodeHandler.clean();
        }

        synchronized (handlerThreadLock) {
            if (handlerThread != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    handlerThread.quitSafely();
                } else {
                    handlerThread.quit();
                }

                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". image region decode thread " + handlerThread.getName() + " quit");
                }

                handlerThread = null;
            }
        }
    }

    /**
     * 取消解码
     *
     * @param force 是否强制取消
     */
    public void cancelDecode(boolean force) {
        if (force) {
            decodeKeyNumber.refresh();
        }
        decodeHandler.clean();
    }

    /**
     * 是否已经准备好可以使用？
     */
    public boolean isReady() {
        synchronized (decoderLock) {
            return running && decoder != null && !initializing;
        }
    }

    void initCompleted(ImageRegionDecoder decoder) {
        if (!running) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". stop running. initCompleted");
            }
            decoder.recycle();
            return;
        }

        synchronized (decoderLock) {
            ImageRegionDecodeExecutor.this.decoder = decoder;
        }

        initializing = false;
        callback.onInitCompleted();
    }

    void initFailed(Exception e) {
        if (!running) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". stop running. initFailed");
            }
            return;
        }

        initializing = false;
        callback.onInitFailed(e);
    }

    void decodeCompleted(DecodeParams decodeParams) {
        if (!running) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". stop running. decodeCompleted");
            }
            Bitmap bitmap = decodeParams.getBitmap();
            if (bitmap != null) {
                bitmap.recycle();
            }
            return;
        }

        callback.onDecodeCompleted(decodeParams.getSrcRect(),
                decodeParams.getDrawRectF(),
                decodeParams.getInSampleSize(),
                decodeParams.getVisibleRect(),
                decodeParams.getScale(),
                decodeParams.getBitmap());
    }

    /**
     * 正在初始化？
     */
    public boolean isInitializing() {
        return running && initializing;
    }

    Context getContext() {
        return callback.getContext();
    }

    ImageRegionDecoder getDecoder() {
        return decoder;
    }

    DecodeParams getDecodeParams() {
        return decodeParams;
    }

    MainHandler getMainHandler() {
        return mainHandler;
    }

    public int getDecodeKey() {
        return decodeKeyNumber.getKey();
    }

    public int getInitKey() {
        return initKeyNumber.getKey();
    }

    public interface Callback {
        Context getContext();

        void onInitCompleted();

        void onInitFailed(Exception e);

        void onDecodeCompleted(Rect srcRect, RectF drawRectF, int inSampleSize, RectF visibleRect, float scale, Bitmap newBitmap);
    }
}
