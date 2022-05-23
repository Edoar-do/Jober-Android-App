package com.example.jober

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class WorkerProfileCreation : AppCompatActivity() {

    lateinit var btn_save : Button
    lateinit var edt_name : EditText
    lateinit var edt_surname : EditText
    lateinit var edt_age : EditText
    lateinit var edt_country : EditText
    lateinit var edt_city : EditText
    lateinit var edt_main_profession : EditText
    lateinit var edt_bio : EditText
    lateinit var edt_skills : EditText
    lateinit var edt_languages : EditText
    lateinit var edt_educational_experiences : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_profile_creation)

        btn_save = findViewById(R.id.btn_save)
        edt_name = findViewById(R.id.edt_name)
        edt_surname = findViewById(R.id.edt_surname)
        edt_age = findViewById(R.id.edt_age)
        edt_country = findViewById(R.id.edt_country)
        edt_city = findViewById(R.id.edt_city)
        edt_main_profession = findViewById(R.id.edt_main_profession)
        edt_bio = findViewById(R.id.edt_bio)
        edt_skills = findViewById(R.id.edt_skills)
        edt_languages = findViewById(R.id.edt_languages)
        edt_educational_experiences = findViewById(R.id.edt_educational_experiences)
    }

    fun save(view : View) {
        // TODO
    }
}