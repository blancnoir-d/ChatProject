package me.saeha.android.chatproject.ui.peoples

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import me.saeha.android.chatproject.databinding.ActivityPeoplesProfileBinding
import me.saeha.android.chatproject.getUserId
import me.saeha.android.chatproject.model.Peoples
import me.saeha.android.chatproject.ui.chatting.ChattingActivity

class PeoplesProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPeoplesProfileBinding
    private lateinit var people: Peoples
    val peoplesViewModel: PeoplesViewModel by viewModels()

    //Firebase realtimeDB
    val databaseReference =
        Firebase.database("https://chatapplication-2b8c6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeoplesProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = getUserId(this)
        people = intent.getSerializableExtra("peoplesData") as Peoples

        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("chatRooms").hasChild("${userId}_${people.id}")){
                    people.sendRoomId = "${userId}_${people.id}"
                }else if(snapshot.child("chatRooms").hasChild("${people.id}_${userId}")){
                    people.sendRoomId = "${people.id}_${userId}"
                }else{
                    people.sendRoomId = "${userId}_${people.id}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        setVIew()
    }

    private fun setVIew(){
        binding.tvProfileName.text = people.name
        binding.tvProfilePosition.text = people.position

        binding.btnProfileGoChat.setOnClickListener {

            val intent = Intent(this,ChattingActivity::class.java)
            intent.putExtra("whereFrom", 1)
            intent.putExtra("peoplesData", people)
            startActivity(intent)

        }
    }
}