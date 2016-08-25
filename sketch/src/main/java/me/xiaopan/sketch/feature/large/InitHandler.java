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
 * 专门负责初始化ImageRegionDecoder
 */
public class InitHandler extends Handler {
    private static final String NAME = "InitHandler";
    private static final int WHAT_INIT = 1002;

    private WeakReference<ImageRegionDecodeExecutor> reference;

    public InitHandler(Looper looper, ImageRegionDecodeExecutor decodeExecutor) {
        super(looper);
        reference = new WeakReference<ImageRegionDecodeExecutor>(decodeExecutor);
    }

    @Override
    public void handleMessage(Message msg) {
        ImageRegionDecodeExecutor decodeExecutor = reference.get();
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

        Message message = obtainMessage(WHAT_INIT, imageUri);
        message.arg1 = initKey;
        message.sendToTarget();
    }

    private void init(ImageRegionDecodeExecutor decodeExecutor, String imageUri, int initKey) {
        if (decodeExecutor == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". weak reference break" +
                        ". initKey: " + initKey +
                        ", imageUri: " + imageUri);
            }
            return;
        }

        int newestInitKey = decodeExecutor.getInitKey();
        if (initKey != newestInitKey) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". init key expired" +
                        ". before init" +
                        ". initKey: " + initKey +
                        ", newestInitKey: " + newestInitKey +
                        ", imageUri: " + imageUri);
            }
        }

        ImageRegionDecoder decoder;
        try {
            decoder = ImageRegionDecoder.build(decodeExecutor.getContext(), imageUri);
        } catch (final Exception e) {
            e.printStackTrace();
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". init failed" +
                        ". exception" +
                        ". initKey: " + initKey +
                        ", imageUri: " + imageUri);
            }
            decodeExecutor.getMainHandler().postInitFailed(new InitFailedException(e, imageUri), initKey);
            return;
        }

        if (decoder == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". init failed" +
                        ". decode is null" +
                        ". initKey: " + initKey +
                        ", imageUri: " + imageUri);
            }
            decodeExecutor.getMainHandler().postInitFailed(new InitFailedException(new Exception("decoder is null"), imageUri), initKey);
            return;
        }

        newestInitKey = decodeExecutor.getInitKey();
        if (initKey != newestInitKey) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". init key expired" +
                        ". after init" +
                        ". initKey: " + initKey +
                        ". newestInitKey: " + newestInitKey +
                        ", imageUri: " + imageUri);
            }
            decoder.recycle();
            return;
        }

        decodeExecutor.getMainHandler().postInitCompleted(decoder, initKey);
    }

    public void clean() {
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
