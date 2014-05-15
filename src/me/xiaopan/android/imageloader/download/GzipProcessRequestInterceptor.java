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

package me.xiaopan.android.imageloader.download;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class GzipProcessRequestInterceptor implements HttpRequestInterceptor{
	/**
	 * 头字段 - 接受的编码
	 */
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	
	/**
	 * 编码 - gzip
	 */
	public static final String ENCODING_GZIP = "gzip";
	
	@Override
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
		//如果请求头中没有HEADER_ACCEPT_ENCODING属性就添加进去
    	if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
            request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
        }
	}
}
