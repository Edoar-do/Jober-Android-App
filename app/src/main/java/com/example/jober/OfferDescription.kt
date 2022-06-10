package com.example.jober

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jober.model.Company
import com.example.jober.model.Offer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class OfferDescription : AppCompatActivity() {

    lateinit var btn_edit : ImageButton
    lateinit var tv_company_name : TextView
    lateinit var tv_company_sector : TextView
    lateinit var tv_position : TextView
    lateinit var tv_location : TextView
    lateinit var tv_job_description : TextView
    lateinit var tv_skills_required : TextView
    lateinit var tv_languages_required : TextView
    lateinit var tv_edu_exp_required : TextView
    lateinit var iv_logo : ImageView

    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase

    lateinit var offer : Offer
    lateinit var company : Company
    lateinit var offer_id : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_description)

        btn_edit = findViewById(R.id.btn_edit)
        tv_company_name = findViewById(R.id.tv_company_name)
        tv_company_sector = findViewById(R.id.tv_company_sector)
        tv_position = findViewById(R.id.tv_position)
        tv_location = findViewById(R.id.tv_location)
        tv_job_description = findViewById(R.id.tv_job_description)
        tv_skills_required = findViewById(R.id.tv_skills_required)
        tv_languages_required = findViewById(R.id.tv_languages_required)
        tv_edu_exp_required = findViewById(R.id.tv_edu_exp_required)
        iv_logo = findViewById(R.id.iv_logo)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database =
            Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        offer_id = intent.getStringExtra("offer_id").toString()

        // get references to buttons 'view applicants', 'apply', 'cancel application'
        // get user type

        // if usertype == company :
        //      if offer.company == this.company :
        //          button.visible = true
        //          button.text = show applicants
        //          button.onclick = fun show applicants
        //          pencil_mod.visible = true
        // else :
        //      button.visible = true
        //      if worker is already applied to this offer:
        //          button.text = cancel application
        //          button.onclick = fun cancel application
        //      else:
        //          button.text = apply
        //          button.onclick = fun apply



        val offer_event_listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                offer = snapshot.getValue(Offer::class.java)!!

                m_db_ref.child("companies").child(offer.company_id!!).get().addOnSuccessListener {

                    company = it.getValue(Company::class.java)!!

                    tv_company_name.text = company.company_name
                    tv_company_sector.text = company.sector
                    tv_job_description.text = offer.job_description
                    tv_position.text = offer.position
                    tv_location.text = offer.location
                    tv_skills_required.text = offer.skills_required
                    tv_languages_required.text = offer.languages_required
                    tv_edu_exp_required.text = offer.edu_exp_required

                    if (company.img_profile_url != null) {
                        var profile_image_ref =
                            storage_ref.child(company.img_profile_url!!)

                        var local_file = File.createTempFile("tempImage", "jpg")
                        profile_image_ref.getFile(local_file).addOnSuccessListener {
                            val bitmap = BitmapFactory.decodeFile(local_file.absolutePath)
                            iv_logo.setImageBitmap(bitmap)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OfferDescription, "Something went wrong...", Toast.LENGTH_LONG)
                finish()
            }
        }

        m_db_ref.child("offers").child(offer_id).addValueEventListener(offer_event_listener)

        btn_edit.setOnClickListener {
            var intent = Intent(this, OfferEdit::class.java)
            intent.putExtra("offer_id", offer_id)
            startActivity(intent)
        }
    }

}
