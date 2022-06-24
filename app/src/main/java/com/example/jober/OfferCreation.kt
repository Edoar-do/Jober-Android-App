package com.example.jober

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.jober.model.Offer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference

class OfferCreation : AppCompatActivity() {

    lateinit var btn_save : Button
    lateinit var edt_position : EditText
    lateinit var edt_location : EditText
    lateinit var edt_job_description : EditText
    lateinit var edt_languages_required : EditText
    lateinit var edt_edu_exp_required : EditText
    lateinit var edt_skills_required : EditText
    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_creation)
        supportActionBar!!.title = "Jober - New Offer"

        m_auth = FirebaseAuth.getInstance()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()


        btn_save = findViewById(R.id.btn_save_company)
        edt_position = findViewById(R.id.edt_position)
        edt_location = findViewById(R.id.edt_location)
        edt_job_description = findViewById(R.id.edt_job_description)
        edt_languages_required = findViewById(R.id.edt_languages_required)
        edt_edu_exp_required = findViewById(R.id.edt_edu_exp_required)
        edt_skills_required = findViewById(R.id.edt_skills_required)
    }


    fun save(view : View) {
        //instance creation

        val position = edt_position.text.toString()
        val location = edt_location.text.toString()
        val skills_required = edt_skills_required.text.toString()
        val languages_required = edt_languages_required.text.toString()
        val edu_exp_required = edt_edu_exp_required.text.toString()
        val job_description = edt_job_description.text.toString()

        var error_present = false

        if (position.isEmpty()) {
            edt_position.error = "Please enter a position"
            error_present = true
        }

        if (location.isEmpty()) {
            edt_location.error = "Please enter a location"
            error_present = true
        }

        if (edt_job_description.text.toString() == "") {
            edt_job_description.error = "Please enter a job description"
            error_present = true
        }

        if (skills_required.isEmpty()) {
            edt_skills_required.error = "Please enter the skills required"
            error_present = true
        }

        if (languages_required.isEmpty()) {
            edt_languages_required.error = "Please enter the languages required"
            error_present = true
        }

        if (edu_exp_required.isEmpty()) {
            edt_edu_exp_required.error = "Please enter the educational experiences required"
            error_present = true
        }

        val company_id = m_auth.currentUser?.uid!!

        if (!error_present) {
            var timestamp = System.currentTimeMillis()
            val offer_id = m_auth.currentUser?.uid!! + "_" + timestamp

            val offer = Offer(offer_id, company_id, position, location,
                job_description, skills_required, languages_required, edu_exp_required, timestamp)


            m_db_ref.child("offers").child(offer_id).setValue(offer)

            //fragment switch
            intent = Intent(this, OfferDescription::class.java)
            intent.putExtra("offer_id", offer_id)    // specifichiamo il destination fragment
            finish()
            startActivity(intent)
        }


    }
}