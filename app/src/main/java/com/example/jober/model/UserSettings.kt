package com.example.jober.model

import com.google.firebase.database.Exclude

class UserSettings {

    var user_type : String? = null

    constructor(){}

    constructor(user_type: String?) {
        this.user_type = user_type
    }

    @Exclude
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "user_type" to user_type,
        )
    }


}