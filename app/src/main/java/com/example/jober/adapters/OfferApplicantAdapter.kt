package com.example.jober.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jober.OfferDescription
import com.example.jober.R
import com.example.jober.model.Offer
import com.example.jober.model.Worker

class OfferApplicantAdapter(val context: Context, val workerList: ArrayList<Worker>, val workerPics: ArrayList<Bitmap>): RecyclerView.Adapter<OfferApplicantAdapter.OfferViewHolder>() {

    class OfferViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_applicant_name = itemView.findViewById<TextView>(R.id.tv_applicant_name)
        val tv_applicant_profession = itemView.findViewById<TextView>(R.id.tv_applicant_profession)
        val iv_applicant_profile = itemView.findViewById<ImageView>(R.id.iv_applicant_profile)
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

        holder.tv_applicant_name.text = current_applicant.name
        holder.tv_applicant_profession.text = current_applicant.main_profession
        holder.iv_applicant_profile.setImageBitmap(current_applicant_pic)

        holder.itemView.setOnClickListener {
            // TODO open worker profile, remember to pass application id
//            val intent = Intent(context, OfferDescription()::class.java)
//            intent.putExtra("offer_id", current_applicant.id)
//            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
//        println("################################ questa e' la lunghezza delle liste: " + offerList.size)
        return workerList.size
    }

}