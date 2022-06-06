package com.example.jober

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import com.example.jober.model.Company
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CompanyProfileCreation : AppCompatActivity() {

    lateinit var btn_save : Button
    lateinit var edt_name : EditText
    lateinit var edt_sector : EditText
    lateinit var edt_country : EditText
    lateinit var edt_city : EditText
    lateinit var edt_description : EditText
    lateinit var iv_profile : ImageView
    lateinit var change_photo_link : TextView
    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var image_uri_local : Uri
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase
    var image_path_db : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_company_profile_creation)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
//        m_db_ref = FirebaseDatabase.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        btn_save = findViewById(R.id.btn_save_company)
        edt_name = findViewById(R.id.edt_name)
        edt_sector = findViewById(R.id.edt_sector)
        edt_country = findViewById(R.id.edt_country)
        edt_city = findViewById(R.id.edt_city)
        edt_description = findViewById(R.id.edt_description)
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

    fun save(view : View) {
        //instance creation
        val name = edt_name.text.toString()
        val sector = edt_sector.text.toString()
        val country = edt_country.text.toString()
        val city = edt_city.text.toString()
        val description = edt_description.text.toString()

        var error_present = false

        if (name.isEmpty()) {
            edt_name.error = "Please enter a name"
            error_present = true
        }
        if (sector.isEmpty()) {
            edt_sector.error = "Please enter a sector"
            error_present = true
        }
        if (country.isEmpty()) {
            edt_country.error = "Please enter a country"
            error_present = true
        }
        if (city.isEmpty()) {
            edt_city.error = "Please enter a city"
            error_present = true
        }
        if (description.isEmpty()) {
            edt_description.error = "Please enter a description"
            error_present = true
        }

        if (!error_present) {
            if(image_uri_local != Uri.EMPTY) {
                image_path_db = "images/company_profile/" + m_auth.currentUser?.uid!! + "_" + System.currentTimeMillis().toString()
                val file_ref = storage_ref.child(image_path_db!!)
                file_ref.putFile(image_uri_local).addOnSuccessListener {

                    val company = Company(name, sector, country, city, description, image_path_db)

                    m_db_ref.child("companies").child(m_auth.currentUser?.uid!!).setValue(company)

                    //fragment switch
                    intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("fragment", "CompanyProfile")    // specifichiamo il destination fragment
                    finish()
                    startActivity(intent)
                }
            }else {
                val company = Company(name, sector, country, city, description, image_path_db)

                m_db_ref.child("companies").child(m_auth.currentUser?.uid!!).setValue(company)

                //fragment switch
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fragment", "CompanyProfile")    // specifichiamo il destination fragment
                finish()
                startActivity(intent)
            }
        }


    }
}