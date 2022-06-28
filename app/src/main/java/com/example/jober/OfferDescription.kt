package com.example.jober

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.jober.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class OfferDescription : AppCompatActivity() {

    lateinit var btn_edit : ImageButton
    lateinit var tv_company_name : TextView
    lateinit var tv_company_sector : TextView
    lateinit var tv_position : TextView
    lateinit var tv_location : TextView
    lateinit var tv_job_description : TextView
    lateinit var tv_skills_required : TextView
    lateinit var tv_languages_required : TextView
    lateinit var tv_edu_exp_required : TextView
    lateinit var iv_logo : ImageView
    lateinit var btn_bottom : Button
    lateinit var btn_delete : ImageButton
    lateinit var bottom_layout : ConstraintLayout

    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase

    lateinit var offer : Offer
    lateinit var company : Company
    lateinit var offer_id : String

    lateinit var user_type : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_description)
        supportActionBar!!.title = "Jober - Offer Details"

        btn_delete = findViewById(R.id.btn_delete)
        btn_edit = findViewById(R.id.btn_edit)
        tv_company_name = findViewById(R.id.tv_company_name)
        tv_company_sector = findViewById(R.id.tv_company_sector)
        tv_position = findViewById(R.id.tv_position)
        tv_location = findViewById(R.id.tv_location)
        tv_job_description = findViewById(R.id.tv_job_description)
        tv_skills_required = findViewById(R.id.tv_skills_required)
        tv_languages_required = findViewById(R.id.tv_languages_required)
        tv_edu_exp_required = findViewById(R.id.tv_edu_exp_required)
        iv_logo = findViewById(R.id.iv_logo)
        bottom_layout = findViewById(R.id.bottom_layout)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database =
            Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        offer_id = intent.getStringExtra("offer_id").toString()

        btn_delete.setOnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm deletion")
            builder.setMessage("Do you really want to delete this offer?")
            builder.setPositiveButton("Delete",DialogInterface.OnClickListener{ dialog, id ->
                //chiamata al db per distruggere il record
                m_db_ref.child("offers").child(offer_id).removeValue()

                //rimuovere tutte le application associate alla offer rimossa
                m_db_ref.child("applications").get().addOnCompleteListener {
                    for (app in it.result.children) {
                        var application = app.getValue(Application::class.java)
                        if (application!!.offer_id == offer_id) {
                            app.ref.removeValue()
                        }
                    }
                }

                //rimuovere tutte le chat associate alla offer rimossa
                m_db_ref.child("chats").get().addOnCompleteListener {
                    for (chat in it.result.children) {
                        var ch = chat.getValue(Chat::class.java)
                        if (ch!!.chat_id!!.endsWith(offer_id)) {
                            chat.ref.removeValue()
                        }
                    }
                }

                //rimuovere tutti gli eventi associati alla offer rimossa
                m_db_ref.child("events").get().addOnCompleteListener {
                    for (ev in it.result.children) {
                        var event = ev.getValue(Event::class.java)
                        if (event!!.chat_id!!.endsWith(offer_id)) {
                            ev.ref.removeValue()
                        }
                    }
                }




                dialog.cancel()
                finish()
            })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialog, id ->
                dialog.cancel()
            })
            var alert : AlertDialog = builder.create()
            alert.show()
        }


        // get references to buttons 'view applicants', 'apply', 'cancel application'
        btn_bottom = findViewById(R.id.btn_bottom)
        // get user type
        val user_id = m_auth.currentUser?.uid!!


        val offer_event_listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    offer = snapshot.getValue(Offer::class.java)!!


                    m_db_ref.child("userSettings").child(user_id).get().addOnSuccessListener {
                        val user_settings = it.getValue(UserSettings::class.java)
                        user_type = user_settings?.user_type!!

                        if (user_type.equals("company")) {
                            if (offer.company_id == user_id) {
                                btn_bottom.visibility = View.VISIBLE
                                btn_bottom.text = "show applicants"
                                btn_bottom.setOnClickListener {
                                    show_applicants(offer_id)
                                }
                                btn_edit.visibility = View.VISIBLE
                                btn_delete.visibility = View.VISIBLE
                            } else {
                                bottom_layout.visibility = View.GONE
                                btn_edit.visibility = View.GONE
                                btn_delete.visibility = View.GONE
                            }
                        } else { //user is a worker
                            btn_bottom.visibility = View.VISIBLE
                            btn_edit.visibility = View.GONE
                            btn_delete.visibility = View.GONE

                            m_db_ref.child("applications").child(user_id + "_" + offer_id).get().addOnSuccessListener {
                                if (it.exists()) {  // the worker has already applied to this offer
                                    btn_bottom.text = "withdraw"
                                    btn_bottom.setOnClickListener {
                                        withdraw(user_id, offer_id)
                                    }
                                } else {    // the worker has not applied to this offer
                                    btn_bottom.text = "apply"
                                    btn_bottom.setOnClickListener{
                                        apply_to_offer(user_id, offer_id)
                                    }
                                }
                            }
                        }
                    }


                    m_db_ref.child("companies").child(offer.company_id!!).get().addOnSuccessListener {

                        company = it.getValue(Company::class.java)!!

                        tv_company_name.text = company.company_name
                        tv_company_sector.text = company.sector
                        tv_job_description.text = offer.job_description
                        tv_position.text = offer.position
                        tv_location.text = offer.location
                        tv_skills_required.text = offer.skills_required
                        tv_languages_required.text = offer.languages_required
                        tv_edu_exp_required.text = offer.edu_exp_required

                        if (company.img_profile_url != null) {
                            var profile_image_ref =
                                storage_ref.child(company.img_profile_url!!)

                            var local_file = File.createTempFile("tempImage", "jpg")
                            profile_image_ref.getFile(local_file).addOnSuccessListener {
                                val bitmap = BitmapFactory.decodeFile(local_file.absolutePath)
                                iv_logo.setImageBitmap(bitmap)
                            }
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OfferDescription, "Something went wrong...", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        m_db_ref.child("offers").child(offer_id).addValueEventListener(offer_event_listener)

        btn_edit.setOnClickListener {
            var intent = Intent(this, OfferEdit::class.java)
            intent.putExtra("offer_id", offer_id)
            startActivity(intent)
        }
    }

    private fun apply_to_offer(userId: String, offerId: String) {
        val application = Application(userId + "_" + offerId, userId, offerId)
        m_db_ref.child("applications").child(userId + "_" + offerId).setValue(application).addOnCompleteListener {
            Toast.makeText(this, "You have successfully applied for this position", Toast.LENGTH_LONG).show()
            btn_bottom.text = "withdraw"
            btn_bottom.setOnClickListener { withdraw(userId, offerId) }
        }
    }

    private fun withdraw(userId: String, offerId: String) {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm withdrawal")
        builder.setMessage("Do you really want to withdraw this application?")
        builder.setPositiveButton("withdraw",DialogInterface.OnClickListener{ dialog, id ->
            //chiamata al db per distruggere il record
            val application_id = userId + "_" + offerId
            m_db_ref.child("applications").child(application_id).removeValue().addOnCompleteListener {
                btn_bottom.text = "apply"
                btn_bottom.setOnClickListener { apply_to_offer(userId, offerId) }
            }

            m_db_ref.child("chats").child(application_id).removeValue()

            m_db_ref.child("events").orderByKey().startAt(application_id).endAt(application_id + "\uf8ff").get().addOnSuccessListener {
                for (snapshot in it.children) {
                    snapshot.ref.removeValue()
                }
            }

            dialog.cancel()
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialog, id ->
            dialog.cancel()
        })
        var alert : AlertDialog = builder.create()
        alert.show()
    }

    private fun show_applicants(offerId: String) {
        val intent = Intent(this@OfferDescription, OfferApplicants::class.java)
        intent.putExtra("offer_id", offerId)
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


}
