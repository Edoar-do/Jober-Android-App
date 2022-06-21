package com.example.jober

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jober.adapters.OfferApplicantAdapter
import com.example.jober.model.Application
import com.example.jober.model.Offer
import com.example.jober.model.Worker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class OfferApplicants : AppCompatActivity() {

    lateinit var edt_search : EditText
    lateinit var btn_search : Button
    lateinit var applicants_recycler_view : RecyclerView
    lateinit var search_view : SearchView

    lateinit var worker_list : ArrayList<Worker>
    lateinit var worker_pics : ArrayList<Bitmap>
    lateinit var application_list : ArrayList<Application>

    lateinit var applicant_adapter : OfferApplicantAdapter

    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase

    lateinit var valueEventListener: ValueEventListener

    var offer_id : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_recycler_view)

        worker_list = ArrayList()
        worker_pics = ArrayList()
        application_list = ArrayList()

        applicant_adapter = OfferApplicantAdapter(this, worker_list, worker_pics, application_list)

        applicants_recycler_view = findViewById(R.id.recyclerview)
        applicants_recycler_view.layoutManager = LinearLayoutManager(this)
        applicants_recycler_view.adapter = applicant_adapter


        offer_id = intent.getStringExtra("offer_id")

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        search_view = findViewById(R.id.searchView)
        search_view.clearFocus()
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if(p0.isNullOrEmpty()){
                    applicant_adapter.setFilteredLists(worker_list, worker_pics, application_list)
                }else{
                    filterList(p0)
                }
                applicant_adapter.notifyDataSetChanged()
                return true
            }

        })

        val user_id = m_auth.currentUser?.uid!!


        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                worker_list.clear()
                worker_pics.clear()
                application_list.clear()

                for (applicationSnapshot in snapshot.children) {

                    val application_id = applicationSnapshot.ref.key

                    if(application_id!!.split("_", ignoreCase = false ,limit = 2)[1].equals(offer_id)){

                        val current_application = applicationSnapshot.getValue(Application::class.java)

                        val worker_id = current_application!!.worker_id
                        var applicant_profile_pic_url : String? = null
                        var applicant_profile_pic : Bitmap? = null

                        m_db_ref.child("workers").child(worker_id!!).get().addOnSuccessListener {
                            val applicant = it.getValue(Worker::class.java)
                            applicant_profile_pic_url = applicant?.img_profile_url

                            if (applicant_profile_pic_url != null) {
                                var profile_image_ref = storage_ref.child(applicant_profile_pic_url!!)

                                var local_file = File.createTempFile("tempImage", "jpg")
                                profile_image_ref.getFile(local_file).addOnSuccessListener {
                                    applicant_profile_pic = BitmapFactory.decodeFile(local_file.absolutePath)
                                    worker_list.add(applicant!!)
                                    worker_pics.add(applicant_profile_pic!!)
                                    application_list.add(current_application)
                                    applicant_adapter.notifyItemInserted(worker_list.size-1)
                                    }
                            }else {
                                applicant_profile_pic = BitmapFactory.decodeResource(resources, R.drawable.user_profile_placeholder)
                                worker_list.add(applicant!!)
                                worker_pics.add(applicant_profile_pic!!)
                                application_list.add(current_application)
                                applicant_adapter.notifyItemInserted(worker_list.size-1)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        m_db_ref.child("applications").addValueEventListener(valueEventListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        m_db_ref.child("applications").removeEventListener(valueEventListener)
    }


    private fun filterList(search_text : String?) {
        var workers_filtered_list = ArrayList<Worker>()
        var worker_pics_filtered_list = ArrayList<Bitmap>()
        val list_of_words = search_text!!.split(" ")

        for (i in worker_list.indices) {
            var to_get = true
            for (word in list_of_words) {
                if (!worker_list.get(i).name!!.contains(word, true) && !worker_list.get(i).main_profession!!.contains(word, true)) {
                    to_get = false
                }
            }
            if (to_get) {
                workers_filtered_list.add(worker_list.get(i))
                worker_pics_filtered_list.add(worker_pics.get(i))
            }
        }

        applicant_adapter.setFilteredLists(workers_filtered_list, worker_pics_filtered_list, application_list)
    }
}