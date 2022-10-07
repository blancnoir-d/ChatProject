package me.saeha.android.chatproject.ui.career

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.saeha.android.chatproject.R
import me.saeha.android.chatproject.databinding.FragmentCareerBinding
import me.saeha.android.chatproject.databinding.FragmentMyPageBinding

class CareerFragment : Fragment() {
    private lateinit var binding: FragmentCareerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCareerBinding.inflate(inflater, container,false)


        return binding.root
    }


}