package me.saeha.android.chatproject.ui.chatting

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.saeha.android.chatproject.model.ChattingRoom
import me.saeha.android.chatproject.model.Message
import me.saeha.android.chatproject.room.AppDataBase
import me.saeha.android.chatproject.room.Repository
import java.lang.reflect.Type


class ChattingViewModel(application: Application) : AndroidViewModel(application) {


    val databaseReference =
        Firebase.database("https://chatapplication-2b8c6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    //채팅 기록
    var _chattingLogs = MutableLiveData<List<Message>>()
    val chattingLogs: LiveData<List<Message>>
        get() = _chattingLogs
    var chattingLogsList = mutableListOf<Message>()

    //Room DB
    private var repository: Repository

    //채팅방 정보
    var chatRoomInfo: ChattingRoom? = null
    var getLastDate = ""
    var getLastMessage = ""
    //lateinit var thisChattingRoom: LiveData<ChattingRoom>


    init {
        _chattingLogs.value = chattingLogsList
        val coroutineScope = CoroutineScope(SupervisorJob())
        val appDao = AppDataBase.getInstance(application, coroutineScope)?.appDao()
        repository = appDao?.let { Repository(it) }!!
    }

    fun updateChattingLogs() {
        _chattingLogs.value = chattingLogsList
    }


    fun updateUnseenMessage(roomId: String) {
        selectThisRoomInfo(roomId)  //RoomDB 조회
        Log.d("개수확인", chatRoomInfo?.unSeenMessage.toString())
        chatRoomInfo?.lastCount = chattingLogsList.size
        chatRoomInfo?.unSeenMessage = 0
        chatRoomInfo?.let { updateChatRoom(it) }
    }

    /**
     * 메세지 저장 및 보내기
     * @param message: 보낼 메세지 정보를 담은 객체
     *
     */

    fun saveMessage(
        userId: String,
        userName: String,
        userPosition: String,
        partnerId: String,
        partnerName: String,
        partnerPosition: String,
        roomId: String,
        message: Message
    ) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("chatRooms")
                        .hasChild(roomId)
                ) {//realtimeDB에 roomId로 만들어진 채팅방 정보가 있으면
                    selectThisRoomInfo(roomId)  //로컬 DB 조회

                    val getChattingLogJson = snapshot.child("chatRooms").child(roomId).child("thread").value.toString()
                    val gson = Gson()
                    //채팅 내용이 담긴 json array -> List
                    val userListType: Type =
                        object : TypeToken<java.util.ArrayList<Message?>?>() {}.type
                    val getChattingLog: java.util.ArrayList<Message> =
                        gson.fromJson(getChattingLogJson, userListType)
                    chattingLogsList = getChattingLog

                    //날짜가 다르면 center Item 추가
                    val thisLastDate = chatRoomInfo?.lastDate?.substring(0, 11) //로컬에 저장되어 있는 날짜
                    val thisMessageCreated = message.created?.substring(0, 11) //방금 전송하려고 하는 메시지의 날짜
                    //Log.d("${javaClass}로컬 마지막 날짜", thisLastDate.toString())
                    //Log.d("${javaClass}보낼 메시지 날짜", thisMessageCreated.toString())

                    if (thisLastDate != thisMessageCreated) {//날짜가 다르면 center item 추가
                        val centerItem = Message(senderId = "C", created = message.created)
                        chattingLogsList.add(centerItem)
                    }

                    chattingLogsList.add(message)
                    val json = gson.toJson(chattingLogsList)

                    //realtimeDB 수정. 수정할 때는 hashmap으로 해야 함
                    val map = hashMapOf<String, Any?>(
                        "chatRooms/${roomId}/thread" to json,
                        "chatRooms/${roomId}/lastCount" to chattingLogsList.size
                    )
                    databaseReference.updateChildren(map)

                    //로컬에서 가져온 채팅방 정보 update
                    chatRoomInfo?.lastCount = chattingLogsList.size
                    chatRoomInfo?.unSeenMessage = 0
                    getLastDate = chattingLogsList[chattingLogsList.size - 1].created.toString()
                    getLastMessage = chattingLogsList[chattingLogsList.size - 1].content.toString()
                    chatRoomInfo?.lastDate = getLastDate
                    chatRoomInfo?.lastMessage = getLastMessage
                    chatRoomInfo?.let { updateChatRoom(it) }

                } else {//realtimeDB에 roomId로 만들어진 채팅방 정보가 없으면 -> 추가

                    //center Item 추가
                    val centerItem = Message(senderId = "C", created = message.created)
                    chattingLogsList.add(centerItem)

                    //mutablelist -> json으로 변환
                    val gson = Gson()
                    chattingLogsList.add(message)
                    val json = gson.toJson(chattingLogsList)

                    databaseReference.child("chatRooms").child(roomId).child("thread")
                        .setValue(json)
                    databaseReference.child("chatRooms").child(roomId).child("lastCount")
                        .setValue(chattingLogsList.size)
                    databaseReference.child("chatRooms").child(roomId).child("user1_id")
                        .setValue(userId)
                    databaseReference.child("chatRooms").child(roomId).child("user1_name")
                        .setValue(userName)
                    databaseReference.child("chatRooms").child(roomId).child("user1_position")
                        .setValue(userPosition)
                    databaseReference.child("chatRooms").child(roomId).child("user2_id")
                        .setValue(partnerId)
                    databaseReference.child("chatRooms").child(roomId).child("user2_name")
                        .setValue(partnerName)
                    databaseReference.child("chatRooms").child(roomId).child("user2_position")
                        .setValue(partnerPosition)


                    val getLastMessage = chattingLogsList[chattingLogsList.size - 1].content
                    val getLastDate = chattingLogsList[chattingLogsList.size - 1].created
                    val getLastCount = chattingLogsList.size
                    val newChatRoom = ChattingRoom(
                        partnerId,
                        partnerName,
                        partnerPosition,
                        getLastMessage,
                        0,
                        "",
                        chattingLogsList,
                        roomId = roomId,
                        getLastDate,
                        getLastCount
                    )
                    insertChatRoom(newChatRoom)
                }

                _chattingLogs.value = chattingLogsList

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    /**
     * 해당 방 채팅내용 가져오기 위한 메소드
     * @param roomId: 채팅방 고유 아이디
     */
    fun getThisChatLog(roomId: String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("chatRooms").hasChild(roomId)) {// realtimeDB에 생성된 채팅방이 있으면
                    //채팅 로그 가져오기
                    val getChattingLogJson =
                        snapshot.child("chatRooms").child(roomId).child("thread")
                            .getValue(String::class.java).toString() //json
                    val gson = Gson()
                    //채팅 내용이 담긴 json array -> List
                    val userListType: Type = object : TypeToken<ArrayList<Message?>?>() {}.type
                    val getChattingLog: ArrayList<Message> =
                        gson.fromJson(getChattingLogJson, userListType)

                    chattingLogsList = getChattingLog
                    _chattingLogs.value = chattingLogsList

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }




    //채팅방 저장
    fun insertChatRoom(chatRoom: ChattingRoom) = viewModelScope.launch {
        repository.insertChatRoom(chatRoom)
    }

    //채팅방 수정
    fun updateChatRoom(chatRoom: ChattingRoom) = viewModelScope.launch {
        repository.updateChatRoom(chatRoom)
    }

    //특정 채팅룸이 있는지 ChattingRoom 객체
    fun selectThisRoomInfo(roomId: String) = viewModelScope.launch {
        chatRoomInfo = repository.selectThisRoomInfo(roomId)
    }

    //특정 채팅룸이 있는지
//    fun selectThisChatRoom(roomId: String) = viewModelScope.launch {
//        thisChattingRoom = repository.selectThisRoom(roomId)
//    }
}