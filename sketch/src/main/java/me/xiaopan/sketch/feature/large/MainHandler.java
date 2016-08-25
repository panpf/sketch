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

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.Sketch;

class MainHandler extends Handler {
    private static final String NAME = "MainHandler";

    private static final int WHAT_RECYCLE_DECODE_THREAD = 2001;
    private static final int WHAT_INIT_COMPLETED = 2002;
    private static final int WHAT_INIT_FAILED = 2003;
    private static final int WHAT_DECODE_COMPLETED = 2004;

    private WeakReference<ImageRegionDecodeExecutor> reference;

    public MainHandler(Looper looper, ImageRegionDecodeExecutor decodeExecutor) {
        super(looper);
        reference = new WeakReference<ImageRegionDecodeExecutor>(decodeExecutor);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_RECYCLE_DECODE_THREAD:
                recycleDecodeThread();
                break;
            case WHAT_INIT_COMPLETED:
                initCompleted((ImageRegionDecoder) msg.obj, msg.arg1);
                break;
            case WHAT_INIT_FAILED:
                initFailed((InitHandler.InitFailedException) msg.obj, msg.arg1);
                break;
            case WHAT_DECODE_COMPLETED:
                decodeCompleted((DecodeParams) msg.obj, msg.arg1);
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
        ImageRegionDecodeExecutor decodeExecutor = reference.get();
        if (decodeExecutor != null) {
            decodeExecutor.recycleDecodeThread();
        }
    }

    /**
     * 取消停止解码线程的延迟任务
     */
    public void cancelDelayDestroyThread() {
        removeMessages(MainHandler.WHAT_RECYCLE_DECODE_THREAD);
    }


    public void postInitCompleted(ImageRegionDecoder decoder, int initKey) {
        Message message = obtainMessage(MainHandler.WHAT_INIT_COMPLETED, decoder);
        message.arg1 = initKey;
        message.sendToTarget();
    }

    private void initCompleted(ImageRegionDecoder decoder, int initKey) {
        ImageRegionDecodeExecutor decodeExecutor = reference.get();
        if (decodeExecutor == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". weak reference break" +
                        ". initCompleted" +
                        ". initKey: " + initKey +
                        ", imageUri: " + decoder.getImageUri());
            }
            decoder.recycle();
            return;
        }

        int newestInitKey = decodeExecutor.getInitKey();
        if (initKey != newestInitKey) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". init key expired" +
                        ". initCompleted" +
                        ". initKey: " + initKey +
                        ". newestInitKey: " + newestInitKey +
                        ", imageUri: " + decoder.getImageUri());
            }
            decoder.recycle();
            return;
        }

        decodeExecutor.initCompleted(decoder);
    }


    public void postInitFailed(InitHandler.InitFailedException e, int initKey) {
        Message message = obtainMessage(MainHandler.WHAT_INIT_FAILED, e);
        message.arg1 = initKey;
        message.sendToTarget();
    }

    private void initFailed(InitHandler.InitFailedException e, int initKey) {
        ImageRegionDecodeExecutor decodeExecutor = reference.get();
        if (decodeExecutor == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". weak reference break" +
                        ". initFailed" +
                        ". initKey: " + initKey +
                        ", imageUri: " + e.getImageUri());
            }
            return;
        }

        int newestInitKey = decodeExecutor.getInitKey();
        if (initKey != newestInitKey) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". init key expire" +
                        ". initFailed" +
                        ". initKey: " + initKey +
                        ", imageUri: " + e.getImageUri());
            }
            return;
        }

        decodeExecutor.initFailed(e.getException());
    }


    public void postDecodeCompleted(DecodeParams decodeParams, int decodeKey) {
        Message message = obtainMessage(MainHandler.WHAT_DECODE_COMPLETED, decodeParams);
        message.arg1 = decodeKey;
        message.sendToTarget();
    }

    private void decodeCompleted(DecodeParams decodeParams, int decodeKey) {
        ImageRegionDecodeExecutor decodeExecutor = reference.get();
        if (decodeExecutor == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". weak reference break" +
                        ". decodeCompleted" +
                        ". decodeKey: " + decodeKey +
                        ", visibleRect: " + decodeParams.getVisibleRect().toString() +
                        ", inSample: " + decodeParams.getInSampleSize() +
                        ", srcRect: " + decodeParams.getSrcRect().toString() +
                        ", scale: " + decodeParams.getScale());
            }
            decodeParams.getBitmap().recycle();
            return;
        }

        int newestDecodeKey = decodeExecutor.getDecodeKey();
        if (decodeKey != newestDecodeKey) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". decode key expire" +
                        ". decodeCompleted" +
                        ". decodeKey: " + decodeKey +
                        ", visibleRect: " + decodeParams.getVisibleRect().toString() +
                        ", inSample: " + decodeParams.getInSampleSize() +
                        ", srcRect: " + decodeParams.getSrcRect().toString() +
                        ", scale: " + decodeParams.getScale());
            }
            decodeParams.getBitmap().recycle();
            return;
        }

        decodeExecutor.decodeCompleted(decodeParams);
    }


    public void clean() {
        removeMessages(WHAT_RECYCLE_DECODE_THREAD);
        removeMessages(WHAT_INIT_COMPLETED);
        removeMessages(WHAT_INIT_FAILED);
        removeMessages(WHAT_DECODE_COMPLETED);
    }
}
