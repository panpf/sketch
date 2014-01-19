/*
 * Copyright 2013 Peng fei Pan
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

package me.xiaopan.android.imageloader.task.file;

import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.android.imageloader.Configuration;
import me.xiaopan.android.imageloader.task.BitmapLoadTask;
import me.xiaopan.android.imageloader.task.Request;

public class FileBitmapLoadTask extends  BitmapLoadTask {
	
	public FileBitmapLoadTask(Request request, ReentrantLock reentrantLock, Configuration configuration) {
		super(request, configuration, new FileBitmapLoadCallable(request, reentrantLock, configuration));
	}
}
