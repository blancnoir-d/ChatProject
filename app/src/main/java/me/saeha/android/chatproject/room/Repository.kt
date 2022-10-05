package me.saeha.android.chatproject.room

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import me.saeha.android.chatproject.model.ChattingRoom
import me.saeha.android.chatproject.model.Message

class Repository(private val appDAO: AppDAO) {

    //Observed Flow는 데이터가 변경되면 관찰자에게 알립니다.
    val allChatRooms: Flow<List<ChattingRoom>> = appDAO.getAllChatRoom()
    //데이터 다 가져오는 거 위랑 같은걸 메소드로 만든 것
//    fun getAllChatRoom(): Flow<List<ChatRoom>> {
//        return chatRoomDAO.getAllChatRoom()
//    }



    //DAO의 fun이 suspend면 여기도 같이 suspend fun이어야 함.
    suspend fun insertChatRoom(chatRoom: ChattingRoom) {
        appDAO.insertChatRoom(chatRoom)
    }

    fun updateChatRoom(chatRoom: ChattingRoom) {
        appDAO.updateChatRoom(chatRoom)
    }

    fun deleteChatRoom(chatRoom: ChattingRoom) {
        appDAO.deleteChatRoom(chatRoom)
    }

    fun selectThisRoom(roomId: String):LiveData<ChattingRoom>{
        return appDAO.selectThisRoom(roomId)
    }

    fun selectThisRoomInfo(roomId: String):ChattingRoom{
        return appDAO.selectThisRoomInfo(roomId)
    }

    fun selectThisRoomMessage(id: Int): Flow<List<Message>> {
        return appDAO.selectThisRoomMessage(id)
    }


}