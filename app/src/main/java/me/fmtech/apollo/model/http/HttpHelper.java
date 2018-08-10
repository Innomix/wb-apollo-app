package me.fmtech.apollo.model.http;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public interface HttpHelper {
    Flowable<ResponseBody> get(String url);

    Flowable<Response<String>> get(String url, Map<String, Object> headers);

    Flowable<Response<byte[]>> getResponseBody(String url, Map<String, Object> headers);

    Flowable<Response<String>> getStream(String url, Map<String, Object> headers);

    Flowable<Response<String>> put(String url, Map<String, Object> headers);

    Flowable<Response<String>> put(String url, Map<String, Object> headers, RequestBody body);

    Flowable<Response<String>> delete(String url, Map<String, Object> headers);

    Flowable<Response<String>> post(String url, Map<String, Object> headers, Map<String, Object> body);

    Flowable<Response<String>> postBody(String url, Map<String, Object> headers, RequestBody body);

    Flowable<Response<String>> postFile(String url, Map<String, Object> headers, MultipartBody.Part body);

}