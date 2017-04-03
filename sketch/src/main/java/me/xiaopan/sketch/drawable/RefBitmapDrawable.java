/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.drawable;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.util.SketchUtils;

public class RefBitmapDrawable extends BitmapDrawable implements RefDrawable {
    protected String logName = "RefBitmapDrawable";

    private RefBitmap refBitmap;
    private ImageFrom imageFrom;

    public RefBitmapDrawable(RefBitmap refBitmap) {
        super(null, refBitmap.getBitmap());

        if (refBitmap.isRecycled()) {
            throw new IllegalArgumentException("refBitmap recycled. " + refBitmap.getInfo());
        }

        this.refBitmap = refBitmap;

        // 这一步很重要，让BitmapDrawable的density和Bitmap的density保持一致
        // 这样getIntrinsicWidth()和getIntrinsicHeight()方法得到的就是bitmap的真实的（未经过缩放）尺寸
        setTargetDensity(refBitmap.getBitmap().getDensity());
    }

    protected void setLogName(String logName) {
        this.logName = logName;
    }

    @Override
    public String getKey() {
        return refBitmap.getKey();
    }

    @Override
    public String getUri() {
        return refBitmap.getUri();
    }

    @Override
    public int getOriginWidth() {
        return refBitmap.getAttrs().getOriginWidth();
    }

    @Override
    public int getOriginHeight() {
        return refBitmap.getAttrs().getOriginHeight();
    }

    @Override
    public String getMimeType() {
        return refBitmap.getAttrs().getMimeType();
    }

    @Override
    public int getOrientation() {
        return refBitmap.getAttrs().getOrientation();
    }

    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    @Override
    public void setImageFrom(ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }

    @Override
    public String getInfo() {
        // TODO: 2017/4/3 图片信息里加上ImageAttrs里的全部信息
        return SketchUtils.makeImageInfo(logName, getBitmap(), getMimeType(), getByteCount());
    }

    @Override
    public int getByteCount() {
        return refBitmap.getByteCount();
    }

    @Override
    public Bitmap.Config getBitmapConfig() {
        return refBitmap.getBitmapConfig();
    }

    @Override
    public void setIsDisplayed(String callingStation, boolean displayed) {
        refBitmap.setIsDisplayed(callingStation, displayed);
    }

    @Override
    public void setIsCached(String callingStation, boolean cached) {
        refBitmap.setIsCached(callingStation, cached);
    }

    @Override
    public void setIsWaitingUse(String callingStation, boolean waitingUse) {
        refBitmap.setIsWaitingUse(callingStation, waitingUse);
    }

    @Override
    public boolean isRecycled() {
        return refBitmap.isRecycled();
    }
}