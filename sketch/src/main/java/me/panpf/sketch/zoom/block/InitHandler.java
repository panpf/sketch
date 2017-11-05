/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.zoom.block;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import me.panpf.sketch.SLog;
import me.panpf.sketch.util.KeyCounter;

/**
 * 运行在解码线程中，负责初始化 {@link BlockDecoder}
 */
class InitHandler extends Handler {
    private static final String NAME = "InitHandler";
    private static final int WHAT_INIT = 1002;

    private WeakReference<BlockExecutor> reference;

    public InitHandler(Looper looper, BlockExecutor decodeExecutor) {
        super(looper);
        reference = new WeakReference<>(decodeExecutor);
    }

    @Override
    public void handleMessage(Message msg) {
        BlockExecutor decodeExecutor = reference.get();
        if (decodeExecutor != null) {
            decodeExecutor.callbackHandler.cancelDelayDestroyThread();
        }

        switch (msg.what) {
            case WHAT_INIT:
                Wrapper wrapper = (Wrapper) msg.obj;
                init(decodeExecutor, wrapper.imageUri, wrapper.correctImageOrientationDisabled, msg.arg1, wrapper.keyCounter);
                break;
        }

        if (decodeExecutor != null) {
            decodeExecutor.callbackHandler.postDelayRecycleDecodeThread();
        }
    }

    public void postInit(String imageUri, boolean correctImageOrientationDisabled, int key, KeyCounter keyCounter) {
        removeMessages(WHAT_INIT);

        Message message = obtainMessage(WHAT_INIT);
        message.arg1 = key;
        message.obj = new Wrapper(imageUri, correctImageOrientationDisabled, keyCounter);
        message.sendToTarget();
    }

    private void init(BlockExecutor decodeExecutor, String imageUri, boolean correctImageOrientationDisabled, int key, KeyCounter keyCounter) {
        if (decodeExecutor == null) {
            SLog.w(NAME, "weak reference break. key: %d, imageUri: %s", key, imageUri);
            return;
        }

        int newKey = keyCounter.getKey();
        if (key != newKey) {
            SLog.w(NAME, "init key expired. before init. key: %d, newKey: %d, imageUri: %s", key, newKey, imageUri);
            return;
        }

        ImageRegionDecoder decoder;
        try {
            decoder = ImageRegionDecoder.build(decodeExecutor.callback.getContext(), imageUri, correctImageOrientationDisabled);
        } catch (final Exception e) {
            e.printStackTrace();
            decodeExecutor.callbackHandler.postInitError(e, imageUri, key, keyCounter);
            return;
        }

        if (decoder == null || !decoder.isReady()) {
            decodeExecutor.callbackHandler.postInitError(new Exception("decoder is null or not ready"), imageUri, key, keyCounter);
            return;
        }

        newKey = keyCounter.getKey();
        if (key != newKey) {
            SLog.w(NAME, "init key expired. after init. key: %d, newKey: %d, imageUri: %s", key, newKey, imageUri);
            decoder.recycle();
            return;
        }

        decodeExecutor.callbackHandler.postInitCompleted(decoder, imageUri, key, keyCounter);
    }

    public void clean(String why) {
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
            SLog.d(NAME, "clean. %s", why);
        }

        removeMessages(WHAT_INIT);
    }

    public static class Wrapper {
        public String imageUri;
        public KeyCounter keyCounter;
        public boolean correctImageOrientationDisabled;

        public Wrapper(String imageUri, boolean correctImageOrientationDisabled, KeyCounter keyCounter) {
            this.imageUri = imageUri;
            this.correctImageOrientationDisabled = correctImageOrientationDisabled;
            this.keyCounter = keyCounter;
        }
    }
}
