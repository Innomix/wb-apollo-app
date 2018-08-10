package me.fmtech.apollo.model.http.api;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface MyApis {

    String HOST = "https://www.google.com/";

    @GET
    Flowable<ResponseBody> get(@Url String url);

    @GET
    Flowable<ResponseBody> getBody(@Url String url, @HeaderMap Map<String, Object> headers);

    @GET
    Flowable<Response<String>> get(@Url String url, @HeaderMap Map<String, Object> headers);

    @GET
    Flowable<Response<byte[]>> getResponseBody(@Url String url, @HeaderMap Map<String, Object> headers);

    @PUT
    Flowable<Response<String>> put(@Url String url, @HeaderMap Map<String, Object> headers);

    @PUT
    Flowable<Response<String>> put(@Url String url, @HeaderMap Map<String, Object> headers, @Body RequestBody body);

    @DELETE
    Flowable<Response<String>> delete(@Url String url, @HeaderMap Map<String, Object> headers);

    @GET
    @Streaming
    Flowable<Response<String>> getStream(@Url String url, @HeaderMap Map<String, Object> headers);

    @FormUrlEncoded
    @POST
    Flowable<Response<String>> post(@Url String url, @HeaderMap Map<String, Object> headers,
                                    @FieldMap Map<String, Object> body);

    @POST
    Flowable<Response<String>> postBody(@Url String url, @HeaderMap Map<String, Object> headers,
                                        @Body RequestBody body);

    @Multipart
    @POST
    Flowable<Response<String>> postFile(@Url String url, @HeaderMap Map<String, Object> headers,
                                        @Part MultipartBody.Part file);

}
