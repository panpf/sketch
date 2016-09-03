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
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.decode.ImageFormat;

/**
 * 解码处理器，运行在解码线程中，负责解码
 */
class DecodeHandler extends Handler {
    private static final String NAME = "DecodeHandler";
    private static final int WHAT_DECODE = 1001;

    private WeakReference<TileDecodeExecutor> reference;

    public DecodeHandler(Looper looper, TileDecodeExecutor decodeExecutor) {
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
            case WHAT_DECODE:
                decode(decodeExecutor, msg.arg1, (Tile) msg.obj);
                break;
        }

        if (decodeExecutor != null) {
            decodeExecutor.getMainHandler().postDelayRecycleDecodeThread();
        }
    }

    public void postDecode(int key, Tile tile) {
        Message message = obtainMessage(DecodeHandler.WHAT_DECODE);
        message.arg1 = key;
        message.obj = tile;
        message.sendToTarget();
    }

    private void decode(TileDecodeExecutor decodeExecutor, int key, Tile tile) {
        if (tile.isExpired(key)) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". key expired. before decode. key: " + key + ", tile=" + tile.getInfo());
            }
            decodeExecutor.getMainHandler().postDecodeFailed(key, new DecodeFailedException(tile, DecodeFailedException.CAUSE_BEFORE_KEY_EXPIRED));
            return;
        }

        if (tile.isDecodeParamEmpty()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". decode param is empty. key: " + key + ", tile=" + tile.getInfo());
            }
            decodeExecutor.getMainHandler().postDecodeFailed(key, new DecodeFailedException(tile, DecodeFailedException.CAUSE_DECODE_PARAM_EMPTY));
            return;
        }

        if (decodeExecutor == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". weak reference break. key: " + key + ", tile=" + tile.getInfo());
            }
            return;
        }

        Rect srcRect = new Rect(tile.srcRect);
        int inSampleSize = tile.inSampleSize;

        TileDecoder decoder = decodeExecutor.getDecoder();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        ImageFormat imageFormat = decoder.getImageFormat();
        if (imageFormat != null) {
            options.inPreferredConfig = imageFormat.getConfig(false);
        }

        Bitmap bitmap = decoder.decodeRegion(srcRect, options);
        if (bitmap == null || bitmap.isRecycled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". bitmap is null or recycled. after decode. key: " + key + ", tile=" + tile.getInfo());
            }
            decodeExecutor.getMainHandler().postDecodeFailed(key, new DecodeFailedException(tile, DecodeFailedException.CAUSE_BITMAP_NULL));
            return;
        }

        if (tile.isExpired(key)) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". key expired. after decode. key: " + key + ", tile=" + tile.getInfo());
            }
            bitmap.recycle();
            decodeExecutor.getMainHandler().postDecodeFailed(key, new DecodeFailedException(tile, DecodeFailedException.CAUSE_AFTER_KEY_EXPIRED));
            return;
        }

        decodeExecutor.getMainHandler().postDecodeCompleted(key, tile, bitmap);
    }

    public void clean(String why) {
        if (Sketch.isDebugMode()) {
            Log.w(Sketch.TAG, NAME + ". clean. " + why);
        }

        removeMessages(WHAT_DECODE);
    }

    public static class DecodeFailedException extends Exception {
        public static final int CAUSE_BITMAP_NULL = 1101;
        public static final int CAUSE_BEFORE_KEY_EXPIRED = 1102;
        public static final int CAUSE_AFTER_KEY_EXPIRED = 1103;
        public static final int CAUSE_CALLBACK_KEY_EXPIRED = 1104;
        public static final int CAUSE_DECODE_PARAM_EMPTY = 1105;

        public Tile tile;
        private int cause;

        public DecodeFailedException(Tile tile, int cause) {
            this.tile = tile;
            this.cause = cause;
        }

        public int getFailedCause() {
            return cause;
        }

        public String getCauseMessage(){
            if (cause == CAUSE_BITMAP_NULL) {
                return "bitmap is null or recycled";
            } else if(cause == CAUSE_BEFORE_KEY_EXPIRED) {
                return "key expired before decode";
            } else if(cause == CAUSE_AFTER_KEY_EXPIRED) {
                return "key expired after decode";
            } else if(cause == CAUSE_CALLBACK_KEY_EXPIRED) {
                return "key expired before callback";
            } else if(cause == CAUSE_DECODE_PARAM_EMPTY) {
                return "decode param is empty";
            } else {
                return "unknown";
            }
        }
    }
}
