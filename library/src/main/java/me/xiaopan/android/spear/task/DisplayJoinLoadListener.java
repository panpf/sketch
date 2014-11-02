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

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.request.DisplayListener;
import me.xiaopan.android.spear.request.DisplayRequest;
import me.xiaopan.android.spear.request.LoadListener;
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
            displayRequest.getSpear().getDisplayCallbackHandler().cancelCallback(displayRequest.getDisplayListener());
            return;
        }

        // 显示
        DisplayListener.From displayFrom = from!=null?(from==From.NETWORK?DisplayListener.From.NETWORK:DisplayListener.From.LOCAL):null;

        displayRequest.getSpear().getDisplayCallbackHandler().completeCallback(displayRequest, bitmapDrawable, displayFrom);
    }

    @Override
    public void onFailed(FailureCause failureCause) {
        displayRequest.getSpear().getDisplayCallbackHandler().failCallback(displayRequest, displayRequest.getFailedDrawable(), failureCause);
    }

    @Override
    public void onCanceled() {
        if(displayRequest.getSpear().isDebugMode()){
            Log.w(Spear.LOG_TAG, NAME + "：" + "已取消显示" + "；" + displayRequest.getName());
        }
        displayRequest.getSpear().getDisplayCallbackHandler().cancelCallback(displayRequest.getDisplayListener());
    }
}
