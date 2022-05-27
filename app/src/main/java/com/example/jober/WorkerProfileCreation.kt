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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

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
    lateinit var image_uri : Uri
    lateinit var storage_ref : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_profile_creation)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        m_db_ref = FirebaseDatabase.getInstance().getReference()

        btn_save = findViewById(R.id.btn_save)
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
            image_uri = data.data!!
            iv_profile.setImageURI(image_uri)
        }
    }

    fun uploadImageToFirebase(image_uri : Uri) : String?{
        var profile_pic_url : String? = null
        val file_ref = storage_ref.child("image_profile_" + m_auth.currentUser?.uid!! + getFileExtension(image_uri))
        file_ref.putFile(image_uri).addOnSuccessListener{
            file_ref.downloadUrl.addOnSuccessListener {
                profile_pic_url = it.toString()
            }.addOnFailureListener{
                Toast.makeText(this, "Failure in Dowloading URL", Toast.LENGTH_LONG)
            }
        }.addOnFailureListener{
            Toast.makeText(this, "Failure Input File", Toast.LENGTH_LONG)
        }
        return profile_pic_url
    }

    fun getFileExtension(uri : Uri) : String? {
        val resolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(resolver.getType(uri))
    }

    fun save(view : View) {
        //instance creation

        val name = edt_name.text.toString()
        val surname = edt_surname.text.toString()
        val age = edt_age.text.toString().toInt()
        val bio = edt_bio.text.toString()
        val main_profession = edt_main_profession.text.toString()
        val country = edt_country.text.toString()
        val city = edt_city.text.toString()
        val skills = edt_skills.text.toString()
        val languages = edt_languages.text.toString()
        val educational_experiences = edt_educational_experiences.text.toString()
        var profile_image_url : String? = null

        if(image_uri != null){
            profile_image_url = uploadImageToFirebase(image_uri)
        }

        val worker = Worker(name, surname,
            age, skills, languages, educational_experiences, profile_image_url, bio, main_profession)

        m_db_ref.child("workers").child(m_auth.currentUser?.uid!!).setValue(worker)


        //fragment switch
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra("fragment", "WorkerProfile")    // specifichiamo il destination fragment
        finish()
        startActivity(intent)
    }
}