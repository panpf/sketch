package me.xiaopan.android.imageloader.http;

import org.apache.http.client.HttpClient;

public interface HttpClientCreator {
    public static final int DEFAULT_CONNECTION_TIME_OUT = 20000;
    public static final int DEFAULT_MAX_CONNECTIONS = 10;
    public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;

    public HttpClient onCreatorHttpClient();
}
