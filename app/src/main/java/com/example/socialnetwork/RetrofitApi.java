package com.example.socialnetwork;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitApi
{
    @GET
    Call<MsgModel>  getMessage(@Url String Url);
}
