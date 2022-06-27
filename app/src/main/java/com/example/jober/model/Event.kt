package com.example.jober.model

import java.io.Serializable
import java.util.*

class Event  : Serializable {

    var event_id: String? = null
    var chat_id: String? = null
    var worker_id: String? = null
    var company_id: String? = null
    var date: Date? = null
    var inverted_date_millis : Long? = null

    constructor(){}

    constructor(event_id: String?, chat_id: String?, worker_id: String?, company_id: String?, date: Date?, inverted_date_millis : Long?) {
        this.event_id = event_id
        this.chat_id = chat_id
        this.worker_id = worker_id
        this.company_id = company_id
        this.date = date
        this.inverted_date_millis = inverted_date_millis
    }


}