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

import me.xiaopan.android.imageloader.decode.ContentInputStreamCreator;
import me.xiaopan.android.imageloader.decode.InputStreamCreator;
import me.xiaopan.android.imageloader.task.BitmapLoadCallable;
import me.xiaopan.android.imageloader.task.BitmapLoadTask;

public class ContentBitmapDisplayTask extends  BitmapLoadTask {
	
	public ContentBitmapDisplayTask(DisplayRequest displayRequest) {
		super(displayRequest, new ContentBitmapLoadCallable(displayRequest));
	}
	
	private static class ContentBitmapLoadCallable extends BitmapLoadCallable {
		
		public ContentBitmapLoadCallable(DisplayRequest displayRequest) {
			super(displayRequest);
		}

		@Override
		public InputStreamCreator getInputStreamCreator() {
			return new ContentInputStreamCreator(displayRequest.getConfiguration().getContext(), displayRequest.getImageUri());
		}

		@Override
		public void onFailed() {
			
		}
	}
}
