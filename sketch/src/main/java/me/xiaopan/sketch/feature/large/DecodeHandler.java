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
import me.xiaopan.sketch.SketchMonitor;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.decode.ImageFormat;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 解码处理器，运行在解码线程中，负责解码
 */
class DecodeHandler extends Handler {
    private static final String NAME = "DecodeHandler";
    private static final int WHAT_DECODE = 1001;

    private boolean disableInBitmap;

    private WeakReference<TileExecutor> reference;
    private BitmapPool bitmapPool;
    private SketchMonitor monitor;

    public DecodeHandler(Looper looper, TileExecutor executor) {
        super(looper);
        this.reference = new WeakReference<TileExecutor>(executor);
        this.bitmapPool = Sketch.with(executor.callback.getContext()).getConfiguration().getBitmapPool();
        this.monitor = Sketch.with(executor.callback.getContext()).getConfiguration().getMonitor();
    }

    @Override
    public void handleMessage(Message msg) {
        TileExecutor decodeExecutor = reference.get();
        if (decodeExecutor != null) {
            decodeExecutor.mainHandler.cancelDelayDestroyThread();
        }

        switch (msg.what) {
            case WHAT_DECODE:
                decode(decodeExecutor, msg.arg1, (Tile) msg.obj);
                break;
        }

        if (decodeExecutor != null) {
            decodeExecutor.mainHandler.postDelayRecycleDecodeThread();
        }
    }

    public void postDecode(int key, Tile tile) {
        Message message = obtainMessage(DecodeHandler.WHAT_DECODE);
        message.arg1 = key;
        message.obj = tile;
        message.sendToTarget();
    }

    private void decode(TileExecutor executor, int key, Tile tile) {
        if (executor == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". weak reference break. key: " + key + ", tile=" + tile.getInfo());
            }
            return;
        }

        if (tile.isExpired(key)) {
            executor.mainHandler.postDecodeError(key, tile, new DecodeErrorException(DecodeErrorException.CAUSE_BEFORE_KEY_EXPIRED));
            return;
        }

        if (tile.isDecodeParamEmpty()) {
            executor.mainHandler.postDecodeError(key, tile, new DecodeErrorException(DecodeErrorException.CAUSE_DECODE_PARAM_EMPTY));
            return;
        }

        ImageRegionDecoder regionDecoder = tile.decoder;
        if (regionDecoder == null || !regionDecoder.isReady()) {
            executor.mainHandler.postDecodeError(key, tile, new DecodeErrorException(DecodeErrorException.CAUSE_DECODER_NULL_OR_NOT_READY));
            return;
        }

        Rect srcRect = new Rect(tile.srcRect);
        int inSampleSize = tile.inSampleSize;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        ImageFormat imageFormat = regionDecoder.getImageFormat();
        if (imageFormat != null) {
            options.inPreferredConfig = imageFormat.getConfig(false);
        }

        if (!disableInBitmap && SketchUtils.sdkSupportInBitmapForRegionDecoder()) {
            SketchUtils.setInBitmapFromPoolForRegionDecoder(options, srcRect, bitmapPool);
        }

        long time = System.currentTimeMillis();
        Bitmap bitmap = null;
        try {
            bitmap = regionDecoder.decodeRegion(srcRect, options);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

            if (SketchUtils.sdkSupportInBitmapForRegionDecoder()) {
                // 不再使用inBitmap功能
                if (!disableInBitmap) {
                    if (SketchUtils.inBitmapThrowForRegionDecoder(e, options, monitor, bitmapPool,
                            regionDecoder.getImageUri(), regionDecoder.getImageSize().x, regionDecoder.getImageSize().y, srcRect)) {
                        disableInBitmap = true;
                    }
                }

                // 要是因为inBitmap而解码失败就再此尝试
                if (options.inBitmap != null) {
                    options.inBitmap = null;
                    try {
                        bitmap = regionDecoder.decodeRegion(srcRect, options);
                    } catch (Throwable error) {
                        error.printStackTrace();
                    }
                }
            }
        } catch (Throwable error) {
            error.printStackTrace();
        }

        int useTime = (int) (System.currentTimeMillis() - time);

        if (bitmap == null || bitmap.isRecycled()) {
            executor.mainHandler.postDecodeError(key, tile, new DecodeErrorException(DecodeErrorException.CAUSE_BITMAP_NULL));
            return;
        }

        if (tile.isExpired(key)) {
            SketchUtils.freeBitmapToPoolForRegionDecoder(bitmap, Sketch.with(executor.callback.getContext()).getConfiguration().getBitmapPool());
            executor.mainHandler.postDecodeError(key, tile, new DecodeErrorException(DecodeErrorException.CAUSE_AFTER_KEY_EXPIRED));
            return;
        }

        executor.mainHandler.postDecodeCompleted(key, tile, bitmap, useTime);
    }

    public void clean(String why) {
        if (Sketch.isDebugMode()) {
            Log.w(Sketch.TAG, NAME + ". clean. " + why);
        }

        removeMessages(WHAT_DECODE);
    }

    public static class DecodeErrorException extends Exception {
        public static final int CAUSE_BITMAP_NULL = 1101;
        public static final int CAUSE_BEFORE_KEY_EXPIRED = 1102;
        public static final int CAUSE_AFTER_KEY_EXPIRED = 1103;
        public static final int CAUSE_CALLBACK_KEY_EXPIRED = 1104;
        public static final int CAUSE_DECODE_PARAM_EMPTY = 1105;
        public static final int CAUSE_DECODER_NULL_OR_NOT_READY = 1106;

        private int cause;

        public DecodeErrorException(int cause) {
            this.cause = cause;
        }

        public int getErrorCause() {
            return cause;
        }

        public String getCauseMessage() {
            if (cause == CAUSE_BITMAP_NULL) {
                return "bitmap is null or recycled";
            } else if (cause == CAUSE_BEFORE_KEY_EXPIRED) {
                return "key expired before decode";
            } else if (cause == CAUSE_AFTER_KEY_EXPIRED) {
                return "key expired after decode";
            } else if (cause == CAUSE_CALLBACK_KEY_EXPIRED) {
                return "key expired before callback";
            } else if (cause == CAUSE_DECODE_PARAM_EMPTY) {
                return "decode param is empty";
            } else if (cause == CAUSE_DECODER_NULL_OR_NOT_READY) {
                return "decoder is null or not ready";
            } else {
                return "unknown";
            }
        }
    }
}
