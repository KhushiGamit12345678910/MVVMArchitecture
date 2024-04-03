package com.example.mvvmarchitecture.registration

import android.app.Application
import android.widget.Toast
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
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(var application: Application) : ViewModel() {

    @Inject
    lateinit var retrofit: Retrofit

    var name: String = ""
    var email: String = ""
    var password: String = ""
    var imagePath: String = ""

    var nameerror: String = ""
    var emailerror: String = ""
    var passworderror: String = ""


    var responseCodeCheck: ResponseCodeCheck = ResponseCodeCheck()

    private var dataMutable: MutableLiveData<Resource<DataModel>> = MutableLiveData()
    var dataLiveData: LiveData<Resource<DataModel>> = dataMutable

    private var validationMutable: MutableLiveData<String> = MutableLiveData()
    var validationLiveData: LiveData<String> = validationMutable

    fun rgValidation(imagePath: String): Boolean {

        nameerror = ""
        emailerror = ""
        passworderror = ""


        if (name.isEmpty()) {
            nameerror = application.getString(R.string.pleaseEnterName)
            validationMutable.value =
                application.getString(R.string.pleaseEnterName)
            return false
        }
        if (email.isEmpty()) {
            emailerror = application.getString(R.string.please_enter_email)
            return false
        } else if (!isValidEmail(email)) {
            emailerror = application.getString(R.string.please_enter_valid_email)
            return false
        }
        if (password.isEmpty()) {
            passworderror = application.getString(R.string.enter_password)
            return false
        }
        if (this.imagePath.isEmpty()) {
            Toast.makeText(application, "please select image", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun registerUser(imagePath: String) {

        val file = File(imagePath)
        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("profile_picture", file.name, requestFile)

        val fullName = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), name)
        val fEmail = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), email)
        val fPassword = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), password)

        val hasMap: HashMap<String, RequestBody> = HashMap()
        hasMap.apply {
            put(nameParams, fullName)
            put(emailParams, fEmail)
            put(passwordParams, fPassword)
        }

        dataMutable.value = Resource.loading(null)

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val registrationRepository = RegistrationRepository(apiInterface)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository: Response<DataModel> =
                    registrationRepository.userRegistration(body, hasMap)
                dataMutable.postValue(responseCodeCheck.getResponseResult(repository))

            } catch (e: Exception) {
                dataMutable.postValue(
                    Resource.error(
                        application.getString(R.string.something_went_wrong),
                        null
                    )
                )
            }
        }

        /*apiInterface.userRegister(body,hasMap).enqueue(object : Callback<DataModel> {
            override fun onResponse(call: Call<DataModel>, response: Response<DataModel>) {

                if (response.body() !== null) {
                    try { dataMutable.postValue(responseCodeCheck.getResponseResult(Response.success(response.body())))

                    } catch (e: Exception) { dataMutable.postValue(Resource.error(getApplication<Application>().getString(R.string.error), null)
                        )
                    }
                } else {
                    dataMutable.postValue(
                        Resource.error(
                            getApplication<Application>().getString(R.string.error),
                            null
                        )
                    )
                }
            }
            override fun onFailure(call: Call<DataModel>, t: Throwable) {
                dataMutable.postValue(
                    Resource.error(
                        getApplication<Application>().getString(R.string.error),
                        null
                    )
                )
                Toast.makeText(getApplication(), t.message, Toast.LENGTH_SHORT).show()
            }

        })*/

    }

}
