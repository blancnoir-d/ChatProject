package me.saeha.android.chatproject.ui.chatting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.saeha.android.chatproject.model.Message
import java.lang.reflect.Type


class ChattingViewModel(application: Application) : AndroidViewModel(application) {


    val databaseReference =
        Firebase.database("https://chatapplication-2b8c6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    val _chattingLogs = MutableLiveData<List<Message>>()
    val chattingLogs: LiveData<List<Message>>
        get() = _chattingLogs

    var chattingLogsList = mutableListOf<Message>()

    init {

        _chattingLogs.value = chattingLogsList

    }

    fun getChattingLogs() {

    }


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
                if (snapshot.child("chatRooms").hasChildren()) {
                    for (i in snapshot.child("chatRooms").children) {
                        val getRoomId = i.key.toString()
                        if (getRoomId == roomId) { //roomId로 만들어진 챗이 있느냐 확인-> 기존에 있는 값 수정하기
                            //기존에 있는 값 수정하기
                            val gson = Gson()
                            chattingLogsList.add(message)
                            val json = gson.toJson(chattingLogsList)

                            //수정할 때는 hashmap으로 해야 함
                            val map = hashMapOf<String, Any?>(
                                "chatRooms/${roomId}/thread" to json,
                                "chatRooms/${roomId}/lastCount" to chattingLogsList.size
                            )
                            databaseReference.updateChildren(map)

                        }
                    }

                } else {

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

                    //Json으로 넣어줘야
                    val gson = Gson()
                    chattingLogsList.add(message)
                    val json = gson.toJson(chattingLogsList)
                    databaseReference.child("chatRooms").child(roomId).child("thread")
                        .setValue(json)
                }

                _chattingLogs.value = chattingLogsList

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }


    fun checkChatRoom(userId: String, roomId: String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("chatRooms").hasChild(roomId)) {// 생성된 채팅방이 있으면
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
                TODO("Not yet implemented")
            }
        })


    }
}