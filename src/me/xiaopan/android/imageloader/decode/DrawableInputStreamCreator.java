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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;

public class DrawableInputStreamCreator implements InputStreamCreator {
	private String drawableIdString;
	private Context context;
	
	public DrawableInputStreamCreator(Context context, String drawableIdString) {
		this.context = context;
		this.drawableIdString = drawableIdString;
	}

	@Override
	public InputStream onCreateInputStream() {
		BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(Integer.parseInt(drawableIdString));
		Bitmap bitmap = drawable.getBitmap();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, os);
		return new ByteArrayInputStream(os.toByteArray());
	}
}
