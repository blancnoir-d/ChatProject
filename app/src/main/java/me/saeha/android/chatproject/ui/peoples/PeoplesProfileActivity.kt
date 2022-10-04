package me.saeha.android.chatproject.ui.peoples

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import me.saeha.android.chatproject.databinding.ActivityPeoplesProfileBinding
import me.saeha.android.chatproject.model.Peoples
import me.saeha.android.chatproject.ui.chatting.ChattingActivity

class PeoplesProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPeoplesProfileBinding
    private lateinit var people: Peoples
    val peoplesViewModel: PeoplesViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeoplesProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        people = intent.getSerializableExtra("peoplesData") as Peoples

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