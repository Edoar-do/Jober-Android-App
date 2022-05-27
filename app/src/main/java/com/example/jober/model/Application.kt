package com.example.jober.model

class Application {

    var application_id: String? = null
    var worker_id: String? = null
    var offer_id: String? = null

    constructor(){}

    constructor(application_id: String?, worker_id: String?, offer_id: String?) {
        this.application_id = application_id
        this.worker_id = worker_id
        this.offer_id = offer_id
    }


}