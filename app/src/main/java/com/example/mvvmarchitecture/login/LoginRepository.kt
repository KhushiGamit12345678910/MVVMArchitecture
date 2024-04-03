package com.example.mvvmarchitecture.login

import com.example.mvvmarchitecture.apifiles.ApiInterface
import com.example.mvvmarchitecture.models.DataModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response
import javax.inject.Inject


class LoginRepository @Inject constructor(var apiInterface: ApiInterface){
    suspend fun userLogin(hashMap: HashMap<String,String>):Response<DataModel> {
        return apiInterface.userLogin(hashMap)

    }
}