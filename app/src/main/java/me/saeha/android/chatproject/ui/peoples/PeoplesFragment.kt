package me.saeha.android.chatproject.ui.peoples

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import me.saeha.android.chatproject.databinding.FragmentPeoplesBinding

class PeoplesFragment : Fragment() {

    private var _binding: FragmentPeoplesBinding? = null
    private lateinit var peoplesViewModel: PeoplesViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        peoplesViewModel =
            ViewModelProvider(this)[PeoplesViewModel::class.java]
        _binding = FragmentPeoplesBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        peoplesViewModel.peoplesData.observe(viewLifecycleOwner, Observer {
            val adapter = PeoplesListAdapter(context, it)
            val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            binding.rcyPeoplesList.adapter = adapter
            binding.rcyPeoplesList.layoutManager  = layoutManager
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}