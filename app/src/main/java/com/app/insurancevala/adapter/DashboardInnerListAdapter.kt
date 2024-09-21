package com.app.insurancevala.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.pojo.ClosingAmountInfoModel
import com.app.insurancevala.model.pojo.InquiryTypeModel
import com.app.insurancevala.model.pojo.ProposedAmountInfoModel
import com.app.insurancevala.model.response.DashboardInnerModel
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.adapter_dashboard_inner_item.view.*
import kotlinx.android.synthetic.main.adapter_dashboard_inner_item.view.rvInquiryList
import kotlinx.android.synthetic.main.adapter_dashboard_inner_item.view.txtFrequency
import kotlinx.android.synthetic.main.adapter_dashboard_inner_item.view.txtInquiryDate
import kotlinx.android.synthetic.main.adapter_dashboard_inner_item.view.txtInquiryNo
import kotlinx.android.synthetic.main.adapter_dashboard_inner_item.view.txtInquirySubType
import kotlinx.android.synthetic.main.adapter_dashboard_inner_item.view.txtInquiryType
import kotlinx.android.synthetic.main.adapter_dashboard_inner_item.view.txtLeadStatus
import kotlinx.android.synthetic.main.adapter_dashboard_inner_item.view.txtLeadType
import kotlinx.android.synthetic.main.adapter_dashboard_inner_item.view.txtProposedAmount
import kotlinx.android.synthetic.main.adapter_inquiry_item.view.*

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
            } else {
                itemView.txtInquiryDate.text = ""
            }

            if(arrayList[position].InquiryNo != null && arrayList[position].InquiryNo != 0) {
                itemView.txtInquiryNo.text = arrayList[position].InquiryNo.toString()
            } else {
                itemView.txtInquiryNo.text = ""
            }

            if(!arrayList[position].InquirySubType.isNullOrEmpty()) {
                itemView.txtInquirySubType.text = arrayList[position].InquirySubType
            } else {
                itemView.txtInquirySubType.text = ""
            }

            if(!arrayList[position].LeadStatus.isNullOrEmpty()) {
                itemView.txtLeadStatus.text = arrayList[position].LeadStatus
            } else {
                itemView.txtLeadStatus.text = ""
            }

            if(!arrayList[position].LeadType.isNullOrEmpty()) {
                itemView.txtLeadType.text = arrayList[position].LeadType
            } else {
                itemView.txtLeadType.text = ""
            }

            if(!arrayList[position].Frequency.isNullOrEmpty()) {
                itemView.txtFrequency.text = arrayList[position].Frequency
            } else {
                itemView.txtFrequency.text = ""
            }

            if(!arrayList[position].CoPersonAllotmentName.isNullOrEmpty()) {
                itemView.txtCoAllottedToName.text = arrayList[position].CoPersonAllotmentName
                itemView.llCo_Allotted_Person.visible()
            } else {
                itemView.llCo_Allotted_Person.gone()
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

            val arrayListProposedAmount = ArrayList<ProposedAmountInfoModel>()
            val arrayListClosingAmount = ArrayList<ClosingAmountInfoModel>()
            val arrayListInquiryType = ArrayList<InquiryTypeModel>()

            val InquiryType = arrayList[position].InquiryType!!.split(", ")
            val ProposedAmount = if(!arrayList[position].ProposedAmount.isNullOrEmpty())
                arrayList[position].ProposedAmount!!.split(", ")
            else
                listOf<String>()
            val ClosingAmount = if(!arrayList[position].ClosingAmount.isNullOrEmpty())
                arrayList[position].ClosingAmount?.trim()!!.split(", ")
            else
                listOf<String>()

            for (i in InquiryType.indices) {
                if (!ProposedAmount.isEmpty() && arrayList[position].ProposedAmount!!.trim().isNotEmpty()) {
                    arrayListProposedAmount.add(ProposedAmountInfoModel(ProspectAmount = ProposedAmount[i]))
                } else {
                    arrayListProposedAmount.add(ProposedAmountInfoModel(ProspectAmount = ""))
                }
                if (!ClosingAmount.isEmpty() && arrayList[position].ClosingAmount!!.trim().isNotEmpty()) {
                    arrayListClosingAmount.add(ClosingAmountInfoModel(ClosingAmount = ClosingAmount[i]))
                } else {
                    arrayListClosingAmount.add(ClosingAmountInfoModel(ClosingAmount = ""))
                }
                arrayListInquiryType.add(InquiryTypeModel(InquiryType = InquiryType[i]))
            }

            val adapterInquiryTypeList = InquiryTypeListAdapter(
                arrayListProposedAmount,
                arrayListClosingAmount,
                arrayListInquiryType,
                recyclerItemClickListener
            )
            itemView.rvInquiryList.adapter = adapterInquiryTypeList

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