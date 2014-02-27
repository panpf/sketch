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

package me.xiaopan.android.imageloader.decode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import me.xiaopan.android.imageloader.util.LoadIOUtils;

public class FileInputStreamCreator implements InputStreamCreator {
	private File file;
	
	public FileInputStreamCreator(File file) {
		this.file = file;
	}

	@Override
	public InputStream onCreateInputStream() {
		file.setLastModified(System.currentTimeMillis());
		try {
			return new BufferedInputStream(new FileInputStream(file), LoadIOUtils.BUFFER_SIZE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
