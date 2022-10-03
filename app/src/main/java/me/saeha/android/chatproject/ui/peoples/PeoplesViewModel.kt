package me.saeha.android.chatproject.ui.peoples

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.saeha.android.chatproject.model.Peoples

class PeoplesViewModel : ViewModel() {

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is dashboard Fragment"
//    }
//    val text: LiveData<String> = _text
    lateinit var peoplesList : MutableLiveData<Peoples>
    //val peoplesListLiveData

}