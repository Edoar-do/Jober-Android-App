package com.example.jober.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.jober.MainActivity
import com.example.jober.OfferCreation
import com.example.jober.R
import com.example.jober.model.Company
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class CompanyOptionsFragment : Fragment() {

    lateinit var btn_logout : Button
    lateinit var btn_company_profile : Button
    lateinit var btn_new_offer : Button
    lateinit var btn_company_offers : Button
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
        (activity as MainActivity).supportActionBar!!.title = "Jober - Company Options"
        return inflater.inflate(R.layout.fragment_company_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_logout = view?.findViewById(R.id.btn_logout)
        btn_company_profile = view?.findViewById(R.id.btn_company_profile)
        btn_new_offer = view?.findViewById(R.id.btn_new_offer)
        btn_company_offers = view?.findViewById(R.id.btn_company_offers)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        iv_profile = view?.findViewById(R.id.iv_profile)

        btn_logout.setOnClickListener {
            (activity as MainActivity).logout()
        }

        btn_company_profile.setOnClickListener {
            (activity as MainActivity).setFragmentByTitle("CompanyProfile", null)
        }

        btn_new_offer.setOnClickListener {
            var intent = Intent(activity, OfferCreation::class.java)
            startActivity(intent)
        }

        btn_company_offers.setOnClickListener {
            (activity as MainActivity).setFragmentByTitle("CompanyOffers", null)
        }


        var company_id = m_auth.currentUser?.uid!!

        val company_value_listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                company = snapshot.getValue(Company::class.java)!!

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
    }
}
