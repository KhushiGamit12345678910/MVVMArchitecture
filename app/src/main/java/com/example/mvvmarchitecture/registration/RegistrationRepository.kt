package com.example.mvvmarchitecture.registration

import com.example.mvvmarchitecture.apifiles.ApiInterface
import com.example.mvvmarchitecture.models.DataModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject


class RegistrationRepository  @Inject constructor(var apiInterface: ApiInterface) {
    suspend fun userRegistration(image: MultipartBody.Part, hashMap: HashMap<String, RequestBody>): Response<DataModel> {
        return apiInterface.userRegister(image,hashMap)
    }
}