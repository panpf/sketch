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

package me.xiaopan.android.spear.util;


/**
 * 支持的协议类型
 */
public enum Scheme {
	HTTP("http"),

    HTTPS("https"),

    FILE("file"),

    CONTENT("content"){
        @Override
        public String createUri(String uri){
            return uri;
        }
    },

    ASSETS("assets"),

    DRAWABLE("drawable"),

    UNKNOWN("");

	private String scheme;
	private String uriPrefix;

	Scheme(String scheme) {
		this.scheme = scheme;
		uriPrefix = scheme + "://";
	}
	
	private boolean belongsTo(String uri) {
		return uri.startsWith(uriPrefix);
	}

	public String createUri(String content){
        if(content == null || "".equals(content.trim())){
            return null;
        }
		return uriPrefix+content;
	}

	public String crop(String uri) {
		if (!belongsTo(uri)) {
			throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
		}
		return uri.substring(uriPrefix.length());
	}

	public static Scheme valueOfUri(String uri) {
		if (uri != null) {
			for (Scheme s : values()) {
				if (s.belongsTo(uri)) {
					return s;
				}
			}
		}
		return UNKNOWN;
	}
}
