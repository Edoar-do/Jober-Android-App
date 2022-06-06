package com.example.jober.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jober.R
import com.example.jober.model.Offer

class OfferAdapter(val context: Context, val offerList: ArrayList<Offer>, val company_logos: ArrayList<Bitmap>, val company_names: ArrayList<String>): RecyclerView.Adapter<OfferAdapter.OfferViewHolder>() {

    class OfferViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_company_name = itemView.findViewById<TextView>(R.id.tv_company_name)
        val tv_position = itemView.findViewById<TextView>(R.id.tv_position)
        val tv_location = itemView.findViewById<TextView>(R.id.tv_location)
        val iv_company_logo = itemView.findViewById<ImageView>(R.id.iv_company_logo)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OfferAdapter.OfferViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.offer_row, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferAdapter.OfferViewHolder, position: Int) {
        val current_offer = offerList[position]
        val current_company_logo = company_logos[position]
        val current_company_name = company_names[position]

        holder.tv_company_name.text = current_company_name
        holder.tv_position.text = current_offer.position
        holder.tv_location.text = current_offer.location
        holder.iv_company_logo.setImageBitmap(current_company_logo)

        holder.itemView.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return offerList.size
    }

}