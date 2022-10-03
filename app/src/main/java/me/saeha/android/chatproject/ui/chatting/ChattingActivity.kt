package me.saeha.android.chatproject.ui.chatting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.saeha.android.chatproject.R
import me.saeha.android.chatproject.databinding.ActivityChattingBinding

class ChattingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChattingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}