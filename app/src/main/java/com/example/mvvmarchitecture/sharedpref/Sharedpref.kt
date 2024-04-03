package com.example.mvvmarchitecture.sharedpref

import android.content.Context
import com.example.mvvmarchitecture.const.MY_PREF
import javax.inject.Inject

class Sharedpref @Inject constructor(context: Context) {

     val sharedpref = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE)
     var editor = sharedpref.edit()

    fun putBoolean(key : String, value: Boolean){
        editor.putBoolean(key,value)
        editor.apply()
    }

    fun putString(key : String?, value: String){
        editor.putString(key,value)
        editor.apply()
    }
    fun getString(key : String?,value:String):String?{
        return sharedpref.getString(key,"").toString()
    }
    fun getBoolean(key : String?):Boolean?{
        return sharedpref.getBoolean(key,false)
    }

}