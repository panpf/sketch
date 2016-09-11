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

/**
 * 运行在解码线程中，负责初始化TileDecoder
 */
class InitHandler extends Handler {
    private static final String NAME = "InitHandler";
    private static final int WHAT_INIT = 1002;

    private WeakReference<TileDecodeExecutor> reference;

    public InitHandler(Looper looper, TileDecodeExecutor decodeExecutor) {
        super(looper);
        reference = new WeakReference<TileDecodeExecutor>(decodeExecutor);
    }

    @Override
    public void handleMessage(Message msg) {
        TileDecodeExecutor decodeExecutor = reference.get();
        if (decodeExecutor != null) {
            decodeExecutor.getMainHandler().cancelDelayDestroyThread();
        }

        switch (msg.what) {
            case WHAT_INIT:
                init(decodeExecutor, (String) msg.obj, msg.arg1);
                break;
        }

        if (decodeExecutor != null) {
            decodeExecutor.getMainHandler().postDelayRecycleDecodeThread();
        }
    }

    public void postInit(String imageUri, int initKey) {
        removeMessages(WHAT_INIT);

        Message message = obtainMessage(WHAT_INIT);
        message.arg1 = initKey;
        message.obj = imageUri;
        message.sendToTarget();
    }

    private void init(TileDecodeExecutor decodeExecutor, String imageUri, int key) {
        if (decodeExecutor == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". weak reference break. key: " + key + ", imageUri: " + imageUri);
            }
            return;
        }

        int newKey = decodeExecutor.getInitKey();
        if (key != newKey) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". init key expired. before init. key: " + key + ", newKey: " + newKey + ", imageUri: " + imageUri);
            }
        }

        TileDecoder decoder;
        try {
            decoder = TileDecoder.build(decodeExecutor.getContext(), imageUri);
        } catch (final Exception e) {
            e.printStackTrace();
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". init failed. exception. key: " + key + ", imageUri: " + imageUri);
            }
            decodeExecutor.getMainHandler().postInitFailed(new InitFailedException(e, imageUri), key);
            return;
        }

        if (decoder == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". init failed. decode is null. key: " + key + ", imageUri: " + imageUri);
            }
            decodeExecutor.getMainHandler().postInitFailed(new InitFailedException(new Exception("decoder is null"), imageUri), key);
            return;
        }

        newKey = decodeExecutor.getInitKey();
        if (key != newKey) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". init key expired. after init. key: " + key + ", newKey: " + newKey + ", imageUri: " + imageUri);
            }
            decoder.recycle();
            return;
        }

        decodeExecutor.getMainHandler().postInitCompleted(decoder, key);
    }

    public void clean(String why, String imageUri) {
        if (Sketch.isDebugMode()) {
            Log.w(Sketch.TAG, NAME + ". clean. " + why + ". " + imageUri);
        }

        removeMessages(WHAT_INIT);
    }

    public static class InitFailedException extends Exception {
        private String imageUri;

        public InitFailedException(Exception throwable, String imageUri) {
            super(throwable);
            this.imageUri = imageUri;
        }

        public String getImageUri() {
            return imageUri;
        }

        public Exception getException() {
            return (Exception) getCause();
        }
    }
}
