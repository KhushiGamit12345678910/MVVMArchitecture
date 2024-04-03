package com.example.mvvmarchitecture.home

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvmarchitecture.R
import com.example.mvvmarchitecture.apifiles.ApiInterface
import com.example.mvvmarchitecture.apifiles.RetrofitClient
import com.example.mvvmarchitecture.const.ID
import com.example.mvvmarchitecture.const.userIDParams
import com.example.mvvmarchitecture.models.DataModel
import com.example.mvvmarchitecture.sharedpref.Sharedpref
import com.example.mvvmarchitecture.ui.Resource
import com.example.mvvmarchitecture.ui.ResponseCodeCheck
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(var application: Application) : ViewModel(){

    @Inject
    lateinit var retrofit: Retrofit
    @Inject
    lateinit var sharedPreferences: Sharedpref

    var responseCodeCheck: ResponseCodeCheck = ResponseCodeCheck()
    private var mutableData: MutableLiveData<Resource<DataModel>> = MutableLiveData()
    var liveData: LiveData<Resource<DataModel>> = mutableData

    //lateinit var sharedPreferences: Sharedpref
    lateinit var id: String

    fun userProfile() {

        //sharedPreferences = SharedPreferences(application)
        id = sharedPreferences.getString(ID, "")!!

        val hashMap: HashMap<String, String> = HashMap()
        hashMap.apply {
            put(userIDParams,id)
        }

        val apiInterface= retrofit.create(ApiInterface::class.java)
        val homeRepository = HomeRepository(apiInterface)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository : Response<DataModel> = homeRepository.userProfile(hashMap)
                mutableData.postValue(responseCodeCheck.getResponseResult(repository))

            }catch (e: Exception){
                mutableData.postValue(Resource.error(application.getString(R.string.something_went_wrong), null))
            }
        }

        /*RetrofitClient.retrofit.userProfile(hashMap).enqueue(object : Callback<DataModel> {
            override fun onResponse(call: Call<DataModel>, response: Response<DataModel>) {

                if (response.body() !== null) {
                    try {
                        mutableData.postValue(
                            responseCodeCheck.getResponseResult(
                                Response.success(
                                    response.body()
                                )
                            )
                        )
                    } catch (e: Exception) {
                        mutableData.postValue(Resource.error("enter Details", null))
                    }
                }else{
                    mutableData.postValue(Resource.error("enter Details", null))
                }
            }

            override fun onFailure(call: Call<DataModel>, t: Throwable) {
                mutableData.postValue(Resource.error(t.message.toString(), null))
                Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_SHORT).show()
            }

        })*/
    }


}