package me.saeha.android.chatproject.ui.message

import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import me.saeha.android.chatproject.R

import me.saeha.android.chatproject.databinding.FragmentMessageBinding


class MessagesFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private lateinit var messageViewModel: MessagesViewModel

    private lateinit var progressDialog: AppCompatDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        messageViewModel =
            ViewModelProvider(this)[MessagesViewModel::class.java]

        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root





        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rcyMessageList.hasFixedSize()
        binding.rcyMessageList.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

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