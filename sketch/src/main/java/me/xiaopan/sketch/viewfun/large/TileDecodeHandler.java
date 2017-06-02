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

package me.xiaopan.sketch.viewfun.large;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.ErrorTracker;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.decode.ImageDecodeUtils;
import me.xiaopan.sketch.decode.ImageOrientationCorrector;
import me.xiaopan.sketch.decode.ImageType;

/**
 * 解码处理器，运行在解码线程中，负责解码
 */
class TileDecodeHandler extends Handler {
    private static final String NAME = "DecodeHandler";
    private static final int WHAT_DECODE = 1001;

    private boolean disableInBitmap;

    private WeakReference<TileExecutor> reference;
    private BitmapPool bitmapPool;
    private ErrorTracker errorTracker;
    private ImageOrientationCorrector orientationCorrector;

    public TileDecodeHandler(Looper looper, TileExecutor executor) {
        super(looper);
        this.reference = new WeakReference<>(executor);

        Configuration configuration = Sketch.with(executor.callback.getContext()).getConfiguration();
        this.bitmapPool = configuration.getBitmapPool();
        this.errorTracker = configuration.getErrorTracker();
        this.orientationCorrector = configuration.getImageOrientationCorrector();
    }

    @Override
    public void handleMessage(Message msg) {
        TileExecutor decodeExecutor = reference.get();
        if (decodeExecutor != null) {
            decodeExecutor.tileDecodeCallbackHandler.cancelDelayDestroyThread();
        }

        switch (msg.what) {
            case WHAT_DECODE:
                decode(decodeExecutor, msg.arg1, (Tile) msg.obj);
                break;
        }

        if (decodeExecutor != null) {
            decodeExecutor.tileDecodeCallbackHandler.postDelayRecycleDecodeThread();
        }
    }

    public void postDecode(int key, Tile tile) {
        Message message = obtainMessage(TileDecodeHandler.WHAT_DECODE);
        message.arg1 = key;
        message.obj = tile;
        message.sendToTarget();
    }

    private void decode(TileExecutor executor, int key, Tile tile) {
        if (executor == null) {
            if (SLogType.LARGE.isEnabled()) {
                SLog.fw(SLogType.LARGE, NAME, "weak reference break. key: %d, tile=%s", key, tile.getInfo());
            }
            return;
        }

        if (tile.isExpired(key)) {
            executor.tileDecodeCallbackHandler.postDecodeError(key, tile, new DecodeErrorException(DecodeErrorException.CAUSE_BEFORE_KEY_EXPIRED));
            return;
        }

        if (tile.isDecodeParamEmpty()) {
            executor.tileDecodeCallbackHandler.postDecodeError(key, tile, new DecodeErrorException(DecodeErrorException.CAUSE_DECODE_PARAM_EMPTY));
            return;
        }

        ImageRegionDecoder regionDecoder = tile.decoder;
        if (regionDecoder == null || !regionDecoder.isReady()) {
            executor.tileDecodeCallbackHandler.postDecodeError(key, tile, new DecodeErrorException(DecodeErrorException.CAUSE_DECODER_NULL_OR_NOT_READY));
            return;
        }

        Rect srcRect = new Rect(tile.srcRect);
        int inSampleSize = tile.inSampleSize;

        // 根据图片方向恢复src区域的真实位置
        Point imageSize = regionDecoder.getImageSize();
        orientationCorrector.reverseRotate(srcRect, imageSize.x, imageSize.y, regionDecoder.getExifOrientation());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        ImageType imageType = regionDecoder.getImageType();
        if (imageType != null) {
            options.inPreferredConfig = imageType.getConfig(false);
        }

        if (!disableInBitmap && BitmapPoolUtils.sdkSupportInBitmapForRegionDecoder()) {
            BitmapPoolUtils.setInBitmapFromPoolForRegionDecoder(options, srcRect, bitmapPool);
        }

        long time = System.currentTimeMillis();
        Bitmap bitmap = null;
        try {
            bitmap = regionDecoder.decodeRegion(srcRect, options);
        } catch (Throwable throwable) {
            throwable.printStackTrace();

            if (ImageDecodeUtils.isInBitmapDecodeError(throwable, options, true)) {
                disableInBitmap = true;

                ImageDecodeUtils.recycleInBitmapOnDecodeError(errorTracker, bitmapPool, regionDecoder.getImageUri(),
                        regionDecoder.getImageSize().x, regionDecoder.getImageSize().y, regionDecoder.getImageType().getMimeType(), throwable, options, true);

                try {
                    bitmap = regionDecoder.decodeRegion(srcRect, options);
                } catch (Throwable throwable1) {
                    throwable1.printStackTrace();
                }
            } else if (ImageDecodeUtils.isSrcRectDecodeError(throwable, regionDecoder.getImageSize().x, regionDecoder.getImageSize().y, srcRect)) {
                errorTracker.onDecodeRegionError(regionDecoder.getImageUri(), regionDecoder.getImageSize().x, regionDecoder.getImageSize().y,
                        regionDecoder.getImageType().getMimeType(), throwable, srcRect, options.inSampleSize);
            }
        }

        int useTime = (int) (System.currentTimeMillis() - time);

        if (bitmap == null || bitmap.isRecycled()) {
            executor.tileDecodeCallbackHandler.postDecodeError(key, tile, new DecodeErrorException(DecodeErrorException.CAUSE_BITMAP_NULL));
            return;
        }

        if (tile.isExpired(key)) {
            BitmapPoolUtils.freeBitmapToPoolForRegionDecoder(bitmap, Sketch.with(executor.callback.getContext()).getConfiguration().getBitmapPool());
            executor.tileDecodeCallbackHandler.postDecodeError(key, tile, new DecodeErrorException(DecodeErrorException.CAUSE_AFTER_KEY_EXPIRED));
            return;
        }

        // 旋转图片
        Bitmap newBitmap = orientationCorrector.rotate(bitmap, regionDecoder.getExifOrientation(), bitmapPool);
        if (newBitmap != null && newBitmap != bitmap) {
            if (!newBitmap.isRecycled()) {
                BitmapPoolUtils.freeBitmapToPool(bitmap, bitmapPool);
                bitmap = newBitmap;
            } else {
                executor.tileDecodeCallbackHandler.postDecodeError(key, tile, new DecodeErrorException(DecodeErrorException.CAUSE_ROTATE_BITMAP_RECYCLED));
                return;
            }
        }

        if (bitmap.isRecycled()) {
            executor.tileDecodeCallbackHandler.postDecodeError(key, tile, new DecodeErrorException(DecodeErrorException.CAUSE_BITMAP_RECYCLED));
            return;
        }

        executor.tileDecodeCallbackHandler.postDecodeCompleted(key, tile, bitmap, useTime);
    }

    public void clean(String why) {
        if (SLogType.LARGE.isEnabled()) {
            SLog.w(SLogType.LARGE, NAME, "clean. %s" + why);
        }

        removeMessages(WHAT_DECODE);
    }

    public static class DecodeErrorException extends Exception {
        public static final int CAUSE_BITMAP_RECYCLED = 1100;
        public static final int CAUSE_BITMAP_NULL = 1101;
        public static final int CAUSE_BEFORE_KEY_EXPIRED = 1102;
        public static final int CAUSE_AFTER_KEY_EXPIRED = 1103;
        public static final int CAUSE_CALLBACK_KEY_EXPIRED = 1104;
        public static final int CAUSE_DECODE_PARAM_EMPTY = 1105;
        public static final int CAUSE_DECODER_NULL_OR_NOT_READY = 1106;
        public static final int CAUSE_ROTATE_BITMAP_RECYCLED = 1107;

        private int cause;

        public DecodeErrorException(int cause) {
            this.cause = cause;
        }

        public int getErrorCause() {
            return cause;
        }

        public String getCauseMessage() {
            if (cause == CAUSE_BITMAP_RECYCLED) {
                return "bitmap is recycled";
            } else if (cause == CAUSE_BITMAP_NULL) {
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
            } else if (cause == CAUSE_ROTATE_BITMAP_RECYCLED) {
                return "rotate result bitmap is recycled";
            } else {
                return "unknown";
            }
        }
    }
}
