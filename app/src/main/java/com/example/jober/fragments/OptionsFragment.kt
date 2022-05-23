package com.example.tablayout.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.jober.Login
import com.example.jober.MainActivity
import com.example.jober.R

class OptionsFragment : Fragment() {

    lateinit var btn_logout : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_logout = view?.findViewById<Button>(R.id.btn_logout)!!

        btn_logout.setOnClickListener {
            (activity as MainActivity).logout()
        }
    }
}
