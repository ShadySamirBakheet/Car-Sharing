package shady.samir.carsharing.data.datasources

import android.content.Context
import android.content.SharedPreferences
import shady.samir.carsharing.data.models.User

class SharedStorage {
    companion object{

        fun saveLoginData(context: Context,user: User){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            val edit = sharedPreferences.edit()
            edit.putString("login_id", user.id)
            edit.putString("user_phone", user.userPhone)
            edit.putString("user_img", user.userImg)
            edit.putBoolean("haveCar", user.haveCar == true)
            edit.apply()
        }

        fun getLoginPhoneData(context: Context): String? {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            return sharedPreferences.getString("user_phone","")
        }

        fun getHaveCarData(context: Context): Boolean{
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("haveCar",false)
        }

        fun getLoginImagesData(context: Context): String? {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            return sharedPreferences.getString("user_img","")
        }

    }
}