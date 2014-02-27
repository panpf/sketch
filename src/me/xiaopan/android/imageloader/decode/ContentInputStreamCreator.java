/*
 * Copyright 2014 Peng fei Pan
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

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

public class ContentInputStreamCreator implements InputStreamCreator {
	private String contentUri;
	private Context context;
	
	public ContentInputStreamCreator(Context context, String contentUri) {
		this.context = context;
		this.contentUri = contentUri;
	}

	@Override
	public InputStream onCreateInputStream() {
		try{
			ContentResolver res = context.getContentResolver();
			Uri uri = Uri.parse(contentUri);
			return res.openInputStream(uri);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			return null;
		}
	}
}
