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
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.decode.ImageFormat;

class DecodeHandler extends Handler {
    private static final String NAME = "DecodeHandler";
    private static final int WHAT_DECODE = 1001;

    private WeakReference<ImageRegionDecodeExecutor> reference;

    public DecodeHandler(Looper looper, ImageRegionDecodeExecutor decodeExecutor) {
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
            case WHAT_DECODE:
                decode(decodeExecutor, msg.arg1);
                break;
        }

        if (decodeExecutor != null) {
            decodeExecutor.getMainHandler().postDelayRecycleDecodeThread();
        }
    }

    public void postDecode(int decodeKey) {
        removeMessages(DecodeHandler.WHAT_DECODE);

        Message message = obtainMessage(DecodeHandler.WHAT_DECODE);
        message.arg1 = decodeKey;
        message.sendToTarget();
    }

    private void decode(ImageRegionDecodeExecutor decodeExecutor, int decodeKey) {
        if (decodeExecutor == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". weak reference break. decodeKey: " + decodeKey);
            }
            return;
        }

        int newestDecodeKey = decodeExecutor.getDecodeKey();
        if (decodeKey != newestDecodeKey) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". decode key expired" +
                        ". before decode" +
                        ". decodeKey: " + decodeKey +
                        ", newestDecodeKey: " + newestDecodeKey);
            }
            return;
        }

        DecodeParams decodeParams = new DecodeParams();
        decodeParams.set(decodeExecutor.getDecodeParams());
        if (decodeParams.isEmpty()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". decode params is empty. decodeKey: " + decodeKey);
            }
            return;
        }

        ImageRegionDecoder decoder = decodeExecutor.getDecoder();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = decodeParams.getInSampleSize();
        ImageFormat imageFormat = decoder.getImageFormat();
        if (imageFormat != null) {
            options.inPreferredConfig = imageFormat.getConfig(false);
        }

        Bitmap bitmap = decoder.decodeRegion(decodeParams.getSrcRect(), options);
        if (bitmap == null || bitmap.isRecycled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". bitmap is null or recycled" +
                        ". after decode" +
                        ". decodeKey: " + decodeKey);
            }
            return;
        }

        newestDecodeKey = decodeExecutor.getDecodeKey();
        if (decodeKey != newestDecodeKey) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME +
                        ". decode key expired" +
                        ". after decode" +
                        ". decodeKey: " + decodeKey +
                        ", newestDecodeKey: " + newestDecodeKey);
            }
            bitmap.recycle();
            return;
        }

        decodeParams.setBitmap(bitmap);
        decodeExecutor.getMainHandler().postDecodeCompleted(decodeParams, decodeKey);
    }

    public void clean() {
        removeMessages(WHAT_DECODE);
    }
}
