package com.example.jober.model

import com.google.firebase.database.Exclude

class Company {

    var company_name: String? = null
    var sector: String? = null
    var country: String? = null
    var city: String? = null
    var description: String? = null
    var img_profile_url: String? = null

    constructor(){}

    constructor(
        company_name: String?,
        sector: String?,
        country: String?,
        city: String?,
        description: String?,
        img_profile_url: String?
    ) {
        this.company_name = company_name
        this.sector = sector
        this.country = country
        this.city = city
        this.description = description
        this.img_profile_url = img_profile_url
    }

    @Exclude
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "company_name" to company_name,
            "sector" to sector,
            "country" to country,
            "city" to city,
            "description" to description,
            "img_profile_url" to img_profile_url
        )
    }


}