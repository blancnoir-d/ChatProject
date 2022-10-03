package me.saeha.android.chatproject.ui.peoples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.saeha.android.chatproject.databinding.ActivityPeoplesProfileBinding
import me.saeha.android.chatproject.model.Peoples

class PeoplesProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPeoplesProfileBinding
    private lateinit var people: Peoples

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

        }
    }
}