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

package me.xiaopan.sketch.http;

import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import me.xiaopan.sketch.util.SketchUtils;

public class HurlStack implements HttpStack {
    protected String logName = "HurlStack";

    private int readTimeout = DEFAULT_READ_TIMEOUT;
    private int maxRetryCount = DEFAULT_MAX_RETRY_COUNT;
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private String userAgent;
    private Map<String, String> setExtraHeaders;
    private Map<String, String> addExtraHeaders;

    @Override
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    @Override
    public HurlStack setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
        return this;
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public HurlStack setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public HurlStack setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public HurlStack setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    @Override
    public Map<String, String> getExtraHeaders() {
        return setExtraHeaders;
    }

    @Override
    public HurlStack setExtraHeaders(Map<String, String> extraHeaders) {
        this.setExtraHeaders = extraHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAddExtraHeaders() {
        return addExtraHeaders;
    }

    @Override
    public HurlStack addExtraHeaders(Map<String, String> extraHeaders) {
        this.addExtraHeaders = extraHeaders;
        return this;
    }

    @Override
    public boolean canRetry(Throwable throwable) {
        return throwable instanceof SocketTimeoutException;
    }

    @Override
    public String getKey() {
        return String.format("%s(maxRetryCount=%d,connectTimeout=%d,readTimeout=%d,userAgent=%s)",
                logName, maxRetryCount, connectTimeout, readTimeout, userAgent);
    }

    @Override
    public ImageHttpResponse getHttpResponse(String uri) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(uri).openConnection();

        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        connection.setDoInput(true);

        // HTTP connection reuse which was buggy pre-froyo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            connection.setRequestProperty("http.keepAlive", "false");
        }

        if (userAgent != null) {
            connection.setRequestProperty("User-Agent", userAgent);
        }

        if (addExtraHeaders != null && addExtraHeaders.size() > 0) {
            for (Map.Entry<String, String> entry : addExtraHeaders.entrySet()) {
                connection.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        if (setExtraHeaders != null && setExtraHeaders.size() > 0) {
            for (Map.Entry<String, String> entry : setExtraHeaders.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        processRequest(uri, connection);

        connection.connect();

        return new HttpUrlConnectionResponse(connection);
    }

    protected void processRequest(@SuppressWarnings("UnusedParameters") String uri,
                                  @SuppressWarnings("UnusedParameters") HttpURLConnection connection) {

    }

    private static class HttpUrlConnectionResponse implements ImageHttpResponse {
        private HttpURLConnection connection;

        HttpUrlConnectionResponse(HttpURLConnection connection) {
            this.connection = connection;
        }

        @Override
        public int getResponseCode() throws IOException {
            return connection.getResponseCode();
        }

        @Override
        public String getResponseMessage() throws IOException {
            return connection.getResponseMessage();
        }

        @Override
        public long getContentLength() {
            return connection.getContentLength();
        }

        @Override
        public String getResponseHeadersString() {
            Map<String, List<String>> headers = connection.getHeaderFields();
            if (headers == null || headers.size() == 0) {
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                if (stringBuilder.length() != 1) {
                    stringBuilder.append(", ");
                }

                stringBuilder.append("{");

                stringBuilder.append(entry.getKey());

                stringBuilder.append(":");

                List<String> values = entry.getValue();
                if (values.size() == 0) {
                    stringBuilder.append("");
                } else if (values.size() == 1) {
                    stringBuilder.append(values.get(0));
                } else {
                    stringBuilder.append(values.toString());
                }

                stringBuilder.append("}");
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }

        @Override
        public InputStream getContent() throws IOException {
            return connection.getInputStream();
        }

        @Override
        public void releaseConnection() {
            try {
                SketchUtils.close(getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean isContentChunked() {
            String transferEncodingValue = connection.getHeaderField("Transfer-Encoding");
            if (transferEncodingValue != null) {
                transferEncodingValue = transferEncodingValue.trim();
            }
            return transferEncodingValue != null && "chunked".equalsIgnoreCase(transferEncodingValue);
        }
    }
}
