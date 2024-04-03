package com.example.mvvmarchitecture.utils


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.IInterface
import com.example.mvvmarchitecture.apifiles.ApiInterface
import com.example.mvvmarchitecture.const.BASE_URL
import com.example.mvvmarchitecture.const.MY_PREF
import com.example.mvvmarchitecture.sharedpref.Sharedpref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()

        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreference(context: Context): Sharedpref {
        return Sharedpref(context)
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): ApiInterface{
        return retrofit.create(ApiInterface::class.java)
    }

}
