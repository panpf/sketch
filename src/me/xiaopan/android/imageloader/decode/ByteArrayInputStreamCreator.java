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

package me.xiaopan.android.imageloader.decode;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import me.xiaopan.android.imageloader.util.IOUtils;

public class ByteArrayInputStreamCreator implements InputStreamCreator {
	private byte[] data;
	
	public ByteArrayInputStreamCreator(byte[] data) {
		this.data = data;
	}

	@Override
	public InputStream onCreateInputStream() {
		return new BufferedInputStream(new ByteArrayInputStream(data), IOUtils.BUFFER_SIZE);
	}
}
