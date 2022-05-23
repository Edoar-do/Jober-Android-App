package com.example.jober

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class CompanyProfileEdit : AppCompatActivity() {

    lateinit var btn_save : Button
    lateinit var btn_cancel : Button
    lateinit var edt_name : EditText
    lateinit var edt_sector : EditText
    lateinit var edt_country : EditText
    lateinit var edt_city : EditText
    lateinit var edt_description : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btn_save = findViewById(R.id.btn_save)
        btn_cancel = findViewById(R.id.btn_cancel)
        edt_name = findViewById(R.id.edt_name)
        edt_sector = findViewById(R.id.edt_sector)
        edt_country = findViewById(R.id.edt_country)
        edt_city = findViewById(R.id.edt_city)
        edt_description = findViewById(R.id.edt_description)

        btn_save.setOnClickListener {
            //TODO edit new information in db
            finish()
        }

        btn_cancel.setOnClickListener {
            finish()
        }
    }

}