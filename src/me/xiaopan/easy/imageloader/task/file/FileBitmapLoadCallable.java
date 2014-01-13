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

package me.xiaopan.easy.imageloader.task.file;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.easy.imageloader.Configuration;
import me.xiaopan.easy.imageloader.decode.FileInputStreamCreator;
import me.xiaopan.easy.imageloader.decode.InputStreamCreator;
import me.xiaopan.easy.imageloader.task.BitmapLoadCallable;
import me.xiaopan.easy.imageloader.task.Request;
import me.xiaopan.easy.imageloader.util.Scheme;

public class FileBitmapLoadCallable extends BitmapLoadCallable {
	
	public FileBitmapLoadCallable(Request request, ReentrantLock reentrantLock, Configuration configuration) {
		super(request, reentrantLock, configuration);
	}

	@Override
	public InputStreamCreator getInputStreamCreator() {
		return new FileInputStreamCreator(new File(Scheme.FILE.crop(request.getImageUri())));
	}

	@Override
	public void onFailed() {
		
	}
}
