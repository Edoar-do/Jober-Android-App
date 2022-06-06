package com.example.jober.fragments

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
import com.example.jober.R
import com.example.jober.model.Worker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class WorkerOptionsFragment : Fragment() {

    lateinit var btn_logout : Button
    lateinit var btn_profile : Button
    lateinit var btn_applications : Button
    lateinit var iv_profile : ImageView
    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase
    lateinit var worker : Worker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_worker_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_logout = view?.findViewById(R.id.btn_logout)!!
        btn_profile = view?.findViewById(R.id.btn_company_profile)
        btn_applications = view?.findViewById(R.id.btn_new_offer)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        iv_profile = view?.findViewById(R.id.iv_profile)

        btn_logout.setOnClickListener {
            (activity as MainActivity).logout()
        }

        btn_profile.setOnClickListener {
            (activity as MainActivity).setFragmentByTitle("WorkerProfile")
        }


        var worker_id = m_auth.currentUser?.uid!!

        var worker_event_listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                worker = snapshot.getValue(Worker::class.java)!!

                if (worker.img_profile_url != null) {
                    var profile_image_ref = storage_ref.child(worker.img_profile_url!!)

                    var local_file = File.createTempFile("tempImage", "jpg")
                    profile_image_ref.getFile(local_file).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(local_file.absolutePath)
                        iv_profile.setImageBitmap(bitmap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Something went wrong...", Toast.LENGTH_LONG)
                (activity as MainActivity).setFragmentByTitle("Options")
            }
        }
        m_db_ref.child("workers").child(worker_id).addValueEventListener(worker_event_listener)
    }
}
