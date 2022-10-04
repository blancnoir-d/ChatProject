package me.saeha.android.chatproject.ui.chatting
import java.time.LocalDate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import me.saeha.android.chatproject.R
import me.saeha.android.chatproject.databinding.ActivityChattingBinding
import me.saeha.android.chatproject.getUserId
import me.saeha.android.chatproject.getUserName
import me.saeha.android.chatproject.getUserPosition
import me.saeha.android.chatproject.model.ChattingRoom
import me.saeha.android.chatproject.model.Message
import me.saeha.android.chatproject.model.Peoples
import me.saeha.android.chatproject.ui.peoples.PeoplesViewModel
import java.text.SimpleDateFormat
import java.util.*

class ChattingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingBinding
    private val chattingViewModel: ChattingViewModel by viewModels()
    private lateinit var chatRoom: ChattingRoom
    private lateinit var people: Peoples
    private lateinit var adapter: ChattingBubbleAdapter
    private lateinit var layoutManager: LinearLayoutManager
    var whereFrom = 0
    var partnerName = ""
    var partnerPosition = ""
    var partnerId = ""
    var roomId = ""
    var userId = ""
    var userName = ""
    var userPosition = ""



    //채팅 키보드 화면 가림 방지하기위해 만든 것
    private lateinit var onLayoutChangeListener: View.OnLayoutChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChattingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userId = getUserId(this).toString()
        userName = getUserName(this).toString()
        userPosition = getUserPosition(this).toString()


        whereFrom = intent.getIntExtra("whereFrom",0)
        if(whereFrom == 1){ //profile에서 왔을 때
            people = intent.getSerializableExtra("peoplesData") as Peoples
            partnerName = people.name
            partnerPosition = people.position
            partnerId = people.id
            roomId = "${userId}_${partnerId}"
            //profile에서 왔을 때 이미 만들어져 있는 방이 있는지 없는지 확인 필요
            //있으면 데이터 불러와야하고
            //없으면 메시지 전송 때 방을 생성
           chattingViewModel.checkChatRoom(userId,roomId)

        }else if(whereFrom == 2){ //메시지 목록에서 왔을 때
            chatRoom = intent.getSerializableExtra("chatRoomData") as ChattingRoom
            partnerName = chatRoom.partnerName.toString()
            partnerPosition = chatRoom.partnerPosition.toString()
            partnerId = chatRoom.partnerId.toString()
            roomId = chatRoom.roomId.toString()
            chattingViewModel.checkChatRoom(userId,roomId)
        }

        setView()


    }


    private fun setView() {
        val toolbarBodyTemplate = binding.toolbar
        setSupportActionBar(toolbarBodyTemplate)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화 (화살표)
        binding.toolbar.setNavigationIcon(R.drawable.ic_close_gray_24) //뒤로가기 버튼 아이콘 변경
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //키보드가 올라왔을때 화면을 가리는 부분 해결하기 위해서 추가
        onLayoutChangeListener =
            View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom -> // 키보드가 올라와 높이가 변함
                if (bottom < oldBottom) {
                    binding.rcyChattingList.scrollBy(0, oldBottom - bottom) // 스크롤 유지를 위해 추가
                }
            }
        binding.rcyChattingList.addOnLayoutChangeListener(onLayoutChangeListener) //키보드가 올라왔을때 화면을 가리는 부분 해결하기 위해서 추가

        binding.tvChattingToolbarName.text = partnerName
        binding.tvChattingToolbarPosition.text = partnerPosition

//        Log.d("ChattingActivity 메시지 사이즈 확인", chatRoom.chatLog?.size.toString())
//        Log.d("ChattingActivity 메시지 내용 확인", chatRoom.chatLog?.get(0)?.content.toString())
//        Log.d("ChattingActivity 메시지 보낸사람 확인", chatRoom.chatLog?.get(0)?.senderName.toString())
//        Log.d("ChattingActivity 메시지 보낸사람 아이디 확인", chatRoom.chatLog?.get(0)?.senderId.toString())


        chattingViewModel.chattingLogs.observe(this){
            adapter = ChattingBubbleAdapter(this,it)
            layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            binding.rcyChattingList.adapter = adapter
            binding.rcyChattingList.layoutManager = layoutManager

        }

        //메시지 전송
        binding.btnChattingSendMessage.setOnClickListener {

            val message = binding.etChattingMessage.text.toString()
            if(message.isNotEmpty()){
                //현재 시간
                val longNow = System.currentTimeMillis()
                // 현재 시간을 Date 타입으로 변환
                val longToDate = Date(longNow)
                val dateFormat = SimpleDateFormat(
                    "yyyy-MM-dd aa hh:mm:ss",
                    Locale.KOREA
                )
                val thisChatDate = dateFormat.format(longToDate)
                Log.d("날짜 확인", thisChatDate.toString()) //확인: 2022-10-04 오후 09:10:51

                val messageRow = Message(message, thisChatDate, userId, userName, roomId)
                chattingViewModel.saveMessage(userId, userName, userPosition, partnerId, partnerName, partnerPosition, roomId, messageRow)
            }


        }
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