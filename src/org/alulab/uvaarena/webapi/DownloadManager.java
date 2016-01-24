/*
 * Copyright (c) 2016, Sudipto Chandra
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package org.alulab.uvaarena.webapi;

import java.io.File;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * This class handles all downloading tasks.
 */
public final class DownloadManager {

    private static final int DEFAULT_MAX_TOTAL = 20;

    private static final CacheConfig mCacheConfig;
    private static final RequestConfig mRequestConfig;
    private static final CloseableHttpClient mClient;
    private static final PoolingHttpClientConnectionManager mHttpPool;

    /**
     * Initializes this instance of download manager
     */
    static {

        mHttpPool = new PoolingHttpClientConnectionManager();
        mHttpPool.setMaxTotal(DEFAULT_MAX_TOTAL);

        mCacheConfig = CacheConfig.custom()
                .setMaxCacheEntries(1000)
                .setMaxObjectSize(8192)
                .build();

        mRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setConnectTimeout(30000)
                .setSocketTimeout(30000)
                .build();

        mClient = CachingHttpClients.custom()
                .setCacheConfig(mCacheConfig)
                .setDefaultRequestConfig(mRequestConfig)
                .setConnectionManager(mHttpPool)
                .build();
    }

    /**
     * Gets the client to make HTTP connection and download data.
     *
     * @return
     */
    public static CloseableHttpClient getHttpClient() {
        return mClient;
    }

    /**
     * Gets the request configuration for connection.
     *
     * @return
     */
    public static RequestConfig getRequestConfig() {
        return mRequestConfig;
    }

    /**
     * Sets the number of maximum total concurrent connections. Default value is
     * 20.
     *
     * @param maxTotal
     */
    public static void setMaxTotal(int maxTotal) {
        mHttpPool.setMaxTotal(maxTotal);
    }

    /**
     * Gets the number of maximum total concurrent connections.
     *
     * @return
     */
    public static int getMaxTotal() {
        return mHttpPool.getMaxTotal();
    }

    /**
     * Sets the number of maximum concurrent connection to a specific host
     *
     * @param host
     * @param maxConn
     */
    public static void setMaxPerRoute(String host, int maxConn) {
        mHttpPool.setMaxPerRoute(new HttpRoute(new HttpHost(host)), maxConn);
    }

    /**
     * Creates a new download string task and returns its instance. It does not
     * starts the download. Call startDownload() method to start the download.
     *
     * @param url URL to download
     * @return
     */
    public static DownloadString downloadString(String url) {
        return new DownloadString(url);
    }

    /**
     * Creates a new download string task and returns its instance. It does not
     * starts the download. Call startDownload() method to start the download.
     *
     * @param url URL to download
     * @param taskMonitor TaskMonitor object to monitor download progress.
     * @return
     */
    public static DownloadString downloadString(String url, TaskMonitor taskMonitor) {
        return (DownloadString) downloadString(url).addTaskMonitor(taskMonitor);
    }

    /**
     * Creates a new download file task and returns its instance. It does not
     * starts the download. Call startDownload() method to start the download.
     *
     * @param url URL to download
     * @param storeFile File to store downloaded data
     * @return
     */
    public static DownloadFile downloadFile(String url, File storeFile) {
        return new DownloadFile(url, storeFile);
    }

    /**
     * Creates a new download file task and returns its instance. It does not
     * starts the download. Call startDownload() method to start the download.
     *
     * @param url URL to download
     * @param storeFile File to store downloaded data
     * @param taskMonitor TaskMonitor object to monitor download progress.
     * @return
     */
    public static DownloadFile downloadFile(String url, File storeFile, TaskMonitor taskMonitor) {
        return (DownloadFile) downloadFile(url, storeFile).addTaskMonitor(taskMonitor);
    }

}
