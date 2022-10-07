package me.saeha.android.navermovie_project.network

import me.saeha.android.chatproject.model.Message


class RxEvents {
    //채팅방에서 빠져나왔을 때 보내는 이벤트
    class EventOutChattingRoom(val roomId: String, val chattingLog: MutableList<Message>)


}
