/*
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package me.xiaopan.sketch.pool;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

/**
 * This class contains utility method for Bitmap
 */
public final class BitmapUtil {
  private static final int DECODE_BUFFER_SIZE = 16 * 1024;
  private static final int POOL_SIZE = 12;
  private static final Pools.SynchronizedPool<ByteBuffer> DECODE_BUFFERS
      = new Pools.SynchronizedPool<ByteBuffer>(POOL_SIZE);

  /**
   * Bytes per pixel definitions
   */
  public static final int ALPHA_8_BYTES_PER_PIXEL = 1;
  public static final int ARGB_4444_BYTES_PER_PIXEL = 2;
  public static final int ARGB_8888_BYTES_PER_PIXEL = 4;
  public static final int RGB_565_BYTES_PER_PIXEL = 2;

  public static final float MAX_BITMAP_SIZE = 2048f;

  /**
   * Returns the amount of bytes used by a pixel in a specific
   * {@link android.graphics.Bitmap.Config}
   * @param bitmapConfig the {@link android.graphics.Bitmap.Config} for which the size in byte
   * will be returned
   * @return
   */
  public static int getPixelSizeForBitmapConfig(Bitmap.Config bitmapConfig) {

    switch (bitmapConfig) {
      case ARGB_8888:
        return ARGB_8888_BYTES_PER_PIXEL;
      case ALPHA_8:
        return ALPHA_8_BYTES_PER_PIXEL;
      case ARGB_4444:
        return ARGB_4444_BYTES_PER_PIXEL;
      case RGB_565:
        return RGB_565_BYTES_PER_PIXEL;
    }
    throw new UnsupportedOperationException("The provided Bitmap.Config is not supported");
  }

  /**
   * Returns the size in byte of an image with specific size
   * and {@link android.graphics.Bitmap.Config}
   * @param width the width of the image
   * @param height the height of the image
   * @param bitmapConfig the {@link android.graphics.Bitmap.Config} for which the size in byte
   * will be returned
   * @return
   */
  public static int getSizeInByteForBitmap(int width, int height, Bitmap.Config bitmapConfig) {
    return width * height * getPixelSizeForBitmapConfig(bitmapConfig);
  }
}
