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

package me.xiaopan.android.spear.task;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.display.ImageDisplayer;
import me.xiaopan.android.spear.request.DisplayListener;
import me.xiaopan.android.spear.request.DisplayRequest;
import me.xiaopan.android.spear.request.LoadListener;
import me.xiaopan.android.spear.request.Request;
import me.xiaopan.android.spear.util.FailureCause;
import me.xiaopan.android.spear.util.RecyclingBitmapDrawable;

public class DisplayJoinLoadListener implements LoadListener {
    private static String NAME= DisplayJoinLoadListener.class.getSimpleName();
    private DisplayRequest displayRequest;

    public DisplayJoinLoadListener(DisplayRequest displayRequest) {
        this.displayRequest = displayRequest;
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(Bitmap bitmap, From from) {
        //创建BitmapDrawable并放入内存缓存
        BitmapDrawable bitmapDrawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            bitmapDrawable = new BitmapDrawable(displayRequest.getSpear().getContext().getResources(), bitmap);
        } else {
            bitmapDrawable = new RecyclingBitmapDrawable(displayRequest.getSpear().getContext().getResources(), bitmap);
        }
        if(displayRequest.isEnableMemoryCache()){
            displayRequest.getSpear().getMemoryCache().put(displayRequest.getId(), bitmapDrawable);
        }

        // 已取消
        if (displayRequest.isCanceled()) {
            if(displayRequest.getSpear().isDebugMode()){
                Log.w(Spear.LOG_TAG, NAME + "：" + "已取消显示" + "；" + displayRequest.getName());
            }
            if(displayRequest.getDisplayListener() != null){
                displayRequest.getSpear().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        displayRequest.getDisplayListener().onCanceled();
                    }
                });
            }
            return;
        }

        // 显示
        DisplayListener.From displayFrom = from!=null?(from==From.NETWORK?DisplayListener.From.NETWORK:DisplayListener.From.LOCAL):null;
        displayRequest.getSpear().getHandler().post(new DisplayRunnable(displayRequest, bitmapDrawable, ImageDisplayer.BitmapType.SUCCESS, null, displayFrom));
    }

    @Override
    public void onFailed(FailureCause failureCause) {
        displayRequest.getSpear().getHandler().post(new DisplayRunnable(displayRequest, displayRequest.getFailedDrawable(), ImageDisplayer.BitmapType.FAILURE, failureCause, null));
    }

    @Override
    public void onCanceled() {
        if(displayRequest.getSpear().isDebugMode()){
            Log.w(Spear.LOG_TAG, NAME + "：" + "已取消显示" + "；" + displayRequest.getName());
        }
        if(displayRequest.getDisplayListener() != null){
            displayRequest.getSpear().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    displayRequest.getDisplayListener().onCanceled();
                }
            });
        }
    }

    private static class DisplayRunnable implements Runnable {
        private static String NAME= DisplayRunnable.class.getSimpleName();
        private DisplayRequest displayRequest;
        private ImageDisplayer.BitmapType bitmapType;
        private BitmapDrawable bitmapDrawable;
        private FailureCause failureCause;
        private DisplayListener.From from;

        public DisplayRunnable(DisplayRequest displayRequest, BitmapDrawable bitmapDrawable, ImageDisplayer.BitmapType bitmapType, FailureCause failureCause, DisplayListener.From from) {
            this.displayRequest = displayRequest;
            this.bitmapDrawable = bitmapDrawable;
            this.bitmapType = bitmapType;
            this.failureCause = failureCause;
            this.from = from;
        }

        @Override
        public void run() {
            if(displayRequest.isCanceled()){
                if(displayRequest.getSpear().isDebugMode()){
                    Log.w(Spear.LOG_TAG, NAME + "：" + "已取消显示" + "；" + displayRequest.getName());
                }
                if(displayRequest.getDisplayListener() != null){
                    displayRequest.getDisplayListener().onCanceled();
                }
                return;
            }

            displayRequest.setStatus(bitmapType == ImageDisplayer.BitmapType.SUCCESS?Request.Status.COMPLETED: Request.Status.FAILED);

            ImageView imageView = displayRequest.getImageViewHolder().getImageView();
            if(imageView == null){
                return;
            }

            displayRequest.getImageDisplayer().display(imageView, bitmapDrawable, bitmapType, displayRequest);

            if(displayRequest.getDisplayListener() != null){
                if(bitmapType == ImageDisplayer.BitmapType.SUCCESS){
                    displayRequest.getDisplayListener().onCompleted(displayRequest.getUri(), imageView, bitmapDrawable, from);
                }else{
                    displayRequest.getDisplayListener().onFailed(failureCause);
                }
            }
        }
    }
}
