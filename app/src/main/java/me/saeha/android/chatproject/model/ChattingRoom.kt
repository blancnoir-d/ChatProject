package me.saeha.android.chatproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
@Entity
data class ChattingRoom(
    var partnerId: String?,
    var partnerName: String?,
    var partnerPosition: String?,
    var lastMessage: String?,
    var unSeenMessage: Int?,
    var partnerImage: String?,
    var chatLog: MutableList<Message>?,
    var roomId: String?,
    var lastDate: String?,
    var lastCount: Int?
):Serializable{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
