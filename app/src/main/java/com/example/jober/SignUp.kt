package com.example.jober

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {

    private lateinit var edt_email : EditText
    private lateinit var edt_password : EditText
    private lateinit var btn_sign_up : Button
    private lateinit var mAuth : FirebaseAuth
    private lateinit var radio_btn_company : RadioButton
    private lateinit var radio_btn_worker : RadioButton
    private lateinit var radioGroup: RadioGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        edt_email = findViewById(R.id.edt_email)
        edt_password = findViewById(R.id.edt_password)
        btn_sign_up = findViewById(R.id.btn_sign_up)
        radioGroup = findViewById(R.id.radioGroup)
        radio_btn_company = findViewById(R.id.radio_btn_company)
        radio_btn_worker = findViewById(R.id.radio_btn_worker)
        mAuth = FirebaseAuth.getInstance()


        radio_btn_worker.isChecked = true
        radio_btn_company.isChecked = false


        btn_sign_up.setOnClickListener {
            val email = edt_email.text.toString()
            val password = edt_password.text.toString()

            signup(email, password)
        }
    }

    private fun signup(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    when(radioGroup.checkedRadioButtonId) {
                        R.id.radio_btn_worker -> {
                            val intent = Intent(this@SignUp, WorkerProfileCreation::class.java)
                            finish()
                            startActivity(intent)
                        }
                        R.id.radio_btn_company -> {
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
}