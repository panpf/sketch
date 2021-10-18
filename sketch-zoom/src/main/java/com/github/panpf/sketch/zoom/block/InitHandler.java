/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.zoom.block;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import com.github.panpf.sketch.SLog;
import com.github.panpf.sketch.util.KeyCounter;

/**
 * 运行在解码线程中，负责初始化 {@link BlockDecoder}
 */
class InitHandler extends Handler {
    private static final String NAME = "InitHandler";
    private static final int WHAT_INIT = 1002;

    @NonNull
    private WeakReference<BlockExecutor> reference;

    InitHandler(@NonNull Looper looper, @NonNull BlockExecutor decodeExecutor) {
        super(looper);
        reference = new WeakReference<>(decodeExecutor);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        BlockExecutor decodeExecutor = reference.get();
        if (decodeExecutor != null) {
            decodeExecutor.callbackHandler.cancelDelayDestroyThread();
        }

        if (msg.what == WHAT_INIT) {
            Wrapper wrapper = (Wrapper) msg.obj;
            init(decodeExecutor, wrapper.imageUri, wrapper.correctImageOrientationDisabled, msg.arg1, wrapper.keyCounter);
        }

        if (decodeExecutor != null) {
            decodeExecutor.callbackHandler.postDelayRecycleDecodeThread();
        }
    }

    void postInit(@NonNull String imageUri, boolean correctImageOrientationDisabled, int key, @NonNull KeyCounter keyCounter) {
        removeMessages(WHAT_INIT);

        Message message = obtainMessage(WHAT_INIT);
        message.arg1 = key;
        message.obj = new Wrapper(imageUri, correctImageOrientationDisabled, keyCounter);
        message.sendToTarget();
    }

    private void init(@Nullable BlockExecutor decodeExecutor, @NonNull String imageUri, boolean correctImageOrientationDisabled, int key, @NonNull KeyCounter keyCounter) {
        if (decodeExecutor == null) {
            SLog.wmf(NAME, "weak reference break. key: %d, imageUri: %s", key, imageUri);
            return;
        }

        int newKey = keyCounter.getKey();
        if (key != newKey) {
            SLog.wmf(NAME, "init key expired. before init. key: %d, newKey: %d, imageUri: %s", key, newKey, imageUri);
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

        if (!decoder.isReady()) {
            decodeExecutor.callbackHandler.postInitError(new Exception("decoder is null or not ready"), imageUri, key, keyCounter);
            return;
        }

        newKey = keyCounter.getKey();
        if (key != newKey) {
            SLog.wmf(NAME, "init key expired. after init. key: %d, newKey: %d, imageUri: %s", key, newKey, imageUri);
            decoder.recycle();
            return;
        }

        decodeExecutor.callbackHandler.postInitCompleted(decoder, imageUri, key, keyCounter);
    }

    public void clean(String why) {
        if (SLog.isLoggable(SLog.VERBOSE)) {
            SLog.vmf(NAME, "clean. %s", why);
        }

        removeMessages(WHAT_INIT);
    }

    @SuppressWarnings("WeakerAccess")
    public static class Wrapper {
        @NonNull
        public String imageUri;
        @NonNull
        public KeyCounter keyCounter;
        public boolean correctImageOrientationDisabled;

        public Wrapper(@NonNull String imageUri, boolean correctImageOrientationDisabled, @NonNull KeyCounter keyCounter) {
            this.imageUri = imageUri;
            this.correctImageOrientationDisabled = correctImageOrientationDisabled;
            this.keyCounter = keyCounter;
        }
    }
}
