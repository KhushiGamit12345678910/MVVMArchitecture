package com.example.mvvmarchitecture.apifiles

import com.example.mvvmarchitecture.models.DataModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface ApiInterface {

    @Multipart
    @POST("trainee/api/register.php")
    suspend fun userRegister(
        @Part image: MultipartBody.Part,
        @PartMap hashMap: HashMap<String, RequestBody>
    ): Response<DataModel>

    @FormUrlEncoded
    @POST("trainee/api/login.php")
    suspend fun userLogin(@FieldMap hashMap: HashMap<String,String>): Response<DataModel>

    @FormUrlEncoded
    @POST("trainee/api/profile.php")
    suspend fun userProfile(@FieldMap hashMap: HashMap<String, String>): Response<DataModel>
}