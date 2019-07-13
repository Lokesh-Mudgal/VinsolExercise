package com.lokeshmudgal06.vinsolproject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("/api/schedule")
    Call<List<MeetingModel>> getMeetingList(@Query("date") String date);
}
