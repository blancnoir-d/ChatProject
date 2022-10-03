package me.saeha.android.chatproject.ui.chatting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import me.saeha.android.chatproject.R
import me.saeha.android.chatproject.databinding.ActivityChattingBinding

class ChattingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChattingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setView()


    }


    private fun setView(){
        //appbar(Toolbar)
        val toolbarBodyTemplate = binding.toolbar
        setSupportActionBar(toolbarBodyTemplate)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화 (화살표)
        binding.toolbar.setNavigationIcon(R.drawable.ic_close_gray_24) //뒤로가기 버튼 아이콘 변경

        binding.tvChattingToolbarName.text = ""
        binding.tvChattingToolbarPosition.text = ""
    }


    //Toolbar 메뉴 클릭 이벤트
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { //뒤로 가기 버튼
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}