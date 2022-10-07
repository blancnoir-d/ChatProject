package me.saeha.android.chatproject.room

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.saeha.android.chatproject.model.Message

class Converters {

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