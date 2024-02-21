package com.app.insurancevala.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.NBInquiryModel
import kotlinx.android.synthetic.main.adapter_inquiry_item.view.*

class InquiryListAdapter(private val context: Context?, private val arrayList: ArrayList<NBInquiryModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<InquiryListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_inquiry_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context!!, position, arrayList, recyclerItemClickListener)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var context: Context? = null

        fun bindItems(
            context: Context,
            position: Int,
            arrayList : ArrayList<NBInquiryModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context

            if(!arrayList[position].InquiryType.isNullOrEmpty()) {
                itemView.txtInquiryType.text = arrayList[position].InquiryType
            }

            if(!arrayList[position].InquirySubType.isNullOrEmpty()) {
                itemView.txtInquirySubType.text = arrayList[position].InquirySubType
            }

            if(!arrayList[position].LeadStatus.isNullOrEmpty()) {
                itemView.txtLeadStatus.text = arrayList[position].LeadStatus
            }

            if(!arrayList[position].LeadType.isNullOrEmpty()) {
                itemView.txtLeadType.text = arrayList[position].LeadType
            }

            if(arrayList[position].ProposedAmount != null && arrayList[position].ProposedAmount != 0.0) {
                itemView.txtProposedAmount.text = arrayList[position].ProposedAmount.toString()
            }

            if(!arrayList[position].Frequency.isNullOrEmpty()) {
                itemView.txtFrequency.text = arrayList[position].Frequency
            }

            var colors = arrayOf(R.drawable.bg_shadow_orange, R.drawable.bg_shadow_purple, R.drawable.bg_shadow_blue)
            var colors1 = arrayOf(R.color.color5, R.color.color11, R.color.color8)

            val reminder = position % 3
//            itemView.v1.setBackgroundDrawable(ContextCompat.getDrawable(context, colors[reminder]))

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }

            itemView.txtEdit.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }
            itemView.txtDelete.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 103)
            }
        }
    }
}