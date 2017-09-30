package com.apptuned.betadvisor;

/**
 * Created by davies on 9/26/17.
 */

import com.loopj.android.http.*;

public class RestAdapter {

    private static final String BASE_URL = "http://ec2-18-220-210-214.us-east-2.compute.amazonaws.com/"; // Machine localhost for local testing

    private static AsyncHttpClient client = new AsyncHttpClient();


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static AsyncHttpClient getClient() {
        return client;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}

