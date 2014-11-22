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

import java.io.File;
import java.util.concurrent.Executor;

import me.xiaopan.android.spear.decode.ByteArrayDecodeListener;
import me.xiaopan.android.spear.decode.CacheFileDecodeListener;
import me.xiaopan.android.spear.request.DownloadListener;
import me.xiaopan.android.spear.request.LoadListener;
import me.xiaopan.android.spear.request.LoadRequest;
import me.xiaopan.android.spear.util.FailureCause;

public class LoadJoinDownloadListener implements DownloadListener {

    private Executor executor;
    private LoadRequest loadRequest;

    public LoadJoinDownloadListener(Executor executor, LoadRequest loadRequest) {
        this.executor = executor;
        this.loadRequest = loadRequest;
    }

    @Override
	public void onStarted() {

	}

    @Override
    public void onCompleted(File cacheFile, From from) {
        executor.execute(new LoadTask(loadRequest, new CacheFileDecodeListener(cacheFile, loadRequest), from!=null?(from==From.LOCAL_CACHE?LoadListener.From.LOCAL :LoadListener.From.NETWORK):null));
    }

    @Override
    public void onCompleted(byte[] data, From from) {
        executor.execute(new LoadTask(loadRequest, new ByteArrayDecodeListener(data, loadRequest), from!=null?(from==From.LOCAL_CACHE?LoadListener.From.LOCAL :LoadListener.From.NETWORK):null));
    }

	@Override
	public void onFailed(FailureCause failureCause) {
        if(loadRequest.getLoadListener() != null){
            loadRequest.getLoadListener().onFailed(failureCause);
        }
	}

    @Override
    public void onCanceled() {
        if(loadRequest.getLoadListener() != null){
            loadRequest.getLoadListener().onCanceled();
        }
    }
}
