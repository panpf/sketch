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

package me.xiaopan.android.imageloader.task.download;

import java.io.File;

import me.xiaopan.android.imageloader.task.Task;

/**
 * 下载任务
 */
public class DownloadTask extends Task{
	private DownloadRequest downloadRequest;
	
	public DownloadTask(DownloadRequest downloadRequest) {
		super(downloadRequest, new DownloadCallable(downloadRequest));
		this.downloadRequest = downloadRequest;
	}
	
	@Override
	protected void done() {
		if(isCancelled()){
            if(downloadRequest.getDownloadListener() != null){
                downloadRequest.getDownloadListener().onCancel();
            }
		}else{
			Object result = null;
			try {
				result = get();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if(downloadRequest.getDownloadListener() != null){
				if(result != null){
					if(result.getClass().isAssignableFrom(File.class)){
						downloadRequest.getDownloadListener().onSuccess((File) result);
					}else{
						downloadRequest.getDownloadListener().onSuccess((byte[]) result);
					}
				}else{
					downloadRequest.getDownloadListener().onFailure();
				}
			}
        }
	}
}
