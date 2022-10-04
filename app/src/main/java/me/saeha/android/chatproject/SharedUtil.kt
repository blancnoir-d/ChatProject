package me.saeha.android.chatproject

import android.content.Context
import androidx.preference.PreferenceManager

private const val USER_NAME = "user_name"
private const val USER_POSITION = "user_position"
private const val USER_EMAIL = "user_email"
private const val USER_ID = "user_id"
private const val USER_PASS = "user_pass"

fun saveUserName(context: Context, userName: String){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    sharedPreferences.edit().putString(USER_NAME, userName).apply()
}
fun getUserName(context: Context): String?{
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    return sharedPreferences.getString(USER_NAME, "")
}

fun saveUserPosition(context: Context, userPosition: String){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    sharedPreferences.edit().putString(USER_POSITION, userPosition).apply()
}

fun getUserPosition(context: Context): String?{
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    return sharedPreferences.getString(USER_POSITION, "")
}

fun saveUserEmail(context: Context, userEmail: String){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    sharedPreferences.edit().putString(USER_EMAIL, userEmail).apply()
}

fun saveUserId(context: Context, userId: String){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    sharedPreferences.edit().putString(USER_ID, userId).apply()
}
//fun saveUserPass(context: Context, userPass: String){
//    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//    sharedPreferences.edit().putString(USER_PASS, userPass).apply()
//}

fun getUserId(context: Context): String?{
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    return sharedPreferences.getString(USER_ID, "")
}


fun checkLoginState(context: Context):Boolean{
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    if(sharedPreferences.contains(USER_ID)){
        return true
    }
    return false
}

fun logout(context: Context){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    sharedPreferences.edit().apply{
        remove(USER_ID)
        remove(USER_NAME)
        remove(USER_POSITION)
        remove(USER_EMAIL)
        //remove(USER_PASS)
        apply()
    }
}