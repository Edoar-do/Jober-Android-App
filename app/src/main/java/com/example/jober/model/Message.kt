package com.example.jober.model

class Message {

    var message_id: String? = null
    var chat_id: String? = null
    var sender_id: String? = null
    var receiver_id: String? = null

    constructor(){}

    constructor(message_id: String?, chat_id: String?, sender_id: String?, receiver_id: String?) {
        this.message_id = message_id
        this.chat_id = chat_id
        this.sender_id = sender_id
        this.receiver_id = receiver_id
    }


}