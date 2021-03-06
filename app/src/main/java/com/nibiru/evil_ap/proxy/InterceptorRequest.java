package com.nibiru.evil_ap.proxy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Nibiru on 2016-12-23.
 */

public class InterceptorRequest implements Interceptor{
    /**************************************CLASS FIELDS********************************************/
    /**************************************CLASS METHODS*******************************************/
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request editedReq = originalRequest.newBuilder()
                .removeHeader("Accept-Encoding") //no compression, okhttp does it transparently
                .removeHeader("Upgrade-Insecure-Requests") //stay in http
                .removeHeader("Strict-Transport-Security") //DON'T DO HSTS!
                .method(originalRequest.method(), originalRequest.body())
                .build();

        return chain.proceed(editedReq);
    }
}
