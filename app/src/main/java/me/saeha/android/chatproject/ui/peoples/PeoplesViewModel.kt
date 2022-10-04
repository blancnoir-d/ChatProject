package me.saeha.android.chatproject.ui.peoples

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import me.saeha.android.chatproject.getUserId
import me.saeha.android.chatproject.model.Peoples

class PeoplesViewModel(application: Application) : AndroidViewModel(application) {

    //사람들 목록
    private val _peoplesData = MutableLiveData<List<Peoples>>()
    val peoplesData: LiveData<List<Peoples>>
    get() = _peoplesData

    var peoplesList = mutableListOf<Peoples>()

    val databaseReference = Firebase.database("https://chatapplication-2b8c6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    init {
        _peoplesData.value = peoplesList
        getPeoples()
    }

    private fun getPeoples(){
        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                peoplesList.clear()
               for(i in snapshot.child("users").children){
                   val getKey = i.key.toString() //ID 가져옴
                   Log.d("사람 정보 확인", getKey)
                   val myId = getUserId(getApplication())
                   Log.d("내 아이디 확인", myId.toString())
                   if(getKey != myId){//나 외의 유저들 정보들
                       val getUserName = i.child("name").getValue(String::class.java).toString()
                       val getUserPosition = i.child("position").getValue(String::class.java).toString()
                       val getUserEmail = i.child("email").getValue(String::class.java).toString()
                       val people = Peoples(getKey,getUserName,getUserPosition,getUserEmail,"")

                       peoplesList.add(people)
                   }
               }
                _peoplesData.value = peoplesList
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}