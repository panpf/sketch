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
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.decode.ImageFormat;

class DecodeHandler extends Handler {
    private static final int WHAT_DECODE = 1001;
    private static final int WHAT_INIT = 1002;

    private WeakReference<ImageRegionDecodeExecutor> reference;

    private final Object currentDecodeButlerLock = new Object();
    private DecodeButler currentDecodeButler;

    public DecodeHandler(Looper looper, ImageRegionDecodeExecutor decodeExecutor) {
        super(looper);
        reference = new WeakReference<ImageRegionDecodeExecutor>(decodeExecutor);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_INIT:
                init((String) msg.obj);
                break;
            case WHAT_DECODE:
                decode();
                break;
        }
    }

    public void postInit(String imageUri){
        obtainMessage(DecodeHandler.WHAT_INIT, imageUri).sendToTarget();
    }

    private void init(String imageUri) {
        ImageRegionDecodeExecutor decodeExecutor = reference.get();
        if (decodeExecutor == null) {
            return;
        }

        Context context = decodeExecutor.getContext().getApplicationContext();
        try {
            final ImageRegionDecoder decoder = ImageRegionDecoder.build(context, imageUri);
            decodeExecutor.getMainHandler().postInitCompleted(decoder);
        } catch (final Exception e) {
            e.printStackTrace();
            decodeExecutor.getMainHandler().postInitFailed(e);
        }
    }

    public void postDecode(){
        obtainMessage(DecodeHandler.WHAT_DECODE).sendToTarget();
    }

    private void decode() {
        ImageRegionDecodeExecutor decodeExecutor = reference.get();
        if (decodeExecutor == null) {
            return;
        }

        decodeExecutor.getMainHandler().cancelDelayDestroyThread();

        DecodeParams decodeParams = new DecodeParams();
        decodeParams.set(decodeExecutor.getDecodeParams());

        if (!decodeParams.isEmpty()) {
            DecodeButler decodeButler = new DecodeButler();
            synchronized (currentDecodeButlerLock){
                currentDecodeButler = decodeButler;
            }

            Bitmap bitmap = decodeButler.decode(decodeExecutor.getDecoder(), decodeParams);
            if (bitmap != null && !bitmap.isRecycled()) {
                if (!decodeButler.isForceCanceled()) {
                    decodeParams.setBitmap(bitmap);
                    decodeExecutor.getMainHandler().postDecodeCompleted(decodeParams);
                } else {
                    bitmap.recycle();
                }
            }

            synchronized (currentDecodeButlerLock){
                currentDecodeButler = null;
            }
        }

        decodeExecutor.getMainHandler().postDelayDestroyThread();
    }

    public void clean(){
        removeMessages(WHAT_INIT);

        removeMessages(WHAT_DECODE);
        synchronized (currentDecodeButlerLock){
            if (currentDecodeButler != null) {
                currentDecodeButler.cancel(true);
                currentDecodeButler = null;
            }
        }
    }

    public void cancelDecode(boolean force){
        removeMessages(WHAT_DECODE);
        synchronized (currentDecodeButlerLock){
            if (currentDecodeButler != null) {
                currentDecodeButler.cancel(force);
                currentDecodeButler = null;
            }
        }
    }

    static class DecodeButler {
        private int cancelStatus;  // 0：未取消；1：软取消；2：强取消

        @SuppressWarnings("unused")
        public boolean isCanceled() {
            return cancelStatus != 0;
        }

        public boolean isForceCanceled() {
            return cancelStatus == 2;
        }

        public void cancel(boolean force) {
            cancelStatus = force ? 2 : 1;
        }

        public Bitmap decode(ImageRegionDecoder decoder, DecodeParams decodeParams) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = decodeParams.getInSampleSize();
            ImageFormat imageFormat = decoder.getImageFormat();
            if (imageFormat != null) {
                options.inPreferredConfig = imageFormat.getConfig(false);
            }

            return decoder.decodeRegion(decodeParams.getSrcRect(), options);
        }
    }
}
