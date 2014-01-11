/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package me.xiaoapn.easy.imageloader.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Provides I/O operations
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public final class IOUtils {

	public static final int BUFFER_SIZE = 8 * 1024; // 8 KB 

	private IOUtils() {
	}

	/**
	 * 关闭流
	 * @param closeable
	 */
	public static void close(Closeable closeable) {
		if(closeable != null){
			if(closeable instanceof OutputStream){
				try {
					((OutputStream) closeable).flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
