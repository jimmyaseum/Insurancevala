package com.app.insurancevala.adapter.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.LeadStatusWiseCountModel
import kotlinx.android.synthetic.main.adapter_dashboard_lead_status_item.view.*
import java.util.*

class LeadStatusWiseCountsAdapter(val mContext: Context,
                                  val arrayList: ArrayList<LeadStatusWiseCountModel>?,
                                  private val recyclerItemClickListener: RecyclerClickListener
) : RecyclerView.Adapter<LeadStatusWiseCountsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_dashboard_lead_status_item, parent, false)
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
            model: LeadStatusWiseCountModel,
            recyclerItemClickListener: RecyclerClickListener
        ) {

            var colors = arrayOf(R.drawable.bg_shadow_orange, R.drawable.bg_shadow_purple, R.drawable.bg_shadow_blue)
            val reminder = position % 3
            itemView.ll.setBackgroundDrawable(ContextCompat.getDrawable(mContext, colors[reminder]))

            if (!model.LeadStatus.isNullOrEmpty()) {
                itemView.txtLeadStatusName.text = model.LeadStatus
            }
            if (model.LeadStatusCount != null) {
                itemView.txtLeadStatusCount.text = model.LeadStatusCount.toString()
            }

            itemView.ll.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }
        }
    }
}