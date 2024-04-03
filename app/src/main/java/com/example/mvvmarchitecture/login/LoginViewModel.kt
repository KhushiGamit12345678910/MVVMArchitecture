package com.example.mvvmarchitecture.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvmarchitecture.R
import com.example.mvvmarchitecture.apifiles.ApiInterface
import com.example.mvvmarchitecture.apifiles.RetrofitClient
import com.example.mvvmarchitecture.const.*
import com.example.mvvmarchitecture.models.DataModel
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
class LoginViewModel @Inject constructor(var application: Application) : ViewModel() {

    @Inject
    lateinit var retrofit: Retrofit

    var email: String = ""
    var password: String = ""

    var emailError: String = ""
    var passwordError: String = ""


    var responseCodeCheck: ResponseCodeCheck = ResponseCodeCheck()
    private var dataMutable: MutableLiveData<Resource<DataModel>> = MutableLiveData()
    var liveData: LiveData<Resource<DataModel>> = dataMutable

    fun loginValidation(): Boolean {

        emailError = ""
        passwordError = ""

        if (email.isEmpty()) {
            emailError = application.getString(R.string.please_enter_email)
            return false
        } else if (!isValidEmail(email)) {
            emailError = application.getString(R.string.please_enter_valid_email)
            return false
        }
        if (password.isEmpty()) {
            passwordError = application.getString(R.string.please_enter_password)
            return false
        }
        return true
    }

    fun loginUser() {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.apply {
            put(emailParams, email)
            put(passwordParams, password)
        }

        dataMutable.value = Resource.loading(null)

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val loginRepository = LoginRepository(apiInterface)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository : Response<DataModel> = loginRepository.userLogin(hashMap)
                dataMutable.postValue(responseCodeCheck.getResponseResult(repository))

            }catch (e: Exception){
                dataMutable.postValue(Resource.error(application.getString(R.string.something_went_wrong), null))
            }
        }

    }
}