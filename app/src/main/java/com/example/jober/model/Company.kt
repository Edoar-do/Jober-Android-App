package com.example.jober.model

class Company {

    var company_id: String? = null
    var company_name: String? = null
    var sector: String? = null
    var country: String? = null
    var city: String? = null
    var description: String? = null
    var company_logo: Int? = null

    constructor(){}

    constructor(
        company_id: String?,
        company_name: String?,
        sector: String?,
        country: String?,
        city: String?,
        description: String?,
        company_logo: Int?
    ) {
        this.company_id = company_id
        this.company_name = company_name
        this.sector = sector
        this.country = country
        this.city = city
        this.description = description
        this.company_logo = company_logo
    }


}