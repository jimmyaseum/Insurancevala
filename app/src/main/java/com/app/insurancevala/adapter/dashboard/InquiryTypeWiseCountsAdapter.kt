package com.app.insurancevala.adapter.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.InquiryTypeWiseCountsModel
import kotlinx.android.synthetic.main.adapter_dashboard_inquiry_type_item.view.*
import kotlinx.android.synthetic.main.adapter_dashboard_lead_status_item.view.ll
import java.util.*

class InquiryTypeWiseCountsAdapter(val mContext: Context,
                                   val arrayList: ArrayList<InquiryTypeWiseCountsModel>?,
                                   private val recyclerItemClickListener: RecyclerClickListener
) : RecyclerView.Adapter<InquiryTypeWiseCountsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_dashboard_inquiry_type_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(mContext, arrayList!![position]!!, recyclerItemClickListener)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            mContext: Context,
            model: InquiryTypeWiseCountsModel,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            if (!model.InquiryType.isNullOrEmpty()) {
                itemView.txtInquiryType.text = model.InquiryType
            }
            if (model.InquiryTypeCount != null) {
                itemView.txtInquiryTypeCount.text = model.InquiryTypeCount.toString()
            }

            itemView.ll2.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }
        }
    }
}