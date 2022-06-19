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
import com.example.jober.OfferDescription
import com.example.jober.R
import com.example.jober.model.Offer

class OfferAdapter(val context: Context, var offerList: ArrayList<Offer>, var company_logos: ArrayList<Bitmap>, var company_names: ArrayList<String>): RecyclerView.Adapter<OfferAdapter.OfferViewHolder>() {

    class OfferViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_company_name = itemView.findViewById<TextView>(R.id.tv_company_name)
        val tv_position = itemView.findViewById<TextView>(R.id.tv_position)
        val tv_location = itemView.findViewById<TextView>(R.id.tv_location)
        val iv_company_logo = itemView.findViewById<ImageView>(R.id.iv_company_logo)
    }

    fun setFilteredLists(offers_filtered_list : ArrayList<Offer>, company_names_filtered_list : ArrayList<String>, company_logos_filtered_list : ArrayList<Bitmap>) {
        this.offerList = offers_filtered_list
        this.company_names = company_names_filtered_list
        this.company_logos = company_logos_filtered_list

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OfferViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.offer_row, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val current_offer = offerList[position]
        val current_company_logo = company_logos[position]
        val current_company_name = company_names[position]

//        println("#################################### sono all'interno dell'onbindViewHolder")
//        println("#################################### current company name: " + current_company_name)
//        println("#################################### current position: " + current_offer.position)
//        println("#################################### current location: " + current_offer.location)

        holder.tv_company_name.text = current_company_name
        holder.tv_position.text = current_offer.position
        holder.tv_location.text = current_offer.location
        holder.iv_company_logo.setImageBitmap(current_company_logo)
//        println("#################################### location on the field: " + holder.tv_location.text)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, OfferDescription()::class.java)
            intent.putExtra("offer_id", current_offer.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
//        println("################################ questa e' la lunghezza delle liste: " + offerList.size)
        return offerList.size
    }

}