package com.app.insurancevala.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.LeadModel
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.loadURI
import com.app.insurancevala.utils.loadUrl
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.activity_add_lead.rating_bar
import kotlinx.android.synthetic.main.adapter_lead_item.view.*

class LeadListAdapter(private val context: Context?, private val arrayList: ArrayList<LeadModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<LeadListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_lead_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context!!, position, arrayList, recyclerItemClickListener)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(
            context: Context,
            position: Int,
            arrayList : ArrayList<LeadModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {

            if(!arrayList[position].FirstName.isNullOrEmpty()) {
                itemView.txtName.text = arrayList[position].FirstName +" "+ arrayList[position].LastName
            }

            if(!arrayList[position].MobileNo.isNullOrEmpty()) {
                itemView.txtMobileNumber.text = arrayList[position].MobileNo
            }

            if(!arrayList[position].EmailID.isNullOrEmpty()) {
                itemView.txtEmail.text = arrayList[position].EmailID
                itemView.txtEmail.visible()
            } else {
                itemView.txtEmail.gone()
            }

            if (arrayList[position].CategoryID != null && arrayList[position].CategoryID != 0) {
                itemView.rating_bar.rating = arrayList[position].CategoryID!!.toFloat()
                if (arrayList[position].LeadStage != null && arrayList[position].LeadStage != 0){
                    if (arrayList[position].LeadStage == 1) {
                        itemView.rating_bar.progressTintList = ColorStateList.valueOf(getColor(context, R.color.gold))
                    } else if (arrayList[position].LeadStage == 2) {
                        itemView.rating_bar.progressTintList = ColorStateList.valueOf(getColor(context, R.color.silver))
                    }
                }
            } else {
                itemView.rating_bar.rating = 0.0f
            }

            if(!arrayList[position].LeadImage.isNullOrEmpty()) {
                itemView.imgprofile.loadUrl(arrayList[position].LeadImage, R.drawable.ic_profile)
            } else {
                 itemView.imgprofile.setImageResource(R.drawable.ic_profile)
            }

            if(arrayList[position].IsFamilyDetails == true) {
                itemView.imgFamilyMember.gone()
            } else {
                 itemView.imgFamilyMember.visible()
            }

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }

            itemView.imgEdit.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }

            itemView.imgprofile.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 103)
            }
        }
    }
}