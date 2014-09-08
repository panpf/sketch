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

package me.xiaopan.android.imageloader.task.display;

import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.display.BitmapDisplayer;
import me.xiaopan.android.imageloader.task.load.LoadListener;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import me.xiaopan.android.imageloader.util.RecyclingBitmapDrawable;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class DisplayJoinLoadListener implements LoadListener {
    private static String NAME= DisplayJoinLoadListener.class.getSimpleName();
    private DisplayRequest displayRequest;

    public DisplayJoinLoadListener(DisplayRequest displayRequest) {
        this.displayRequest = displayRequest;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onUpdateProgress(final long totalLength, final long completedLength) {
        if(displayRequest.getDisplayListener() != null){
            displayRequest.getConfiguration().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    displayRequest.getDisplayListener().onUpdateProgress(totalLength, completedLength);
                }
            });
        }
    }

    @Override
    public void onSuccess(Bitmap bitmap) {
        //创建BitmapDrawable
        BitmapDrawable bitmapDrawable;
        if (ImageLoaderUtils.hasHoneycomb()) {
            bitmapDrawable = new BitmapDrawable(displayRequest.getConfiguration().getContext().getResources(), bitmap);
        } else {
            bitmapDrawable = new RecyclingBitmapDrawable(displayRequest.getConfiguration().getContext().getResources(), bitmap);
        }

        //放入内存缓存
        if(displayRequest.getDisplayOptions().isEnableMemoryCache()){
            displayRequest.getConfiguration().getMemoryCache().put(displayRequest.getId(), bitmapDrawable);
        }

        //显示
        if (!displayRequest.getImageViewHolder().isCollected()) {
            displayRequest.getConfiguration().getHandler().post(new DisplayRunnable(displayRequest, bitmapDrawable, BitmapDisplayer.BitmapType.SUCCESS));
        }else{
            if(displayRequest.getDisplayListener() != null){
                displayRequest.getConfiguration().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        displayRequest.getDisplayListener().onCancel();
                    }
                });
            }
            if(displayRequest.getConfiguration().isDebugMode()){
                Log.w(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("已解除绑定关系").append("；").append(displayRequest.getName()).toString());
            }
        }
    }

    @Override
    public void onFailure() {
        displayRequest.getConfiguration().getHandler().post(new DisplayRunnable(displayRequest, displayRequest.getDisplayOptions().getLoadFailDrawable(), BitmapDisplayer.BitmapType.FAILURE));
    }

    @Override
    public void onCancel() {
        if(displayRequest.getDisplayListener() != null){
            displayRequest.getConfiguration().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    displayRequest.getDisplayListener().onCancel();
                }
            });
        }
        if(displayRequest.getConfiguration().isDebugMode()){
            Log.w(ImageLoader.LOG_TAG, new StringBuilder(NAME).append("：").append("已取消加载").append("；").append(displayRequest.getName()).toString());
        }
    }
}
