package me.saeha.android.chatproject.ui.message

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.saeha.android.chatproject.getUserId
import me.saeha.android.chatproject.model.ChattingRoom
import me.saeha.android.chatproject.model.Message
import me.saeha.android.chatproject.room.Repository
import java.lang.reflect.Type
import me.saeha.android.chatproject.room.AppDataBase

class MessagesViewModel(application: Application) : AndroidViewModel(application) {

    //채팅방 목록(before)
    private var _chattingRooms = MutableLiveData<MutableList<ChattingRoom>>()
    val chattingRooms: LiveData<MutableList<ChattingRoom>>
        get() = _chattingRooms

    var chattingRoomList = mutableListOf<ChattingRoom>()

    //Firebase realtimeDB
    val databaseReference =
        Firebase.database("https://chatapplication-2b8c6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    //Room
    private var repository: Repository

    private lateinit var chattingRoomInfo: LiveData<ChattingRoom>
    var thisChattingRoom: ChattingRoom? = null

    init {
        _chattingRooms.value = chattingRoomList
        getChattingRooms()

        val coroutineScope = CoroutineScope(SupervisorJob()) // userData call으로 인해서 추가함
        val appDao = AppDataBase.getInstance(application, coroutineScope)?.appDao()
        repository = appDao?.let { Repository(it) }!!
    }
    var unseenMessageCount = 0

    /**
     * Firebase에 저장되어있는 유저의 채팅룸을 가져오는 메소드
     */
    fun getChattingRooms() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chattingRoomList.clear()
                unseenMessageCount = 0
                val userId = getUserId(getApplication()).toString()

                for(i in snapshot.child("chatRooms").children){ //채팅룸을 다 가져와서
                    val getRoomId = i.key.toString()
                    Log.d("채팅방 룸",getRoomId)
                    if(getRoomId.contains(userId)){ // 해당유저의 채팅방이면
                        val getUser1Id = i.child("user1_id").getValue(String::class.java).toString()
                        val getUser2Id = i.child("user2_id").getValue(String::class.java).toString()
                        var partnerId = ""
                        var partnerName = ""
                        var partnerPosition = ""

                        //상대 정보 가져오기
                        if(getUser1Id == userId){
                            partnerId = i.child("user2_id").getValue(String::class.java).toString()
                            partnerName =i.child("user2_name").getValue(String::class.java).toString()
                            partnerPosition =i.child("user2_position").getValue(String::class.java).toString()
                        }else if(getUser2Id == userId){
                            partnerId = i.child("user1_id").getValue(String::class.java).toString()
                            partnerName =i.child("user1_name").getValue(String::class.java).toString()
                            partnerPosition =i.child("user1_position").getValue(String::class.java).toString()
                        }

                        //채팅 로그 가져오기
                        val getChattingLogJson = i.child("thread").getValue(String::class.java).toString() //json
                        val gson = Gson()
                        //채팅 내용이 담긴 json array -> List
                        val userListType: Type = object : TypeToken<ArrayList<Message?>?>() {}.type
                        val getChattingLog: ArrayList<Message> = gson.fromJson(getChattingLogJson, userListType)

                        var lastMessage = "" //마지막 메시지
                        var lastDateTime = "" //마지막 DateTime

                        Log.d("MessagesViewModel채팅기록", getChattingLog.size.toString())
                        if (getChattingLog.size > 0) {
                            lastMessage = getChattingLog[getChattingLog.size - 1].content.toString()
                            lastDateTime = getChattingLog[getChattingLog.size - 1].created.toString()
                        }
                        //TODO: unseenMessage는 테이블에 저장된 거랑 비교해서 빼서 반영하기로 하죠.
                        val lastCount = i.child("lastCount").getValue(Int::class.java)?.toInt()
                        Log.d("MessagesViewModel채팅기록last", lastCount.toString())




                        //TODO Room에 해당 채팅룸이 있는지 확인, 있으면룸에 저장되어있는 최근 메시지 갯수랑 비교 후에 unseenMessage 업데이트, 없으면 insert
                        selectThisRoomInfo(getRoomId)

                        if(thisChattingRoom == null){// 채팅룸이 저장되어 있지 않으면

                            val chattingRoom = ChattingRoom(
                                partnerId,
                                partnerName,
                                partnerPosition,
                                lastMessage,
                                unseenMessageCount,
                                "image",
                                getChattingLog,
                                getRoomId,
                                lastDateTime,
                                lastCount
                            )
                            chattingRoomList.add(chattingRoom)
                            insertChatRoom(chattingRoom)

                        }else{ //채팅룸이 저장되어 있다면
                            Log.d("lastCount",thisChattingRoom!!.lastCount!!.toString())
                            if( thisChattingRoom!!.lastCount!! < lastCount!!){ //마지막 개수 비교해서
                                unseenMessageCount = lastCount - thisChattingRoom!!.lastCount!!
                                thisChattingRoom!!.unSeenMessage = unseenMessageCount//안본 메시지 개수 반영

                                updateChatRoom(thisChattingRoom!!)

                            }else{
                                thisChattingRoom!!.unSeenMessage = unseenMessageCount
                                updateChatRoom(thisChattingRoom!!)
                            }
                            chattingRoomList.add(thisChattingRoom!!)
                        }
                    }
                }
                _chattingRooms.value = chattingRoomList
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

    //특정 채팅룸이 있는지
    fun selectThisChatRoom(roomId: String) = viewModelScope.launch {
        chattingRoomInfo = repository.selectThisRoom(roomId)
    }

    fun selectThisRoomInfo(roomId: String) = viewModelScope.launch {
            thisChattingRoom = repository.selectThisRoomInfo(roomId)
        }
}