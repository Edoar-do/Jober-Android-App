package com.example.jober

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jober.adapters.OfferApplicantAdapter
import com.example.jober.model.Application
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

    lateinit var worker_list : ArrayList<Worker>
    lateinit var worker_pics : ArrayList<Bitmap>

    lateinit var applicant_adapter : OfferApplicantAdapter

    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase

    var offer_id : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_recycler_view)

        worker_list = ArrayList()
        worker_pics = ArrayList()

        applicant_adapter = OfferApplicantAdapter(this, worker_list, worker_pics)

        applicants_recycler_view = findViewById(R.id.recyclerview)
//        println("############################# this is the recyclerview: " + offer_recycler_view)
        applicants_recycler_view.layoutManager = LinearLayoutManager(this)
        applicants_recycler_view.adapter = applicant_adapter


        offer_id = intent.getStringExtra("offer_id")

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        edt_search = findViewById(R.id.edt_search)
        btn_search = findViewById(R.id.btn_search)

        val user_id = m_auth.currentUser?.uid!!

        m_db_ref.child("applications").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                worker_list.clear()
                worker_pics.clear()

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
                                    //                                println("####################### data added to offer list, elements: ")
                                    worker_pics.add(applicant_profile_pic!!)
                                    applicant_adapter.notifyDataSetChanged()
                                    //                                println("################################## the data changed, adapter has been notified")
                                }
                            }else {
                                applicant_profile_pic = BitmapFactory.decodeResource(resources, R.drawable.user_profile_placeholder)
                                worker_list.add(applicant!!)
                                worker_pics.add(applicant_profile_pic!!)
                                //                            println("#############################################" + offer_adapter.company_logos.size)
                                applicant_adapter.notifyDataSetChanged()
                                //                            println("################################## the data changed, adapter has been notified")
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}