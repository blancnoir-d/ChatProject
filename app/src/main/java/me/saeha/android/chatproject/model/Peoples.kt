package me.saeha.android.chatproject.model

import java.io.Serializable

data class Peoples(
    var id: String,
    var name: String,
    var position: String,
    var email: String,
    var profileImage: String,
    var sendRoomId: String = ""
):Serializable
