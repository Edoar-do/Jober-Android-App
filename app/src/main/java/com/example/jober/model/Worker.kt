package com.example.jober.model

class Worker {


    var name : String? = null
    var surname : String? = null
    var age : Int? = null
    var country : String? = null
    var city : String? = null
    var skills : String? = null
    var languages : String? = null
    var educational_experiences : String? = null
    var profile_image_url : String? = null
    var bio : String? = null
    var main_profession : String? = null

    constructor(){}
    constructor(
        name: String?,
        surname: String?,
        age: Int?,
        country : String?,
        city : String?,
        skills: String?,
        languages: String?,
        educational_experiences: String?,
        profile_image_url: String?,
        bio: String?,
        main_profession: String?
    ) {
        this.name = name
        this.surname = surname
        this.age = age
        this.country = country
        this.city = city
        this.skills = skills
        this.languages = languages
        this.educational_experiences = educational_experiences
        this.profile_image_url = profile_image_url
        this.bio = bio
        this.main_profession = main_profession
    }


}