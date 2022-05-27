package com.example.jober.model

class Chat {

    var chat_id: String? = null
    var application_id: String? = null
    var worker_id: String? = null
    var company_id: String? = null

    constructor(){}

    constructor(
        chat_id: String?,
        application_id: String?,
        worker_id: String?,
        company_id: String?
    ) {
        this.chat_id = chat_id
        this.application_id = application_id
        this.worker_id = worker_id
        this.company_id = company_id
    }


}