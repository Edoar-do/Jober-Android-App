package com.example.jober

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import com.example.jober.model.Company
import com.example.jober.model.Event
import com.example.jober.model.Offer
import com.example.jober.model.Worker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class EventDetails : AppCompatActivity() {

    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase


    lateinit var tv_worker_name : TextView
    lateinit var tv_main_profession : TextView
    lateinit var tv_position : TextView
    lateinit var tv_location : TextView
    lateinit var tv_compnany_name : TextView
    lateinit var tv_date_time : TextView
    lateinit var tv_who : TextView
    lateinit var cv_worker : CardView
    lateinit var cv_position : CardView
    lateinit var iv_company_logo : ImageView
    lateinit var iv_worker_profile : ImageView
    lateinit var bottom_layout : ConstraintLayout
    lateinit var btn_cancel_event : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        tv_worker_name = findViewById(R.id.tv_worker_name)
        tv_main_profession = findViewById(R.id.tv_main_profession)
        tv_position = findViewById(R.id.tv_position)
        tv_location = findViewById(R.id.tv_location)
        tv_compnany_name= findViewById(R.id.tv_company_name)
        tv_date_time = findViewById(R.id.tv_date_time)
        tv_who = findViewById(R.id.tv_who)
        cv_worker = findViewById(R.id.cv_worker)
        cv_position = findViewById(R.id.cv_position)
        iv_company_logo = findViewById(R.id.iv_company_logo)
        iv_worker_profile = findViewById(R.id.iv_worker_profile)
        bottom_layout = findViewById(R.id.bottom_layout)
        btn_cancel_event = findViewById(R.id.btn_cancel_event)

        supportActionBar!!.title = "Jober - Event details"

        val event = intent.getSerializableExtra("event") as Event


        var suffix = ""
        var hour = ""

        if (event.date!!.hours > 13) {
            suffix = "PM"
            hour = (event.date!!.hours - 12).toString()
        } else {
            suffix = "AM"
            hour = (event.date!!.hours).toString()
        }

        tv_date_time.setText("${event.date!!.year}/${event.date!!.month}/${event.date!!.day}  ${hour}:${event.date!!.minutes} ${suffix}")

        val offer_id = event.chat_id!!.split("_", ignoreCase = true, limit = 2)[1]

        cv_position.setOnClickListener {
            val intent = Intent(this, OfferDescription()::class.java)
            intent.putExtra("offer_id", offer_id)
            startActivity(intent)
        }

        m_db_ref.child("offers").child(offer_id).get().addOnSuccessListener {
            if (it.exists()) {
                val offer = it.getValue(Offer::class.java)

                val company_id = offer!!.company_id
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
                            tv_compnany_name.setText(company_name)
                            tv_position.setText(offer.position)
                            tv_location.setText(offer.location)
                            iv_company_logo.setImageBitmap(company_logo)

                        }
                    }else {
                        company_logo = BitmapFactory.decodeResource(resources, R.drawable.user_profile_placeholder)
                        tv_compnany_name.setText(company_name)
                        tv_position.setText(offer.position)
                        tv_location.setText(offer.location)
                        iv_company_logo.setImageBitmap(company_logo)
                    }
                }
            }
        }

        m_db_ref.child("userSettings").child(m_auth.currentUser?.uid!!).child("user_type").get().addOnSuccessListener {
            if (it.exists()) {
                if (it.value!!.equals("worker")) { // sono un worker, non devo vedere la card del "who"
                    tv_who.visibility = View.GONE
                    cv_worker.visibility = View.GONE
                    bottom_layout.visibility = View.GONE
                } else { // sono una company

                    val worker_id = event.chat_id!!.split("_")[0]

                    m_db_ref.child("workers").child(worker_id).get().addOnSuccessListener {
                        val worker = it.getValue(Worker::class.java)

                        cv_worker.setOnClickListener {
                            val intent : Intent = Intent(this, ApplicantProfile::class.java)
                            intent.putExtra("application_id", event.chat_id)
                            intent.putExtra("worker", worker)
                            startActivity(intent)
                        }

                        var worker_profile_pic_url = worker?.img_profile_url

                        if (worker_profile_pic_url != null) {
                            var profile_image_ref = storage_ref.child(worker_profile_pic_url!!)

                            var local_file = File.createTempFile("tempImage", "jpg")
                            profile_image_ref.getFile(local_file).addOnSuccessListener {
                                var worker_profile_pic = BitmapFactory.decodeFile(local_file.absolutePath)
                                iv_worker_profile.setImageBitmap(worker_profile_pic)
                                tv_worker_name.setText(worker!!.name)
                                tv_main_profession.setText(worker!!.main_profession)
                            }
                        }else {
                            var worker_profile_pic = BitmapFactory.decodeResource(resources, R.drawable.user_profile_placeholder)
                            iv_worker_profile.setImageBitmap(worker_profile_pic)
                            tv_worker_name.setText(worker!!.name)
                            tv_main_profession.setText(worker!!.main_profession)
                        }
                    }


                    btn_cancel_event.setOnClickListener {

                        var builder = AlertDialog.Builder(this)
                        builder.setTitle("Confirm cancellation")
                        builder.setMessage("Do you really want to cancel this event?")
                        builder.setPositiveButton("Yes",
                            DialogInterface.OnClickListener{ dialog, id ->
                            //chiamata al db per distruggere il record
                            m_db_ref.child("events").child(event.event_id!!).removeValue()
                            dialog.cancel()
                            finish()
                        })
                        builder.setNegativeButton("No", DialogInterface.OnClickListener{ dialog, id ->
                            dialog.cancel()
                        })
                        var alert : AlertDialog = builder.create()
                        alert.show()

                    }

                }
            }
        }
    }
}