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

package me.xiaopan.android.spear.request;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.util.FailureCause;
import me.xiaopan.android.spear.util.RecyclingBitmapDrawable;

public class DisplayJoinLoadListener  implements LoadListener {
    private static String NAME = "DisplayJoinLoadListener";
    private DisplayRequest displayRequest;

    public DisplayJoinLoadListener(DisplayRequest displayRequest) {
        this.displayRequest = displayRequest;
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(Bitmap bitmap, ImageFrom imageFrom) {
        //创建BitmapDrawable并放入内存缓存
        BitmapDrawable bitmapDrawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            bitmapDrawable = new BitmapDrawable(displayRequest.getSpear().getConfiguration().getContext().getResources(), bitmap);
        } else {
            bitmapDrawable = new RecyclingBitmapDrawable(displayRequest.getSpear().getConfiguration().getContext().getResources(), bitmap);
        }
        if(displayRequest.isEnableMemoryCache()){
            if(bitmapDrawable instanceof RecyclingBitmapDrawable){
                ((RecyclingBitmapDrawable) bitmapDrawable).setIsCached(true);
            }
            displayRequest.getSpear().getConfiguration().getMemoryCache().put(displayRequest.getMemoryCacheId(), bitmapDrawable);
        }

        // 已取消
        if (displayRequest.isCanceled()) {
            if(Spear.isDebugMode()){
                Log.w(Spear.LOG_TAG, NAME + "：" + "已取消显示 onCompleted()" + "；" + displayRequest.getName());
            }
            displayRequest.getSpear().getConfiguration().getDisplayCallbackHandler().cancelCallback(displayRequest.getDisplayListener());
            return;
        }

        // 显示
        DisplayListener.ImageFrom displayImageFrom = imageFrom !=null?(imageFrom == LoadListener.ImageFrom.NETWORK? DisplayListener.ImageFrom.NETWORK: DisplayListener.ImageFrom.DISK):null;

        displayRequest.getSpear().getConfiguration().getDisplayCallbackHandler().completeCallback(displayRequest, bitmapDrawable, displayImageFrom);
    }

    @Override
    public void onFailed(FailureCause failureCause) {
        displayRequest.getSpear().getConfiguration().getDisplayCallbackHandler().failCallback(displayRequest, displayRequest.getLoadFailDrawable(), failureCause);
    }

    @Override
    public void onCanceled() {
        if(Spear.isDebugMode()){
            Log.w(Spear.LOG_TAG, NAME + "：" + "已取消显示 onCanceled()" + "；" + displayRequest.getName());
        }
        displayRequest.getSpear().getConfiguration().getDisplayCallbackHandler().cancelCallback(displayRequest.getDisplayListener());
    }
}
