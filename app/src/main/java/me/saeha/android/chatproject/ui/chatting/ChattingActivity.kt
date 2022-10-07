package me.saeha.android.chatproject.ui.chatting

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.saeha.android.chatproject.R
import me.saeha.android.chatproject.databinding.ActivityChattingBinding
import me.saeha.android.chatproject.getUserId
import me.saeha.android.chatproject.getUserName
import me.saeha.android.chatproject.getUserPosition
import me.saeha.android.chatproject.model.ChattingRoom
import me.saeha.android.chatproject.model.Message
import me.saeha.android.chatproject.model.Peoples
import me.saeha.android.navermovie_project.network.RxBus
import me.saeha.android.navermovie_project.network.RxEvents
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class ChattingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingBinding
    private val chattingViewModel: ChattingViewModel by viewModels()

    private lateinit var chatRoom: ChattingRoom
    private lateinit var people: Peoples
    private lateinit var adapter: ChattingBubbleAdapter
    private lateinit var layoutManager: LinearLayoutManager

    //Firebase realtimeDB
    val databaseReference =
        Firebase.database("https://chatapplication-2b8c6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference


    var whereFrom = 0 //1 : profile에서 넘어왔을 때, 2: 메시지 목록에서 넘어왔을 때
    var partnerName = ""
    var partnerPosition = ""
    var partnerId = ""
    private var roomId = ""
    private var userId = ""
    var userName = ""
    var userPosition = ""


    //채팅 키보드 화면 가림 방지하기위해 추가
    private lateinit var onLayoutChangeListener: View.OnLayoutChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChattingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userId = getUserId(this).toString()
        userName = getUserName(this).toString()
        userPosition = getUserPosition(this).toString()


        whereFrom = intent.getIntExtra("whereFrom", 0)
        if (whereFrom == 1) { //profile에서 왔을 때
            people = intent.getSerializableExtra("peoplesData") as Peoples
            partnerName = people.name
            partnerPosition = people.position
            partnerId = people.id
            roomId = people.sendRoomId

            chattingViewModel.getThisChatLog(roomId)

        } else if (whereFrom == 2) { //메시지 목록에서 왔을 때
            chatRoom = intent.getSerializableExtra("chatRoomData") as ChattingRoom
            partnerName = chatRoom.partnerName.toString()
            partnerPosition = chatRoom.partnerPosition.toString()
            partnerId = chatRoom.partnerId.toString()
            roomId = chatRoom.roomId.toString()
            chattingViewModel.getThisChatLog(roomId)

        }

        //메시지 데이터가 변경이 감지 되었을 때
        databaseReference.child("chatRooms").child(roomId)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    //Log.d("${javaClass.toString()}메시지인지 키확인", snapshot.key.toString())
                    //Log.d("${javaClass.toString()}메시지인지 값확인", snapshot.value.toString())
                    if(snapshot.key == "thread"){
                        val getChattingLogJson = snapshot.value.toString()
                        val gson = Gson()
                        //채팅 내용이 담긴 json array -> List
                        val userListType: Type =
                            object : TypeToken<ArrayList<Message?>?>() {}.type
                        val getChattingLog: ArrayList<Message> =
                            gson.fromJson(getChattingLogJson, userListType)
                        val catchMessage = getChattingLog[getChattingLog.size-1]
                        if(catchMessage.senderId!=userId){
                            chattingViewModel.chattingLogsList.add(catchMessage)
                        }
                        //Log.d("${javaClass.toString()}메시지인지 채팅로그사이즈", chattingViewModel.chattingLogsList.size.toString())
                        chattingViewModel.updateChattingLogs()

                    }

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

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


        chattingViewModel.chattingLogs.observe(this) {
            adapter = ChattingBubbleAdapter(this, it)
            layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.rcyChattingList.adapter = adapter
            binding.rcyChattingList.layoutManager = layoutManager

            //화면으로 들어왔을 때 마지막 채팅이 있는 쪽으로 보일 수 있도록

            binding.rcyChattingList.scrollToPosition(it.size - 1)


        }

        //메시지 전송
        binding.btnChattingSendMessage.setOnClickListener {

            val message = binding.etChattingMessage.text.toString()
            if (message.isNotEmpty()) {
                //현재 시간
                val longNow = System.currentTimeMillis()
                // 현재 시간을 Date 타입으로 변환
                val longToDate = Date(longNow)
                val dateFormat = SimpleDateFormat(
                    "yyyy-MM-dd aa hh:mm:ss",
                    Locale.KOREA
                )
                val thisChatDate = dateFormat.format(longToDate)
                //Log.d("${javaClass.toString()}날짜 확인", snapshot.key.toString())//확인: 2022-10-04 오후 09:10:51


                val messageRow = Message(message, thisChatDate, userId, userName, roomId)
                chattingViewModel.saveMessage(
                    userId,
                    userName,
                    userPosition,
                    partnerId,
                    partnerName,
                    partnerPosition,
                    roomId,
                    messageRow
                )

                binding.etChattingMessage.setText("")
            }


        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //채팅방을 나갔을 때 보지 않은 메시지 숫자 초기화 하기위한
                RxBus.publish(RxEvents.EventOutChattingRoom(roomId,chattingViewModel.chattingLogsList ))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //채팅방을 나갔을 때 보지 않은 메시지 숫자 초기화 하기위한
        RxBus.publish(RxEvents.EventOutChattingRoom(roomId,chattingViewModel.chattingLogsList))
        finish()
    }
}