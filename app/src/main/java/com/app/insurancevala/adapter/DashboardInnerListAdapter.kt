package com.app.insurancevala.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.DashboardInnerModel
import kotlinx.android.synthetic.main.adapter_dashboard_inner_item.view.*

class DashboardInnerListAdapter(private val context: Context?, private val arrayList: ArrayList<DashboardInnerModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<DashboardInnerListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_dashboard_inner_item, parent, false)
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
            arrayList : ArrayList<DashboardInnerModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context

            if(!arrayList[position].LeadName.isNullOrEmpty() && !arrayList[position].GroupCode.isNullOrEmpty()) {
                itemView.txtLeadName.text = arrayList[position].LeadName + " - " + arrayList[position].GroupCode
            } else if (!arrayList[position].LeadName.isNullOrEmpty()) {
                itemView.txtLeadName.text = arrayList[position].LeadName
            }

            if(!arrayList[position].InquiryDate.isNullOrEmpty()) {
                itemView.txtInquiryDate.text = arrayList[position].InquiryDate
            }

            if(arrayList[position].InquiryNo != null && arrayList[position].InquiryNo != 0) {
                itemView.txtInquiryNo.text = arrayList[position].InquiryNo.toString()
            }

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

            if(!arrayList[position].CoPersonAllotmentName.isNullOrEmpty()) {
                itemView.txtCoAllottedToName.text = arrayList[position].CoPersonAllotmentName
            } else {
                itemView.txtCoAllottedToName.text = ""
            }

            if(!arrayList[position].InquiryAllotmentName.isNullOrEmpty()) {
                itemView.txtAllottedToName.text = arrayList[position].InquiryAllotmentName
            } else {
                itemView.txtAllottedToName.text = ""
            }

            if(!arrayList[position].CreatedByName.isNullOrEmpty()) {
                itemView.txtAllottedByName.text = arrayList[position].CreatedByName
            } else {
                itemView.txtAllottedByName.text = ""
            }

            var colors = arrayOf(R.drawable.bg_shadow_orange, R.drawable.bg_shadow_purple, R.drawable.bg_shadow_blue)
            var colors1 = arrayOf(R.color.button_blue, R.color.button_purple, R.color.button_orange)

            val reminder = position % 3
            itemView.LLHeader.setBackgroundColor(ContextCompat.getColor(context, colors1[reminder]))

            itemView.imgEdit.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }
        }
    }
}