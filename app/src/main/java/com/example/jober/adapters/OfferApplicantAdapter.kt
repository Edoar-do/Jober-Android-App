package com.example.jober.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.jober.*
import com.example.jober.model.Application
import com.example.jober.model.Offer
import com.example.jober.model.Worker

class OfferApplicantAdapter(val context: Context, var workerList: ArrayList<Worker>, var workerPics: ArrayList<Bitmap>,
    var application_list : ArrayList<Application>): RecyclerView.Adapter<OfferApplicantAdapter.OfferViewHolder>() {

    class OfferViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_applicant_name = itemView.findViewById<TextView>(R.id.tv_applicant_name)
        val tv_applicant_profession = itemView.findViewById<TextView>(R.id.tv_applicant_profession)
        val iv_applicant_profile = itemView.findViewById<ImageView>(R.id.iv_applicant_profile)
    }

    fun setFilteredLists(workers_filtered_list : ArrayList<Worker>, worker_pics_filtered_list : ArrayList<Bitmap>,
        application_filtered_list : ArrayList<Application>) {
        this.workerList = workers_filtered_list
        this.workerPics = worker_pics_filtered_list
        this.application_list = application_filtered_list
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OfferViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.applicant_row, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val current_applicant = workerList[position]
        val current_applicant_pic = workerPics[position]
        val current_application = application_list[position]

        holder.tv_applicant_name.text = current_applicant.name
        holder.tv_applicant_profession.text = current_applicant.main_profession
        holder.iv_applicant_profile.setImageBitmap(current_applicant_pic)

        holder.itemView.setOnClickListener {
            val intent : Intent = Intent(context, ApplicantProfile::class.java)
            intent.putExtra("application_id", current_application.application_id)
            intent.putExtra("worker", current_applicant)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return workerList.size
    }

}