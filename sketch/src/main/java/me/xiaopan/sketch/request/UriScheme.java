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

package me.xiaopan.sketch.request;

/**
 * 支持的协议类型
 */
public enum UriScheme {
    NET("http://", "https://") {
        @Override
        public String createUri(String uri) {
            return uri;
        }

        @Override
        public String crop(String uri) {
            return uri;
        }
    },

    FILE("/", "file://") {
        @Override
        public String createUri(String uri) {
            return uri;
        }

        @Override
        public String crop(String uri) {
            String uriPrefix = getUriPrefix();
            if (uri.startsWith(uriPrefix)) {
                return uri;
            }

            uriPrefix = getSecondaryUriPrefix();
            if (uri.startsWith(uriPrefix)) {
                return uri.substring(uriPrefix.length());
            }

            return null;
        }
    },

    CONTENT("content://") {
        @Override
        public String createUri(String uri) {
            return uri;
        }

        @Override
        public String crop(String uri) {
            return uri;
        }
    },

    ASSET("asset://") {
        @Override
        public String createUri(String content) {
            if (content == null || "".equals(content.trim())) {
                return null;
            }
            return getUriPrefix() + content;
        }

        @Override
        public String crop(String uri) {
            String uriPrefix = getUriPrefix();
            if (uri.startsWith(uriPrefix)) {
                return uri.substring(uriPrefix.length());
            }

            uriPrefix = getSecondaryUriPrefix();
            if (uri.startsWith(uriPrefix)) {
                return uri.substring(uriPrefix.length());
            }

            return null;
        }
    },

    DRAWABLE("drawable://") {
        @Override
        public String createUri(String content) {
            if (content == null || "".equals(content.trim())) {
                return null;
            }
            return getUriPrefix() + content;
        }

        @Override
        public String crop(String uri) {
            String uriPrefix = getUriPrefix();
            if (uri.startsWith(uriPrefix)) {
                return uri.substring(uriPrefix.length());
            }

            uriPrefix = getSecondaryUriPrefix();
            if (uri.startsWith(uriPrefix)) {
                return uri.substring(uriPrefix.length());
            }

            return null;
        }
    };

    private String uriPrefix;
    private String secondaryUriPrefix;

    UriScheme(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    UriScheme(String uriPrefix, String secondaryUriPrefix) {
        this.uriPrefix = uriPrefix;
        this.secondaryUriPrefix = secondaryUriPrefix;
    }

    public abstract String createUri(String content);

    public abstract String crop(String uri);

    public String getUriPrefix() {
        return uriPrefix;
    }

    public String getSecondaryUriPrefix() {
        return secondaryUriPrefix;
    }

    public static UriScheme valueOfUri(String uri) {
        if (uri != null && !"".equals(uri.trim())) {
            for (UriScheme uriScheme : values()) {
                if ((uriScheme.uriPrefix != null && uri.startsWith(uriScheme.uriPrefix))
                        || (uriScheme.secondaryUriPrefix != null && uri.startsWith(uriScheme.secondaryUriPrefix))) {
                    return uriScheme;
                }
            }
        }
        return null;
    }
}
