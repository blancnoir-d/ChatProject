package me.saeha.android.chatproject.room

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import me.saeha.android.chatproject.model.ChattingRoom
import me.saeha.android.chatproject.model.Message

@Dao
interface AppDAO {
    @Insert
    fun insertChatRoom(message: ChattingRoom)

    @Query("Select * from ChattingRoom ORDER BY id DESC")
    fun getAllChatRoom(): Flow<List<ChattingRoom>>

    @Update
    fun updateChatRoom(vararg room: ChattingRoom)

    @Delete
    fun deleteChatRoom(room: ChattingRoom)

    //특정 채팅룸 LiveData
    @Query("SELECT * FROM ChattingRoom WHERE roomId = :thisRoomId")
    fun selectThisRoom(thisRoomId: String):LiveData<ChattingRoom>

    //특정 채팅룸 ChattingRoom
    @Query("SELECT * FROM ChattingRoom WHERE roomId = :thisRoomId")
    fun selectThisRoomInfo(thisRoomId: String):ChattingRoom

    //특정 채팅룸에 해당하는 메시지 가져오기
    @Query("SELECT * FROM Message WHERE roomId = :thisRoomId ORDER BY messageId DESC")
    fun selectThisRoomMessage(thisRoomId: Int):Flow<List<Message>>

}