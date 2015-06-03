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

package me.xiaopan.sketch;

/**
 * 支持的协议类型
 */
public enum UriScheme {
	HTTP("http://"){
        @Override
        public String createUri(String uri){
            return uri;
        }

        @Override
        public String crop(String uri) {
            return uri;
        }
    },

    HTTPS("https://"){
        @Override
        public String createUri(String uri){
            return uri;
        }

        @Override
        public String crop(String uri) {
            return uri;
        }
    },

    FILE("/"){
        @Override
        public String createUri(String uri){
            return uri;
        }

        @Override
        public String crop(String uri) {
            return uri;
        }
    },

    CONTENT("content://"){
        @Override
        public String createUri(String uri){
            return uri;
        }

        @Override
        public String crop(String uri) {
            return uri;
        }
    },

    ASSET("asset://"){
        @Override
        public String createUri(String content){
            if(content == null || "".equals(content.trim())){
                return null;
            }
            return getUriPrefix()+content;
        }

        @Override
        public String crop(String uri) {
            if (!uri.startsWith(getUriPrefix())) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, getUriPrefix()));
            }
            return uri.substring(getUriPrefix().length());
        }
    },

    DRAWABLE("drawable://"){
        @Override
        public String createUri(String content){
            if(content == null || "".equals(content.trim())){
                return null;
            }
            return getUriPrefix()+content;
        }

        @Override
        public String crop(String uri) {
            if (!uri.startsWith(getUriPrefix())) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, getUriPrefix()));
            }
            return uri.substring(getUriPrefix().length());
        }
    };

	private String uriPrefix;

	UriScheme(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}

	public abstract String createUri(String content);

	public abstract String crop(String uri);

    public String getUriPrefix() {
        return uriPrefix;
    }

    public static UriScheme valueOfUri(String uri) {
		if (uri != null && !"".equals(uri.trim())) {
			for (UriScheme uriScheme : values()) {
				if (uri.startsWith(uriScheme.getUriPrefix())) {
					return uriScheme;
				}
			}
		}
		return null;
	}
}
