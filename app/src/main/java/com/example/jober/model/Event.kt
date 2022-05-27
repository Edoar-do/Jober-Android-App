package com.example.jober.model

class Event {

    var event_id: String? = null
    var chat_id: String? = null
    var worker_id: String? = null
    var company_id: String? = null

    constructor(){}

    constructor(event_id: String?, chat_id: String?, worker_id: String?, company_id: String?) {
        this.event_id = event_id
        this.chat_id = chat_id
        this.worker_id = worker_id
        this.company_id = company_id
    }


}