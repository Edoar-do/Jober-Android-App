package com.example.jober

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.jober.model.Company
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class CompanyProfileEdit : AppCompatActivity() {

    lateinit var btn_save : Button
    lateinit var btn_cancel : Button
    lateinit var edt_name : EditText
    lateinit var edt_sector : EditText
    lateinit var edt_country : EditText
    lateinit var edt_city : EditText
    lateinit var edt_description : EditText
    lateinit var change_photo_link : TextView
    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var image_uri_local : Uri
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase
    lateinit var company : Company
    lateinit var iv_profile : ImageView

    var image_path_db : String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_profile_edit)
        supportActionBar!!.title = "Jober - Edit Profile"

        btn_save = findViewById(R.id.btn_save_company)
        btn_cancel = findViewById(R.id.btn_cancel)
        edt_name = findViewById(R.id.edt_name)
        edt_sector = findViewById(R.id.edt_sector)
        edt_country = findViewById(R.id.edt_country)
        edt_city = findViewById(R.id.edt_city)
        edt_description = findViewById(R.id.edt_description)
        change_photo_link = findViewById(R.id.change_photo_link)
        image_uri_local = Uri.EMPTY
        iv_profile = findViewById(R.id.iv_profile)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        //IMPOSTAZIONI CAMPI DI EDIT A VALORI PESCATI DAL DB

        var company_id = m_auth.currentUser?.uid!!
        m_db_ref.child("companies").child(company_id).get().addOnSuccessListener {
            company = it.getValue(Company::class.java)!!

            edt_name.setText(company.company_name)
            edt_sector.setText(company.sector)
            edt_country.setText(company.country)
            edt_city.setText(company.city)
            edt_description.setText(company.description)
            image_path_db = company.img_profile_url

            if (image_path_db != null) {
                var profile_image_ref = storage_ref.child(company.img_profile_url!!)

                var local_file = File.createTempFile("tempImage", "jpg")
                profile_image_ref.getFile(local_file).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(local_file.absolutePath)
                    iv_profile.setImageBitmap(bitmap)
                }
            }
        }

        btn_save.setOnClickListener {
            save(it)
        }

        btn_cancel.setOnClickListener {
            finish()
        }
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

    fun save(view : View?){
        val company_name = edt_name.text.toString()
        val sector = edt_sector.text.toString()
        val country = edt_country.text.toString()
        val city = edt_city.text.toString()
        val description = edt_description.text.toString()

        var error_present = false

        if (company_name.isEmpty()) {
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
                    val company = Company(company_name, sector, country, city, description, image_path_db)

                    val company_values = company.toMap()
                    val company_key = m_auth.currentUser?.uid!!
                    val company_updates = hashMapOf<String, Any>(
                        "/companies/$company_key" to company_values
                    )

                    m_db_ref.updateChildren(company_updates)

                    //fragment switch
                    intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("fragment", "CompanyProfile")    // specifichiamo il destination fragment
                    finish()
                    startActivity(intent)
                }.addOnFailureListener{
                    Toast.makeText(this, "Failure Input File", Toast.LENGTH_LONG)
                }
            }else {
                println("$$$$$$$$$$$$$$$$$$$$$$$$ non ho cambiato l'immagine")
                val company = Company(company_name, sector, country, city, description, image_path_db)
                val company_values = company.toMap()
                val company_key = m_auth.currentUser?.uid!!
                val company_updates = hashMapOf<String, Any>(
                    "/companies/$company_key" to company_values
                )

                m_db_ref.updateChildren(company_updates)

                //fragment switch
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fragment", "CompanyProfile")    // specifichiamo il destination fragment
                finish()
                startActivity(intent)
            }
        }



    }

}