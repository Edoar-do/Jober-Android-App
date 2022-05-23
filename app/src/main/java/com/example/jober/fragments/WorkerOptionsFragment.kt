package com.example.tablayout.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.jober.MainActivity
import com.example.jober.R

class WorkerOptionsFragment : Fragment() {

    lateinit var btn_logout : Button
    lateinit var btn_profile : Button
    lateinit var btn_applications : Button

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

        btn_logout.setOnClickListener {
            (activity as MainActivity).logout()
        }

        btn_profile.setOnClickListener {
            (activity as MainActivity).setFragmentByTitle("WorkerProfile")
        }
    }
}
