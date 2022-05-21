package com.example.jober

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {

    private lateinit var edt_email : EditText
    private lateinit var edt_password : EditText
    private lateinit var btn_sign_up : Button

    private lateinit var mAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        edt_email = findViewById(R.id.edt_email)
        edt_password = findViewById(R.id.edt_password)
        btn_sign_up = findViewById(R.id.btn_sign_up)

        mAuth = FirebaseAuth.getInstance()

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
                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("JoberError", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}