package com.example.jober.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.jober.*
import com.example.jober.R
import com.example.jober.model.Company
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class CompanyProfileFragment : Fragment() {

    lateinit var btn_edit : ImageButton
    lateinit var tv_company_name : TextView
    lateinit var tv_sector : TextView
    lateinit var tv_country : TextView
    lateinit var tv_city : TextView
    lateinit var tv_description : TextView
    lateinit var iv_profile : ImageView

    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase

    lateinit var company : Company

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as MainActivity).supportActionBar!!.title = "Jober - Company Profile"
        return inflater.inflate(R.layout.fragment_company_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_edit = view?.findViewById(R.id.btn_edit)
        tv_company_name = view?.findViewById(R.id.tv_company_name)
        tv_sector = view?.findViewById(R.id.tv_sector)
        tv_country = view?.findViewById(R.id.tv_country)
        tv_city = view?.findViewById(R.id.tv_city)
        tv_description = view?.findViewById(R.id.tv_description)
        iv_profile = view?.findViewById(R.id.iv_profile)


        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        var company_id = m_auth.currentUser?.uid!!

        val company_value_listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                company = snapshot.getValue(Company::class.java)!!

                tv_company_name.text = company.company_name
                tv_sector.text = company.sector
                tv_country.text = company.country
                tv_city.text = company.city
                tv_description.text = company.description

                if (company.img_profile_url != null) {
                    var profile_image_ref = storage_ref.child(company.img_profile_url!!)

                    var local_file = File.createTempFile("tempImage", "jpg")
                    profile_image_ref.getFile(local_file).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(local_file.absolutePath)
                        iv_profile.setImageBitmap(bitmap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Something went wrong...", Toast.LENGTH_LONG)
                (activity as MainActivity).setFragmentByTitle("Options", null)
            }
        }

        m_db_ref.child("companies").child(company_id).addValueEventListener(company_value_listener)

        btn_edit.setOnClickListener {
            var intent = Intent(activity, CompanyProfileEdit::class.java)
            startActivity(intent)
        }
    }

}
