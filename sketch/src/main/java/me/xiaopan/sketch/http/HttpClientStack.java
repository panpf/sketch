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

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import me.xiaopan.sketch.util.SketchUtils;

@SuppressWarnings("deprecation")
public class HttpClientStack implements HttpStack {
    private static final int DEFAULT_WAIT_TIMEOUT = 60 * 1000;   // 默认从连接池中获取连接的最大等待时间
    private static final int DEFAULT_MAX_ROUTE_CONNECTIONS = 400;    // 默认每个路由的最大连接数
    private static final int DEFAULT_MAX_CONNECTIONS = 800;  // 默认最大连接数
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8 * 1024;  // 默认Socket缓存大小

    protected String logName = "HttpClientStack";

    private int readTimeout = DEFAULT_READ_TIMEOUT;
    private int maxRetryCount = DEFAULT_MAX_RETRY_COUNT;
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private String userAgent;
    private Map<String, String> setExtraHeaders;
    private Map<String, String> addExtraHeaders;

    private DefaultHttpClient httpClient;

    public HttpClientStack() {
        BasicHttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setTimeout(httpParams, DEFAULT_WAIT_TIMEOUT);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(DEFAULT_MAX_ROUTE_CONNECTIONS));
        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSoTimeout(httpParams, readTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeout);
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams);
        httpClient.addRequestInterceptor(new GzipProcessRequestInterceptor());
        httpClient.addResponseInterceptor(new GzipProcessResponseInterceptor());
    }

    @Override
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    @Override
    public HttpClientStack setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
        return this;
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public HttpClientStack setConnectTimeout(int connectTimeout) {
        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeout);

        this.connectTimeout = connectTimeout;
        return this;
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public HttpClientStack setReadTimeout(int readTimeout) {
        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setSoTimeout(httpParams, readTimeout);

        this.readTimeout = readTimeout;
        return this;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public HttpClientStack setUserAgent(String userAgent) {
        HttpParams httpParams = httpClient.getParams();
        HttpProtocolParams.setUserAgent(httpParams, userAgent);

        this.userAgent = userAgent;
        return this;
    }

    @Override
    public Map<String, String> getExtraHeaders() {
        return setExtraHeaders;
    }

    @Override
    public HttpClientStack setExtraHeaders(Map<String, String> extraHeaders) {
        this.setExtraHeaders = extraHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAddExtraHeaders() {
        return addExtraHeaders;
    }

    @Override
    public HttpClientStack addExtraHeaders(Map<String, String> extraHeaders) {
        this.addExtraHeaders = extraHeaders;
        return this;
    }

    @Override
    public boolean canRetry(Throwable throwable) {
        return throwable instanceof ConnectTimeoutException;
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(logName)
                .append("(")
                .append("maxRetryCount").append("=").append(maxRetryCount)
                .append(",")
                .append("connectTimeout").append("=").append(connectTimeout)
                .append(",")
                .append("readTimeout").append("=").append(readTimeout)
                .append(",")
                .append("userAgent").append("=").append(userAgent)
                .append(")");
    }

    @Override
    public ImageHttpResponse getHttpResponse(String uri) throws IOException {
        HttpUriRequest httpUriRequest = new HttpGet(uri);

        if(userAgent != null){
            httpUriRequest.setHeader("User-Agent", userAgent);
        }

        if (addExtraHeaders != null && addExtraHeaders.size() > 0) {
            for (Map.Entry<String, String> entry : addExtraHeaders.entrySet()) {
                httpUriRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (setExtraHeaders != null && setExtraHeaders.size() > 0) {
            for (Map.Entry<String, String> entry : setExtraHeaders.entrySet()) {
                httpUriRequest.setHeader(entry.getKey(), entry.getValue());
            }
        }

        processRequest(uri, httpUriRequest);

        HttpResponse httpResponse = httpClient.execute(httpUriRequest);
        return new HttpClientHttpResponse(httpResponse);
    }

    protected void processRequest(@SuppressWarnings("UnusedParameters") String uri,
                                  @SuppressWarnings("UnusedParameters") HttpUriRequest httpUriRequest) {
    }

    private static class GzipProcessRequestInterceptor implements HttpRequestInterceptor {
        /**
         * 头字段 - 接受的编码
         */
        static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

        /**
         * 编码 - gzip
         */
        static final String ENCODING_GZIP = "gzip";

        @Override
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            //如果请求头中没有HEADER_ACCEPT_ENCODING属性就添加进去
            if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
            }
        }
    }

    private static class GzipProcessResponseInterceptor implements HttpResponseInterceptor {

        @Override
        public void process(HttpResponse response, HttpContext context) {
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
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

        private static class InflatingEntity extends HttpEntityWrapper {
            InflatingEntity(HttpEntity wrapped) {
                super(wrapped);
            }

            @Override
            public InputStream getContent() throws IOException {
                return new GZIPInputStream(wrappedEntity.getContent());
            }

            @Override
            public long getContentLength() {
                return -1;
            }
        }
    }

    private static class HttpClientHttpResponse implements ImageHttpResponse {
        @SuppressWarnings("deprecation")
        private HttpResponse httpResponse;

        HttpClientHttpResponse(HttpResponse httpResponse) {
            this.httpResponse = httpResponse;
        }

        @Override
        public int getResponseCode() throws IOException {
            StatusLine statusLine = httpResponse.getStatusLine();
            return statusLine != null ? statusLine.getStatusCode() : -1;
        }

        @Override
        public String getResponseMessage() throws IOException {
            StatusLine statusLine = httpResponse.getStatusLine();
            return statusLine != null ? statusLine.getReasonPhrase() : null;
        }

        @Override
        public long getContentLength() {
            HttpEntity httpEntity = httpResponse.getEntity();
            return httpEntity != null ? httpResponse.getEntity().getContentLength() : -1;
        }

        @Override
        public String getResponseHeadersString() {
            Header[] headers = httpResponse.getAllHeaders();
            if (headers == null || headers.length == 0) {
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for (Header header : headers) {
                if (stringBuilder.length() != 1) {
                    stringBuilder.append(", ");
                }

                stringBuilder.append("{");
                stringBuilder.append(header.getName());
                stringBuilder.append(":");
                stringBuilder.append(header.getValue());
                stringBuilder.append("}");
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }

        @Override
        public InputStream getContent() throws IOException {
            HttpEntity httpEntity = httpResponse.getEntity();
            return httpEntity != null ? httpEntity.getContent() : null;
        }

        @Override
        public void releaseConnection() {
            try {
                SketchUtils.close(getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
