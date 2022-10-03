package com.example.chitchat.models

class Message {
    var userName: String? = null
    var messageContent : String?  = null
    var roomName: String?  = null
    var senderType : Int?  = null


    constructor(){}

    constructor(userName: String?, messageContent: String?, roomName: String?, senderType: Int?) {
        this.userName = userName
        this.messageContent = messageContent
        this.roomName = roomName
        this.senderType = senderType
    }
}
