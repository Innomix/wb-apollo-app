package me.fmtech.apollo.model.http;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import me.fmtech.apollo.model.http.api.MyApis;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class RetrofitHelper implements HttpHelper {

    private MyApis mMyApiService;

    @Inject
    public RetrofitHelper(MyApis myApiService) {
        this.mMyApiService = myApiService;
    }

    private Map<String, Object> getHeader(String url, Map<String, Object> headers) {
        if (null == headers) {
            headers = new HashMap<>(1);
        }

        return headers;
    }

    @Override
    public Flowable<ResponseBody> get(String url) {
        return mMyApiService.get(url);
    }

    @Override
    public Flowable<Response<String>> get(String url, Map<String, Object> headers) {
        return mMyApiService.get(url, getHeader(url, headers));
    }

    @Override
    public Flowable<Response<byte[]>> getResponseBody(String url, Map<String, Object> headers) {
        return mMyApiService.getResponseBody(url, getHeader(url, headers));
    }

    @Override
    public Flowable<Response<String>> getStream(String url, Map<String, Object> headers) {
        return mMyApiService.getStream(url, getHeader(url, headers));
    }

    @Override
    public Flowable<Response<String>> put(String url, Map<String, Object> headers) {
        return mMyApiService.put(url, headers);
    }

    @Override
    public Flowable<Response<String>> put(String url, Map<String, Object> headers, RequestBody body) {
        return mMyApiService.put(url, headers, body);
    }

    @Override
    public Flowable<Response<String>> delete(String url, Map<String, Object> headers) {
        return mMyApiService.delete(url, headers);
    }

    @Override
    public Flowable<Response<String>> post(String url, Map<String, Object> headers, Map<String, Object> body) {
        return mMyApiService.post(url, getHeader(url, headers), body);
    }

    @Override
    public Flowable<Response<String>> postBody(String url, Map<String, Object> headers, RequestBody body) {
        return mMyApiService.postBody(url, getHeader(url, headers), body);
    }

    @Override
    public Flowable<Response<String>> postFile(String url, Map<String, Object> headers, MultipartBody.Part body) {
        return mMyApiService.postFile(url, headers, body);
    }

}
