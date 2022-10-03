package me.saeha.android.chatproject.ui.peoples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.saeha.android.chatproject.databinding.ActivityPeoplesProfileBinding

class PeoplesProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPeoplesProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeoplesProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setVIew()
    }

    private fun setVIew(){
        binding.btnProfileGoChat.setOnClickListener {

        }
    }
}