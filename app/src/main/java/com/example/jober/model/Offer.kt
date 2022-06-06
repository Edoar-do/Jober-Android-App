package com.example.jober.model

import com.google.firebase.database.Exclude

class Offer {

    var company_id: String? = null
    var position: String? = null
    var location: String? = null
    var job_description: String? = null
    var skills_required: String? = null
    var languages_required: String? = null
    var edu_exp_required: String? = null

    constructor(){}
    constructor(
        company_id: String?,
        position: String?,
        location: String?,
        job_description: String?,
        skills_required: String?,
        languages_required: String?,
        edu_exp_required: String?
    ) {
        this.company_id = company_id
        this.position = position
        this.location = location
        this.job_description = job_description
        this.skills_required = skills_required
        this.languages_required = languages_required
        this.edu_exp_required = edu_exp_required
    }


    @Exclude
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "company_id" to company_id,
            "position" to position,
            "location" to location,
            "job_description" to job_description,
            "skills_required" to skills_required,
            "languages_required" to languages_required,
            "edu_exp_required" to edu_exp_required,
        )
    }
}