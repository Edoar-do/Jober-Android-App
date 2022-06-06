package com.example.jober

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import com.example.jober.model.Worker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

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
    lateinit var iv_profile : ImageView
    lateinit var change_photo_link : TextView
    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase

    lateinit var image_uri_local : Uri
    var image_path_db : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_profile_creation)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
//        m_db_ref = FirebaseDatabase.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()


        btn_save = findViewById(R.id.btn_save_company)
        edt_name = findViewById(R.id.edt_name)
        edt_surname = findViewById(R.id.edt_sector)
        edt_age = findViewById(R.id.edt_age)
        edt_country = findViewById(R.id.edt_country)
        edt_city = findViewById(R.id.edt_city)
        edt_main_profession = findViewById(R.id.edt_main_profession)
        edt_bio = findViewById(R.id.edt_description)
        edt_skills = findViewById(R.id.edt_skills)
        edt_languages = findViewById(R.id.edt_languages)
        edt_educational_experiences = findViewById(R.id.edt_educational_experiences)
        iv_profile = findViewById(R.id.iv_profile)
        change_photo_link = findViewById(R.id.change_photo_link)
        image_uri_local = Uri.EMPTY
    }

    fun photoPicker(view: View){
        val gallery_intent = Intent()
        gallery_intent.setAction(Intent.ACTION_GET_CONTENT)
        gallery_intent.setType("image/*")
        startActivityForResult(gallery_intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            image_uri_local = data.data!!
            iv_profile.setImageURI(image_uri_local)
        }
    }

    fun uploadImageToFirebase(image_uri : Uri){
        image_path_db = "images/worker_profile/" + m_auth.currentUser?.uid!! + "_" + System.currentTimeMillis().toString()
        val file_ref = storage_ref.child(image_path_db!!)
        file_ref.putFile(image_uri).addOnFailureListener{
            Toast.makeText(this, "Failure Input File", Toast.LENGTH_LONG)
        }
    }

    fun save(view : View) {
        //instance creation
        val name = edt_name.text.toString()
        val surname = edt_surname.text.toString()
        val bio = edt_bio.text.toString()
        val main_profession = edt_main_profession.text.toString()
        val country = edt_country.text.toString()
        var age = 0
        val city = edt_city.text.toString()
        val skills = edt_skills.text.toString()
        val languages = edt_languages.text.toString()
        val educational_experiences = edt_educational_experiences.text.toString()

        var error_present = false

        if (name.isEmpty()) {
            edt_name.error = "Please enter a name"
            error_present = true
        }

        if (surname.isEmpty()) {
            edt_surname.error = "Please enter a surname"
            error_present = true
        }

        if (edt_age.text.toString() == "") {
            edt_age.error = "Please enter your age"
            error_present = true
        }else {
            var age_string = edt_age.text.toString()
            if (age_string.toIntOrNull() != null) {
                age = edt_age.text.toString().toInt();
            }else {
                edt_age.error = "The age must be an number"
                error_present = true
            }
        }

        if (bio.isEmpty()) {
            edt_bio.error = "Please enter a bio"
            error_present = true
        }

        if (main_profession.isEmpty()) {
            edt_main_profession.error = "Please enter your main profession"
            error_present = true
        }

        if (country.isEmpty()) {
            edt_country.error = "Please enter your country"
            error_present = true
        }

        if (city.isEmpty()) {
            edt_city.error = "Please enter your city"
            error_present = true
        }

        if (!error_present) {
            if(image_uri_local != Uri.EMPTY) {
                image_path_db = "images/worker_profile/" + m_auth.currentUser?.uid!! + "_" + System.currentTimeMillis().toString()
                val file_ref = storage_ref.child(image_path_db!!)
                file_ref.putFile(image_uri_local).addOnSuccessListener {
                    val worker = Worker(name, surname,
                        age, country, city, skills, languages, educational_experiences, image_path_db, bio, main_profession)


                    m_db_ref.child("workers").child(m_auth.currentUser?.uid!!).setValue(worker)

                    //fragment switch
                    intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("fragment", "WorkerProfile")    // specifichiamo il destination fragment
                    finish()
                    startActivity(intent)
                }.addOnFailureListener{
                    Toast.makeText(this, "Failure Input File", Toast.LENGTH_LONG)
                }
            }else {
                val worker = Worker(name, surname,
                    age, country, city, skills, languages, educational_experiences, image_path_db, bio, main_profession)


                m_db_ref.child("workers").child(m_auth.currentUser?.uid!!).setValue(worker)

                //fragment switch
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fragment", "WorkerProfile")    // specifichiamo il destination fragment
                finish()
                startActivity(intent)
            }

        }


    }
}