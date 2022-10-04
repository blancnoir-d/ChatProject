package me.saeha.android.chatproject.ui.message

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.saeha.android.chatproject.getUserId
import me.saeha.android.chatproject.model.ChattingRoom
import me.saeha.android.chatproject.model.Message
import java.lang.reflect.Type


class MessagesViewModel(application: Application) : AndroidViewModel(application) {

    //채팅방 목록
    private var _chattingRooms = MutableLiveData<MutableList<ChattingRoom>>()
    val chattingRooms: LiveData<MutableList<ChattingRoom>>
        get() = _chattingRooms

    var chattingRoomList = mutableListOf<ChattingRoom>()

    val databaseReference =
        Firebase.database("https://chatapplication-2b8c6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    init {
        _chattingRooms.value = chattingRoomList
        getChattingRooms()
    }
    var unseenMessageCount = 0

    fun getChattingRooms() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chattingRoomList.clear()
                unseenMessageCount = 0
                val userId = getUserId(getApplication()).toString()

                for(i in snapshot.child("chatRooms").children){
                    val getRoomId = i.key.toString()
                    Log.d("채팅방 룸",getRoomId)
                    if(getRoomId.contains(userId)){
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
                        val lastCount = i.child("lastCount").getValue(Int::class.java)

                        val chattingRoom = ChattingRoom(
                            partnerId,
                            partnerName,
                            partnerPosition,
                            lastMessage,
                            10,
                            "image",
                            getChattingLog,
                            getRoomId,
                            lastDateTime,
                            lastCount
                        )
                        chattingRoomList.add(chattingRoom)

                    }
                }
                _chattingRooms.value = chattingRoomList
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}