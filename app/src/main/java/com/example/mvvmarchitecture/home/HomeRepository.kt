package com.example.mvvmarchitecture.home

import com.example.mvvmarchitecture.apifiles.ApiInterface
import com.example.mvvmarchitecture.models.DataModel
import retrofit2.Response
import javax.inject.Inject

class HomeRepository @Inject constructor(var apiInterface: ApiInterface) {
    suspend fun userProfile(hashMap: HashMap<String,String>): Response<DataModel> {
        return apiInterface.userProfile(hashMap)
    }
}