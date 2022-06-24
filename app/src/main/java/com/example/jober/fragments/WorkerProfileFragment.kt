package com.example.jober.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.jober.MainActivity
import com.example.jober.R
import com.example.jober.WorkerProfileEdit
import com.example.jober.model.Worker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class WorkerProfileFragment : Fragment() {

    lateinit var btn_edit : ImageButton
    lateinit var btn_contact : Button
    lateinit var tv_name : TextView
    lateinit var tv_surname : TextView
    lateinit var tv_age : TextView
    lateinit var tv_country : TextView
    lateinit var tv_city : TextView
    lateinit var tv_main_profession : TextView
    lateinit var tv_bio : TextView
    lateinit var tv_skills : TextView
    lateinit var tv_languages : TextView
    lateinit var tv_educational_experiences : TextView
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
        (activity as MainActivity).supportActionBar!!.title = "Jober - My Profile"
        return inflater.inflate(R.layout.fragment_worker_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        btn_edit = view?.findViewById(R.id.btn_edit)
        tv_name = view?.findViewById(R.id.tv_name)
        tv_surname = view?.findViewById(R.id.tv_surname)
        tv_age = view?.findViewById(R.id.tv_age)
        tv_country = view?.findViewById(R.id.tv_country)
        tv_city = view?.findViewById(R.id.tv_city)
        tv_main_profession = view?.findViewById(R.id.tv_main_profession)
        tv_bio = view?.findViewById(R.id.tv_bio)
        tv_skills = view?.findViewById(R.id.tv_skills)
        tv_languages = view?.findViewById(R.id.tv_languages)
        tv_educational_experiences = view?.findViewById(R.id.tv_educational_experiences)
        iv_profile = view?.findViewById(R.id.iv_profile)
        btn_contact = view?.findViewById(R.id.btn_contact)

        btn_contact.visibility = View.GONE


        var worker_id = m_auth.currentUser?.uid!!

        var worker_event_listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                worker = snapshot.getValue(Worker::class.java)!!

                tv_name.text = worker.name
                tv_surname.text = worker.surname
                tv_age.text = worker.age.toString()
                tv_country.text = worker.country
                tv_city.text = worker.city
                tv_main_profession.text = worker.main_profession
                tv_bio.text = worker.bio
                tv_skills.text = worker.skills
                tv_languages.text = worker.languages
                tv_educational_experiences.text = worker.educational_experiences

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
                (activity as MainActivity).setFragmentByTitle("Options", null)
            }
        }
        m_db_ref.child("workers").child(worker_id).addValueEventListener(worker_event_listener)

        btn_edit.setOnClickListener {
            var intent = Intent(activity, WorkerProfileEdit::class.java)
            startActivity(intent)
        }
    }

}
