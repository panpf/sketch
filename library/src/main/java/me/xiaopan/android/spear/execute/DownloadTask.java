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

package me.xiaopan.android.spear.execute;

import java.io.File;

import me.xiaopan.android.spear.download.ImageDownloader;
import me.xiaopan.android.spear.request.DownloadListener;
import me.xiaopan.android.spear.request.DownloadRequest;
import me.xiaopan.android.spear.request.LoadRequest;
import me.xiaopan.android.spear.request.Request;

/**
 * 下载任务
 */
public class DownloadTask implements Runnable {
	private DownloadRequest downloadRequest;
	
	public DownloadTask(DownloadRequest downloadRequest) {
		this.downloadRequest = downloadRequest;
	}
	
	@Override
	public void run() {
        if(downloadRequest.isCanceled()){
            if(downloadRequest.getDownloadListener() != null){
                downloadRequest.getDownloadListener().onCanceled();
            }
            return;
        }

        downloadRequest.setStatus(Request.Status.LOADING);
        ImageDownloader.DownloadResult downloadResult = downloadRequest.getSpear().getConfiguration().getImageDownloader().download(downloadRequest);

        if(downloadRequest.isCanceled()){
            if(downloadRequest.getDownloadListener() != null){
                downloadRequest.getDownloadListener().onCanceled();
            }
            return;
		}

        if(downloadResult != null && downloadResult.getResult() == null){
            downloadResult = null;
        }

        if(downloadResult != null){
            if(!(downloadRequest instanceof LoadRequest)){
                downloadRequest.setStatus(Request.Status.COMPLETED);
            }
            if(downloadRequest.getDownloadListener() != null){
                if(downloadResult.getResult().getClass().isAssignableFrom(File.class)){
                    downloadRequest.getDownloadListener().onCompleted((File) downloadResult.getResult(), downloadResult.isFromNetwork()? DownloadListener.From.NETWORK: DownloadListener.From.LOCAL_CACHE);
                }else{
                    downloadRequest.getDownloadListener().onCompleted((byte[]) downloadResult.getResult(), downloadResult.isFromNetwork()? DownloadListener.From.NETWORK: DownloadListener.From.LOCAL_CACHE);
                }
            }
        }else{
            if(!(downloadRequest instanceof LoadRequest)){
                downloadRequest.setStatus(Request.Status.FAILED);
            }
            if(downloadRequest.getDownloadListener() != null){
                downloadRequest.getDownloadListener().onFailed(null);
            }
        }
	}
}
