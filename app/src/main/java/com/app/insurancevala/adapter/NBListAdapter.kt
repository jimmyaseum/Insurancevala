package com.app.insurancevala.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.NBModel
import com.app.insurancevala.model.response.NewNBModel
import kotlinx.android.synthetic.main.adapter_nb_item.view.*

class NBListAdapter(private val context: Context?, private val arrayList: ArrayList<NewNBModel>?, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<NBListAdapter.ViewHolder>() {

    var adapterPositionGet: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_nb_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        adapterPositionGet = position
        holder.bindItems(context!!, position, arrayList!![position], recyclerItemClickListener)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    fun getAdapterPosition(): Int {
        return adapterPositionGet
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            context: Context,
            position: Int,
            arrayList : NewNBModel,
            recyclerItemClickListener: RecyclerClickListener
        ) {

            if(arrayList.InquiryNo != 0 && arrayList.InquiryNo != null) {
                itemView.txtInquiryNo.text = "Inquiry No : "+ arrayList.InquiryNo
            }

            if(!arrayList.LeadName.isNullOrEmpty()) {
                itemView.txtClientName.text = arrayList.LeadName
            }

            if(!arrayList.MobileNo.isNullOrEmpty()) {
                itemView.txtMobileNo.text = arrayList.MobileNo
            }

            if(arrayList.LeadStageID != null && arrayList.LeadStageID != 0) {
                val stageid =  arrayList.LeadStageID
                if(stageid == 1) {
                    itemView.txtClientType.text = "Existing Client"
                } else if (stageid == 2) {
                    itemView.txtClientType.text = "Prospect"
                }
            }

            if(!arrayList.AllotmentName.isNullOrEmpty()) {
                itemView.txtAllotmentTo.text = arrayList.AllotmentName
            }

            var colors = arrayOf(R.drawable.bg_shadow_orange, R.drawable.bg_shadow_purple, R.drawable.bg_shadow_blue)
            var colors1 = arrayOf(R.color.color5, R.color.color11, R.color.color8)

            val reminder = position % 3
//            itemView.LLStart.setBackgroundDrawable(ContextCompat.getDrawable(context, colors[reminder]))

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }

            itemView.imgEdit.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }

            itemView.imgList.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 103)
            }

            itemView.txtOpen.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 104)
            }

            itemView.txtClose.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 105)
            }
        }
    }
}