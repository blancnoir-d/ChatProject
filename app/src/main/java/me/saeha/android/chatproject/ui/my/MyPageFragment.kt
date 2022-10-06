package me.saeha.android.chatproject.ui.my

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.saeha.android.chatproject.*
import me.saeha.android.chatproject.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvMyPageName.text = getUserName(requireContext())
        binding.tvMyPagePosition.text = getUserPosition(requireContext())

        binding.tvMyPageLogout.setOnClickListener {
            logout(requireContext())
            val intent = Intent(context,LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //이전화면 없애기
            startActivity(intent)


        }
    }
}