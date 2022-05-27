package com.example.jober.model

class Offer {

    var offer_id: String? = null
    var company_id: String? = null
    var position: String? = null
    var location: String? = null
    var skills_required: String? = null
    var languages_required: String? = null
    var titles_required: String? = null

    constructor(){}
    constructor(
        offer_id: String?,
        company_id: String?,
        position: String?,
        location: String?,
        skills_required: String?,
        languages_required: String?,
        titles_required: String?
    ) {
        this.offer_id = offer_id
        this.company_id = company_id
        this.position = position
        this.location = location
        this.skills_required = skills_required
        this.languages_required = languages_required
        this.titles_required = titles_required
    }


}