package com.example.jober.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jober.R
import com.example.jober.adapters.OfferAdapter
import com.example.jober.model.Company
import com.example.jober.model.Offer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class OffersFragment : Fragment() {

    lateinit var edt_search : EditText
    lateinit var btn_search : Button
    lateinit var offer_recycler_view : RecyclerView
    lateinit var search_view : SearchView

    lateinit var offer_list : ArrayList<Offer>
    lateinit var company_logos : ArrayList<Bitmap>
    lateinit var company_names : ArrayList<String>

    lateinit var offer_adapter : OfferAdapter

    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        offer_list = ArrayList()
        company_logos = ArrayList()
        company_names = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view : View = inflater.inflate(R.layout.fragment_recycler_view, container, false)

        offer_adapter = OfferAdapter(view.context, offer_list, company_logos, company_names)

        offer_recycler_view = view.findViewById(R.id.recyclerview)
//        println("############################# this is the recyclerview: " + offer_recycler_view)
        offer_recycler_view.layoutManager = LinearLayoutManager(view.context)
        offer_recycler_view.adapter = offer_adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

//        edt_search = view.findViewById(R.id.edt_search)
//        btn_search = view.findViewById(R.id.btn_search)
        search_view = view.findViewById(R.id.searchView)
        search_view.clearFocus()
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                filterList(p0)
                return true
            }

        })

        m_db_ref.child("offers").orderByChild("created_at").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                print("################################## ON DATA CHANGED CALLED")
                offer_list.clear()
                company_logos.clear()
                company_names.clear()
//                offer_adapter.setFilteredLists(offer_list, company_names, company_logos)

                for (postSnapshot in snapshot.children) {
                    val current_offer = postSnapshot.getValue(Offer::class.java)

                    val company_id = current_offer!!.company_id
                    var company_name : String? = null
                    var company_logo_url : String? = null
                    var company_logo : Bitmap? = null

                    m_db_ref.child("companies").child(company_id!!).get().addOnSuccessListener {
                        val company = it.getValue(Company::class.java)
                        company_name = company?.company_name
                        company_logo_url = company?.img_profile_url

                        if (company_logo_url != null) {
                            var profile_image_ref = storage_ref.child(company_logo_url!!)

                            var local_file = File.createTempFile("tempImage", "jpg")
                            profile_image_ref.getFile(local_file).addOnSuccessListener {
                                company_logo = BitmapFactory.decodeFile(local_file.absolutePath)
                                offer_list.add(current_offer)
                                company_logos.add(company_logo!!)
                                company_names.add(company_name!!)
                                offer_adapter.notifyItemInserted(offer_list.size-1)
                            }
                        }else {
                            company_logo = BitmapFactory.decodeResource(resources, R.drawable.user_profile_placeholder)
                            offer_list.add(current_offer)
                            company_logos.add(company_logo!!)
                            company_names.add(company_name!!)
                            offer_adapter.notifyItemInserted(offer_list.size-1)
                        }

                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun filterList(search_text : String?) {
        var offers_filtered_list = ArrayList<Offer>()
        var company_names_filtered_list = ArrayList<String>()
        var company_logos_filtered_list = ArrayList<Bitmap>()
        val list_of_words = search_text!!.split(" ")

        for (i in offer_list.indices) {
            var to_get = true
            for (word in list_of_words) {
                if (!offer_list.get(i).position!!.contains(word, true) && !company_names.get(i).contains(word, true)) {
                    to_get = false
                }
            }
            if (to_get) {
                offers_filtered_list.add(offer_list.get(i))
                company_names_filtered_list.add(company_names.get(i))
                company_logos_filtered_list.add(company_logos.get(i))
            }
        }

        offer_adapter.setFilteredLists(offers_filtered_list, company_names_filtered_list, company_logos_filtered_list)
    }


}
