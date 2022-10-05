package me.saeha.android.chatproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
@Entity
data class Message(
    var content: String? = "",
    var created: String? = "",
    var senderId: String? = "",
    var senderName: String? = "",
    var roomId: String? = "",
):Serializable{
    @PrimaryKey(autoGenerate = true)
    var messageId: Int = 0
}
