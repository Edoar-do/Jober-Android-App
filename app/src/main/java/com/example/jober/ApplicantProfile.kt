package com.example.jober

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.jober.model.Worker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class ApplicantProfile : AppCompatActivity() {

    lateinit var btn_edit : ImageButton
    lateinit var btn_contact : Button
    lateinit var tv_name : TextView
    lateinit var tv_surname : TextView
    lateinit var tv_age : TextView
    lateinit var tv_country : TextView
    lateinit var tv_city : TextView
    lateinit var tv_main_profession : TextView
    lateinit var tv_bio : TextView
    lateinit var tv_skills : TextView
    lateinit var tv_languages : TextView
    lateinit var tv_educational_experiences : TextView
    lateinit var iv_profile : ImageView

    lateinit var worker : Worker

    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_worker_profile)

        val application_id = intent.getStringExtra("application_id")
        worker = intent.getSerializableExtra("worker") as Worker

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        btn_edit = findViewById(R.id.btn_edit)
        tv_name = findViewById(R.id.tv_name)
        tv_surname = findViewById(R.id.tv_surname)
        tv_age = findViewById(R.id.tv_age)
        tv_country = findViewById(R.id.tv_country)
        tv_city = findViewById(R.id.tv_city)
        tv_main_profession = findViewById(R.id.tv_main_profession)
        tv_bio = findViewById(R.id.tv_bio)
        tv_skills = findViewById(R.id.tv_skills)
        tv_languages = findViewById(R.id.tv_languages)
        tv_educational_experiences = findViewById(R.id.tv_educational_experiences)
        iv_profile = findViewById(R.id.iv_profile)
        btn_contact = findViewById(R.id.btn_contact)

        btn_edit.visibility = View.GONE
        tv_name.text = worker.name
        tv_surname.text = worker.surname
        tv_age.text = worker.age.toString()
        tv_country.text = worker.country
        tv_city.text = worker.city
        tv_main_profession.text = worker.main_profession
        tv_bio.text = worker.bio
        tv_skills.text = worker.skills
        tv_languages.text = worker.languages
        tv_educational_experiences.text = worker.educational_experiences

        if (worker.img_profile_url != null) {
            var profile_image_ref = storage_ref.child(worker.img_profile_url!!)

            var local_file = File.createTempFile("tempImage", "jpg")
            profile_image_ref.getFile(local_file).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(local_file.absolutePath)
                iv_profile.setImageBitmap(bitmap)
            }
        }

        btn_contact.setOnClickListener{
            //cambio sulla actvity della singola chat
            val intent : Intent = Intent(this, SingleChat::class.java)
            startActivity(intent)
        }




    }
}