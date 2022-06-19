package com.example.jober

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.jober.model.UserSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SignUp : AppCompatActivity() {

    private lateinit var edt_email : EditText
    private lateinit var edt_password : EditText
    private lateinit var btn_sign_up : Button
    private lateinit var mAuth : FirebaseAuth
    private lateinit var radio_btn_company : RadioButton
    private lateinit var radio_btn_worker : RadioButton
    private lateinit var radioGroup: RadioGroup
    private lateinit var m_db_ref: DatabaseReference
    private lateinit var database : FirebaseDatabase
    private lateinit var tv_signin : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        edt_email = findViewById(R.id.edt_email)
        edt_password = findViewById(R.id.edt_password)
        btn_sign_up = findViewById(R.id.btn_sign_up)
        radioGroup = findViewById(R.id.radioGroup)
        radio_btn_company = findViewById(R.id.radio_btn_company)
        radio_btn_worker = findViewById(R.id.radio_btn_worker)
        tv_signin = findViewById(R.id.tv_signin)

        mAuth = FirebaseAuth.getInstance()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()


        radio_btn_worker.isChecked = true
        radio_btn_company.isChecked = false

        tv_signin.setOnClickListener {
            goToLogin(it)
        }


        btn_sign_up.setOnClickListener {
            val email = edt_email.text.toString()
            val password = edt_password.text.toString()


            var error_present = false

            if (email.isEmpty()) {
                edt_email.error = "Please enter your email"
                error_present = true
            }
            if (password.isEmpty()) {
                edt_password.error = "Please enter a password"
                error_present = true
            }

            if (!error_present) {
                signup(email, password)
            }
        }
    }

    private fun signup(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    when(radioGroup.checkedRadioButtonId) {
                        R.id.radio_btn_worker -> {
                            val user_settings = UserSettings("worker")
                            m_db_ref.child("userSettings").child(mAuth.currentUser?.uid!!).setValue(user_settings)
                            val intent = Intent(this@SignUp, WorkerProfileCreation::class.java)
                            finish()
                            startActivity(intent)
                        }
                        R.id.radio_btn_company -> {
                            val user_settings = UserSettings("company")
                            m_db_ref.child("userSettings").child(mAuth.currentUser?.uid!!).setValue(user_settings)
                            val intent = Intent(this@SignUp, CompanyProfileCreation::class.java)
                            finish()
                            startActivity(intent)
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("JoberError", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun goToLogin(v : View) {
        val intent : Intent = Intent(this, Login::class.java)
        finish()
        startActivity(intent)
    }
}