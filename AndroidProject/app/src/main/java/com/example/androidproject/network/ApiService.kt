package network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("api.php")
    fun createEntity(
        @Part("title") title: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<EntityResponse>

    @GET("api.php")
    fun getEntities(): Call<List<Entity>>

    @Multipart
    @PUT("api.php")
    fun updateEntity(
        @Part("id") id: RequestBody,
        @Part("title") title: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<EntityResponse>
}
