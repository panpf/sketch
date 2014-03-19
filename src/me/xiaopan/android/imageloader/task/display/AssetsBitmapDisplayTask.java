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

import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.android.imageloader.Configuration;
import me.xiaopan.android.imageloader.decode.AssetsInputStreamCreator;
import me.xiaopan.android.imageloader.decode.InputStreamCreator;
import me.xiaopan.android.imageloader.task.BitmapLoadCallable;
import me.xiaopan.android.imageloader.task.BitmapLoadTask;
import me.xiaopan.android.imageloader.util.Scheme;

public class AssetsBitmapDisplayTask extends  BitmapLoadTask {
	
	public AssetsBitmapDisplayTask(DisplayRequest displayRequest, ReentrantLock reentrantLock, Configuration configuration) {
		super(displayRequest, configuration, new AssetsBitmapLoadCallable(displayRequest, reentrantLock, configuration));
	}
	
	private static class AssetsBitmapLoadCallable extends BitmapLoadCallable {
		
		public AssetsBitmapLoadCallable(DisplayRequest displayRequest, ReentrantLock reentrantLock, Configuration configuration) {
			super(displayRequest, reentrantLock, configuration);
		}

		@Override
		public InputStreamCreator getInputStreamCreator() {
			return new AssetsInputStreamCreator(configuration.getContext(), Scheme.ASSETS.crop(displayRequest.getImageUri()));
		}

		@Override
		public void onFailed() {
			
		}
	}
}
