package me.saeha.android.chatproject.ui.message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import me.saeha.android.chatproject.databinding.FragmentMessageBinding
import me.saeha.android.chatproject.getUserId
import me.saeha.android.chatproject.model.ChattingRoom
import me.saeha.android.chatproject.model.Message
import me.saeha.android.chatproject.ui.peoples.PeoplesListAdapter
import me.saeha.android.navermovie_project.network.RxBus
import me.saeha.android.navermovie_project.network.RxEvents
import java.lang.reflect.Type


class MessagesFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private lateinit var messageViewModel: MessagesViewModel


    //Firebase realtimeDB
    val databaseReference =
        Firebase.database("https://chatapplication-2b8c6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        messageViewModel =
            ViewModelProvider(this)[MessagesViewModel::class.java]
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        messageViewModel.getChattingRooms()

        //채팅방을 나왔을 때 보지 않은 메시지 숫자 초기화 하기위한
        RxBus.listen(RxEvents.EventOutChattingRoom::class.java).subscribe{
            //해당 roomId로 로컬DB 조회
            messageViewModel.selectThisRoom(it.roomId)
            messageViewModel.thisChattingRoom!!.unSeenMessage = 0
            messageViewModel.thisChattingRoom!!.lastCount =it.chattingLog.size
            messageViewModel.thisChattingRoom!!.chatLog = it.chattingLog
            messageViewModel.thisChattingRoom!!.lastMessage = it.chattingLog[it.chattingLog.size-1].content
            messageViewModel.updateChatRoom(messageViewModel.thisChattingRoom!!) //로컬DB 정보 업데이트

            for(index in 0 until messageViewModel.chattingRoomList.size){//채팅목록에 쓰이는 리스트 안에 해당 채팅룸을 찾아서 수정
                if(messageViewModel.chattingRoomList[index].roomId == it.roomId){
                    messageViewModel.chattingRoomList[index] = messageViewModel.thisChattingRoom!!
                }
            }
            messageViewModel.updateChattingRooms()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = getUserId(requireContext())

        //채팅방 변화 감지
        databaseReference.child("chatRooms").addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val getRoomId = snapshot.key.toString() //추가 또는 변화된 채팅방
                var unseenMessageCount = 0
                Log.d("채팅목록키확인",snapshot.key.toString())
                if(userId?.let { getRoomId.contains(it) } == true){ //채팅방 고유 아이디에 userId 포함되어 있는지 확인

                    //해당 roomId로 로컬DB 조회
                    messageViewModel.selectThisRoom(getRoomId)
                   if(messageViewModel.thisChattingRoom!=null){ //로컬 DB에 저장되어 있으면
                       Log.d("채팅목록값확인아아로컬DB에저장되어있으면","dkdkdkd")
                       val getUser1Id = snapshot.child("user1_id").getValue(String::class.java).toString()
                       val getUser2Id = snapshot.child("user2_id").getValue(String::class.java).toString()
                       Log.d("채팅목록값user1이",getUser1Id) //null
                       Log.d("채팅목록값user2이 ",getUser2Id) //null
                       var partnerId = ""
                       var partnerName = ""
                       var partnerPosition = ""

                       //상대 정보 가져오기
                       if(getUser1Id == userId){
                           Log.i("채팅목록값user1이 나일 떄","")
                           partnerId = snapshot.child("user2_id").getValue(String::class.java).toString()
                           partnerName =snapshot.child("user2_name").getValue(String::class.java).toString()
                           partnerPosition =snapshot.child("user2_position").getValue(String::class.java).toString()
                       }else if(getUser2Id == userId){
                           Log.i("채팅목록값user2가 나일 떄","")
                           partnerId = snapshot.child("user1_id").getValue(String::class.java).toString()
                           partnerName =snapshot.child("user1_name").getValue(String::class.java).toString()
                           partnerPosition =snapshot.child("user1_position").getValue(String::class.java).toString()
                       }

                       val getChattingLogJson = snapshot.child("thread").getValue(String::class.java) //채팅 기록
                       Log.d("채팅목록값확인", getChattingLogJson.toString())
                       val gson = Gson()
                       val messageListType: Type = object: TypeToken<ArrayList<Message?>?>(){}.type
                       val getChattingLog: ArrayList<Message> = gson.fromJson(getChattingLogJson, messageListType)

                       var lastMessage = "" //마지막 메시지
                       var lastDateTime = "" //마지막 DateTime
                       if (getChattingLog.size > 0) {
                           lastMessage = getChattingLog[getChattingLog.size - 1].content.toString()
                           lastDateTime = getChattingLog[getChattingLog.size - 1].created.toString()
                       }
                       val lastCount = snapshot.child("lastCount").getValue(Int::class.java)?.toInt()

                       if(messageViewModel.thisChattingRoom!!.partnerId.isNullOrEmpty()){ //채팅방 최초 생성으로 null값이 왔을 때
                           messageViewModel.thisChattingRoom!!.partnerId = partnerId
                           messageViewModel.thisChattingRoom!!.partnerName = partnerName
                           messageViewModel.thisChattingRoom!!.partnerPosition = partnerPosition
                           //messageViewModel.thisChattingRoom!!.unSeenMessage
                       }

                       if(messageViewModel.thisChattingRoom?.lastCount!!> 0){
                           if(messageViewModel.thisChattingRoom?.lastCount!! < lastCount!!){
                               unseenMessageCount = lastCount - messageViewModel.thisChattingRoom!!.lastCount!!
                               messageViewModel.thisChattingRoom!!.unSeenMessage = unseenMessageCount//안 본 메시지 개수 반영
                           }else{
                               messageViewModel.thisChattingRoom!!.unSeenMessage = unseenMessageCount
                           }
                       }
                       messageViewModel.thisChattingRoom?.chatLog = getChattingLog
                       messageViewModel.thisChattingRoom?.lastMessage = lastMessage
                       messageViewModel.thisChattingRoom?.lastDate = lastDateTime
                       messageViewModel.updateChatRoom(messageViewModel.thisChattingRoom!!) //로컬DB 정보 업데이트

                       for(index in 0 until messageViewModel.chattingRoomList.size){//채팅목록에 쓰이는 리스트 안에 해당 채팅룸을 찾아서 수정
                           if(messageViewModel.chattingRoomList[index].roomId == getRoomId){
                               messageViewModel.chattingRoomList[index] = messageViewModel.thisChattingRoom!!
                           }
                       }
                       messageViewModel.updateChattingRooms()

                       messageViewModel.thisChattingRoom = null

                   }else{ //로컬 DB에 저장되어있지 않으면
                       Log.d("채팅목록값확인아아로컬DB에저장되어있지않으면","dkdkdkd")

                       val getChattingLogJson = snapshot.child("thread").getValue(String::class.java)

                       val getUser1Id = snapshot.child("user1_id").getValue(String::class.java).toString()
                       val getUser2Id = snapshot.child("user2_id").getValue(String::class.java).toString()
                       Log.d("채팅목록값user1이",getUser1Id) //null
                       Log.d("채팅목록값user2이 ",getUser2Id) //null
                       var partnerId = ""
                       var partnerName = ""
                       var partnerPosition = ""

                       //상대 정보 가져오기
                       if(getUser1Id == userId){
                           Log.i("채팅목록값user1이 나일 떄","")
                           partnerId = snapshot.child("user2_id").getValue(String::class.java).toString()
                           partnerName =snapshot.child("user2_name").getValue(String::class.java).toString()
                           partnerPosition =snapshot.child("user2_position").getValue(String::class.java).toString()
                       }else if(getUser2Id == userId){
                           Log.i("채팅목록값user2가 나일 떄","")
                           partnerId = snapshot.child("user1_id").getValue(String::class.java).toString()
                           partnerName =snapshot.child("user1_name").getValue(String::class.java).toString()
                           partnerPosition =snapshot.child("user1_position").getValue(String::class.java).toString()
                       }



                       Log.d("채팅목록값확인", getChattingLogJson.toString())
                       val gson = Gson()
                       val messageListType: Type = object: TypeToken<ArrayList<Message?>?>(){}.type
                       val getChattingLog: ArrayList<Message> = gson.fromJson(getChattingLogJson, messageListType)
                       var lastMessage = "" //마지막 메시지te
                       var lastDateTime = "" //마지막 DateTime
                       if (getChattingLog.size > 0) {
                           lastMessage = getChattingLog[getChattingLog.size - 1].content.toString()
                           lastDateTime = getChattingLog[getChattingLog.size - 1].created.toString()
                       }
                       var lastCount = snapshot.child("lastCount").getValue(Int::class.java)?.toInt()
                       Log.d("채팅목록값userid", userId)
                       Log.d("채팅목록값partnerId", partnerId)
                       Log.d("채팅목록값partnerName", partnerName)
                       Log.d("채팅목록값partnerPosition", partnerPosition)
                       Log.d("채팅목록값lastCount", lastCount.toString())//2

                       //unseenmessage set
                       for(index in 0 until getChattingLog.size){
                           if (lastCount != null) {
                               if(getChattingLog[index].senderId=="C"){
                                   lastCount-=1
                               }
                           }
                       }
                       if (lastCount != null) {
                           unseenMessageCount = lastCount.toInt()
                       }

                       val chattingRoom = ChattingRoom(
                           partnerId,
                           partnerName,
                           partnerPosition,
                           lastMessage,
                           unseenMessageCount,
                           "image",
                           getChattingLog,
                           getRoomId,
                           lastDateTime,
                           lastCount
                       )
                       messageViewModel.chattingRoomList.add(chattingRoom)
                       messageViewModel.insertChatRoom(chattingRoom)
                       messageViewModel.updateChattingRooms()
                   }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        binding.rcyMessageList.hasFixedSize()
        binding.rcyMessageList.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        //set RecyclerView
        messageViewModel.chattingRooms.observe(viewLifecycleOwner, Observer{
            val adapter = MessagesListAdapter(context, it)
            val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            binding.rcyMessageList.adapter = adapter
            binding.rcyMessageList.layoutManager  = layoutManager
        })

//        //DB에서
//        messageViewModel.chatRoomsLiveData.observe(viewLifecycleOwner, Observer {
//            val adapter = MessagesListAdapter(context, it)
//            val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
//            binding.rcyMessageList.adapter = adapter
//            binding.rcyMessageList.layoutManager  = layoutManager
//        })
    }


//    fun progressON(){
//        progressDialog = AppCompatDialog(this)
//        progressDialog.setCancelable(false)
//        progressDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        progressDialog.setContentView(R.layout.loading_dialog_custom) //다이얼로그 xml 생성하기
//        progressDialog.show()
//        var img_loading_framge = progressDialog.findViewById<ImageView>(R.id.GIFimage)
//        var frameAnimation = img_loading_framge?.getBackground() as AnimationDrawable
//        img_loading_framge?.post(object : Runnable{
//            override fun run() {
//                frameAnimation.start()
//            }
//
//        })
//    }
//    fun progressOFF(){
//        if(progressDialog != null && progressDialog.isShowing()){
//            progressDialog.dismiss()
//        }
//    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}