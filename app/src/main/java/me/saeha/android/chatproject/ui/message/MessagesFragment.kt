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

import me.saeha.android.chatproject.databinding.FragmentMessageBinding
import me.saeha.android.chatproject.ui.peoples.PeoplesListAdapter


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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseReference.child("chatRooms").addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("채팅목록키확인",snapshot.key.toString())
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

        //        val textView: TextView = binding.textNotifications
//        notificationsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
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