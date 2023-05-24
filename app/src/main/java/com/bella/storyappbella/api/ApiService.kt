package com.bella.storyappbella.api

import com.bella.storyappbella.api.respon.ListStoryRespon
import com.bella.storyappbella.api.respon.LoginRespon
import com.bella.storyappbella.api.respon.StoryDetailRespon
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

data class RegistRespon(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

interface ApiService {
    @FormUrlEncoded
    @POST("/v1/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegistRespon>

    @FormUrlEncoded
    @POST("/v1/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginRespon>

    @GET("/v1/stories")
    fun getListStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 10,
        @Query("location") location: Int? = 0,
    ) : Call<ListStoryRespon>

    @GET("/v1/stories/{id}")
    fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : Call<StoryDetailRespon>

    @Multipart
    @POST("/v1/stories")
    fun postStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<RegistRespon>

    @GET("stories")
    fun getMap(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1,
    ) : Call<ListStoryRespon>
}