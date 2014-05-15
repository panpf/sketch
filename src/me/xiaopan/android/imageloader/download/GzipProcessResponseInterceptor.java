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

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

public class GzipProcessResponseInterceptor implements HttpResponseInterceptor{
    
	@Override
    public void process(HttpResponse response, HttpContext context) {
        final HttpEntity entity = response.getEntity();
        if(entity != null) {
        	final Header encoding = entity.getContentEncoding();
        	if (encoding != null) {
        		for (HeaderElement element : encoding.getElements()) {
        			if (element.getName().equalsIgnoreCase(GzipProcessRequestInterceptor.ENCODING_GZIP)) {
        				response.setEntity(new InflatingEntity(entity));
        				break;
        			}
        		}
        	}
        }
    }
}
