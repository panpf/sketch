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
    private ImageRegionDecoder decoder;
    private HandlerThread handlerThread;
    private DecodeHandler decodeHandler;

    private MainHandler mainHandler;
    private boolean initializing;

    public ImageRegionDecodeExecutor(Callback callback) {
        this.callback = callback;
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
                    mainHandler = new MainHandler(this);

                    mainHandler.postDelayDestroyThread();
                }
            }
        }
    }

    /**
     * 初始化解码器，初始化结果会通过Callback的onInitCompleted()或onInitFailed(Exception)方法回调
     */
    public void initDecoder(String imageUri) {
        initializing = true;
        synchronized (decoderLock) {
            if (decoder != null) {
                decoder.recycle();
                decoder = null;
            }
        }

        if (!TextUtils.isEmpty(imageUri)) {
            installHandlerThread();
            decodeHandler.postInit(imageUri);
        }
    }

    /**
     * 提交一个解码请求
     */
    public void submit(Rect srcRect, int inSampleSize, RectF visibleRect, float scale) {
        installHandlerThread();
        synchronized (this.decodeParams) {
            this.decodeParams.set(srcRect, inSampleSize, visibleRect, scale);
            decodeHandler.postDecode();
        }
    }

    /**
     * 取消所有的待办任务
     */
    public void clean() {
        if (decodeHandler != null) {
            decodeHandler.clean();
        }

        if (mainHandler != null) {
            mainHandler.clean();
        }
    }

    /**
     * 回收所有资源
     */
    public void recycle() {
        clean();
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
        decodeHandler.cancelDecode(force);
    }

    /**
     * 初始化完成回调
     */
    void initCompleted(ImageRegionDecoder decoder) {
        synchronized (decoderLock) {
            this.decoder = decoder;
        }

        initializing = false;
        callback.onInitCompleted();
    }

    /**
     * 初始化失败回调
     */
    void initFailed(Exception e) {
        initializing = false;
        callback.onInitFailed(e);
    }

    /**
     * 解码完成回调
     */
    void decodeCompleted(DecodeParams decodeParams) {
        callback.onDecodeCompleted(decodeParams.getSrcRect(),
                decodeParams.getInSampleSize(), decodeParams.getBitmap(),
                decodeParams.getVisibleRect(), decodeParams.getScale());
    }

    /**
     * 是否已经准备好可以使用？
     */
    public boolean isReady() {
        synchronized (decoderLock) {
            return decoder != null && !initializing;
        }
    }

    /**
     * 正在初始化？
     */
    public boolean isInitializing() {
        return initializing;
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

    public interface Callback {
        Context getContext();

        void onInitCompleted();

        void onInitFailed(Exception e);

        void onDecodeCompleted(Rect srcRect, int inSampleSize, Bitmap newBitmap, RectF visibleRect, float scale);
    }
}
