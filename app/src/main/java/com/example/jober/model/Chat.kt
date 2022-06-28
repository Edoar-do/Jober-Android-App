package com.example.jober.model

import com.google.firebase.database.Exclude

class Chat {

    var chat_id: String? = null
    var application_id: String? = null
    var worker_id: String? = null
    var company_id: String? = null
    var last_update : Long? = null


    constructor(){}

    constructor(
        chat_id: String?,
        application_id: String?,
        worker_id: String?,
        company_id: String?,
        last_update : Long?
    ) {
        this.chat_id = chat_id
        this.application_id = application_id
        this.worker_id = worker_id
        this.company_id = company_id
        this.last_update = last_update
    }

}