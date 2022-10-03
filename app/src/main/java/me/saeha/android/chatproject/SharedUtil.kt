package me.saeha.android.chatproject

import android.content.Context
import androidx.preference.PreferenceManager

private const val USER_NAME = "user_name"
private const val USER_POSITION = "user_position"
private const val USER_PHONE = "user_phone"
private const val USER_EMAIL = "user_email"
private const val USER_PASS = "user_pass"

fun saveUserName(context: Context, userName: String){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    sharedPreferences.edit().putString(USER_NAME, userName).apply()
}
fun saveUserPosition(context: Context, userPosition: String){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    sharedPreferences.edit().putString(USER_POSITION, userPosition).apply()
}
fun saveUserPhone(context: Context, userPhone: String){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    sharedPreferences.edit().putString(USER_PHONE, userPhone).apply()
}
fun saveUserEmail(context: Context, userEmail: String){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    sharedPreferences.edit().putString(USER_EMAIL, userEmail).apply()
}
fun saveUserPass(context: Context, userPass: String){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    sharedPreferences.edit().putString(USER_PASS, userPass).apply()
}
