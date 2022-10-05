package me.saeha.android.chatproject.room

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.saeha.android.chatproject.model.Message

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {

        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }


    @TypeConverter
    fun listToJson(list: MutableList<Message>): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun jsonToList(value: String): MutableList<Message> {
        val listType = object : TypeToken<MutableList<Message>>() {

        }.type
        return Gson().fromJson(value, listType)
    }


}