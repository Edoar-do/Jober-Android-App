package com.example.jober.model

class Company {

    var company_name: String? = null
    var sector: String? = null
    var country: String? = null
    var city: String? = null
    var description: String? = null
    var company_logo: String? = null

    constructor(){}

    constructor(
        company_name: String?,
        sector: String?,
        country: String?,
        city: String?,
        description: String?,
        company_logo: String?
    ) {
        this.company_name = company_name
        this.sector = sector
        this.country = country
        this.city = city
        this.description = description
        this.company_logo = company_logo
    }


}