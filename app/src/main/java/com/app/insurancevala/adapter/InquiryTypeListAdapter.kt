package com.app.insurancevala.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.pojo.InquiryTypeModel
import com.app.insurancevala.model.pojo.ClosingAmountInfoModel
import com.app.insurancevala.model.pojo.ProposedAmountInfoModel
import kotlinx.android.synthetic.main.adapter_inquiry_type_list.view.*

class InquiryTypeListAdapter(
    val arrayListProposedAmount: ArrayList<ProposedAmountInfoModel>?,
    val arrayListClosingAmount: ArrayList<ClosingAmountInfoModel>?,
    val inquiryTypeModel: ArrayList<InquiryTypeModel>,
    val recyclerItemClickListener: RecyclerClickListener
) : RecyclerView.Adapter<InquiryTypeListAdapter.ViewHolder>() {
    var context: Context? = null
    var views: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_inquiry_type_list, parent, false)
        context = parent.context
        views = view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(
            context!!,
            position,
            inquiryTypeModel,
            arrayListProposedAmount,
            arrayListClosingAmount!!,
            recyclerItemClickListener
        )
    }

    override fun getItemCount(): Int {
        return arrayListClosingAmount!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            context: Context,
            position: Int,
            arrayListInquiryType: ArrayList<InquiryTypeModel>?,
            arrayListProposedAmount: ArrayList<ProposedAmountInfoModel>?,
            arrayListClosingAmount: ArrayList<ClosingAmountInfoModel>?,
            recyclerClickListener: RecyclerClickListener
        ) {

            itemView.txtInquiryType.text = arrayListInquiryType!![position].InquiryType

            if (!arrayListProposedAmount!![position].ProspectAmount.isNullOrEmpty()) {
                itemView.txtProposedAmount.text = arrayListProposedAmount[position].ProspectAmount
            }

            if (!arrayListClosingAmount!![position].ClosingAmount.isNullOrEmpty()) {
                itemView.txtClosingAmount.text = arrayListClosingAmount[position].ClosingAmount
            }

        }



    }

    fun getAdapterArrayList(): ArrayList<ClosingAmountInfoModel>? {
        return arrayListClosingAmount
    }

}
