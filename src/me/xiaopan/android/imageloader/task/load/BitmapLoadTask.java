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

package me.xiaopan.android.imageloader.task.load;

import me.xiaopan.android.imageloader.task.Task;
import android.graphics.Bitmap;

public abstract class BitmapLoadTask extends Task {
	private LoadRequest loadRequest;
	
	public BitmapLoadTask(LoadRequest loadRequest, BitmapLoadCallable bitmapLoadCallable) {
		super(loadRequest, bitmapLoadCallable);
		this.loadRequest = loadRequest;
	}
	
	@Override
	protected void done() {
		if(!isCancelled()){
			Bitmap bitmap = null;
			try {
				bitmap = (Bitmap) get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(loadRequest.getLoadListener() != null){
				if(bitmap != null && !bitmap.isRecycled()){
					loadRequest.getLoadListener().onComplete(bitmap);
				}else{
					loadRequest.getLoadListener().onFailure();
				}
			}
		}else{
			if(loadRequest.getLoadListener() != null){
				loadRequest.getLoadListener().onCancel();
			}
		}
	}
}
