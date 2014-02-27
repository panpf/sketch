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

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class AssetsInputStreamCreator implements InputStreamCreator {
	private String assetsFilePath;
	private Context context;
	
	public AssetsInputStreamCreator(Context context, String assetsFilePath) {
		this.context = context;
		this.assetsFilePath = assetsFilePath;
	}

	@Override
	public InputStream onCreateInputStream() {
		try{
			return context.getAssets().open(assetsFilePath);
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
}
