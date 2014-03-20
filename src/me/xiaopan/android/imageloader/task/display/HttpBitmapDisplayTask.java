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

import java.io.File;

import me.xiaopan.android.imageloader.decode.ByteArrayInputStreamCreator;
import me.xiaopan.android.imageloader.decode.FileInputStreamCreator;
import me.xiaopan.android.imageloader.decode.InputStreamCreator;
import me.xiaopan.android.imageloader.task.download.DownloadRequest.DownloadListener;

public class HttpBitmapDisplayTask extends  BitmapDisplayTask {
	
	public HttpBitmapDisplayTask(DisplayRequest displayRequest) {
		super(displayRequest, new HttpBitmapLoadCallable(displayRequest));
	}
	
	private static class HttpBitmapLoadCallable extends BitmapDisplayCallable {
		private DisplayRequest displayRequest;
		private InputStreamCreator inputStreamCreator = null;
		
		public HttpBitmapLoadCallable(DisplayRequest displayRequest) {
			super(displayRequest);
			this.displayRequest = displayRequest;
		}

		@Override
		public InputStreamCreator getInputStreamCreator() {
			if(inputStreamCreator == null){
				if(displayRequest.getDisplayOptions().isEnableDiskCache()){
					if(displayRequest.getCacheFile() != null && displayRequest.getCacheFile().exists()){
						inputStreamCreator = new FileInputStreamCreator(displayRequest.getCacheFile());
					}else{
						inputStreamCreator = getNetInputStreamCreator(displayRequest);
					}
				}else{
					inputStreamCreator = getNetInputStreamCreator(displayRequest);
				}
			}
			return inputStreamCreator;
		}

		@Override
		public void onFailed() {
			if(inputStreamCreator instanceof FileInputStreamCreator && displayRequest.getCacheFile() != null && displayRequest.getCacheFile().exists()){
				displayRequest.getCacheFile().delete();
			}
		}
		
		/**
	     * 获取网络输入流监听器
	     * @param displayRequest
	     * @return
	     */
	    private InputStreamCreator getNetInputStreamCreator(DisplayRequest displayRequest){
	    	final NetInputStreamCreatorHolder holder = new NetInputStreamCreatorHolder();
	    	displayRequest.getConfiguration().getImageDownloader().execute(displayRequest, new DownloadListener() {
				@Override
				public void onFailed() {}
				
				@Override
				public void onComplete(final byte[] data) {
					holder.inputStreamCreator = new ByteArrayInputStreamCreator(data);
				}
				
				@Override
				public void onComplete(final File cacheFile) {
					holder.inputStreamCreator = new FileInputStreamCreator(cacheFile);
				}

				@Override
				public void onStart() {
					
				}

				@Override
				public void onUpdateProgress(long totalLength, long completedLength) {
					
				}
			});
	    	return holder.inputStreamCreator;
	    }
		
		private class NetInputStreamCreatorHolder{
			InputStreamCreator inputStreamCreator;
		}
	}
}
