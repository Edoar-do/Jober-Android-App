package com.example.tablayout.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.example.jober.Login
import com.example.jober.MainActivity
import com.example.jober.R
import com.example.jober.WorkerProfileEdit

class WorkerProfileFragment : Fragment() {

    lateinit var btn_edit : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_worker_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_edit = view?.findViewById(R.id.btn_edit)

        btn_edit.setOnClickListener {
            var intent = Intent(activity, WorkerProfileEdit::class.java)
            startActivity(intent)
        }
    }

}
