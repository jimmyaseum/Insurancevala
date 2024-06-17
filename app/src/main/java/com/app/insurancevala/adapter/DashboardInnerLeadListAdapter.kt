package com.app.insurancevala.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.DashboardInnerLeadModel
import com.app.insurancevala.utils.gone
import kotlinx.android.synthetic.main.adapter_dashboard_inner_lead_item.view.*

class DashboardInnerLeadListAdapter(private val context: Context?, private val arrayList: ArrayList<DashboardInnerLeadModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<DashboardInnerLeadListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_dashboard_inner_lead_item, parent, false)
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
            arrayList : ArrayList<DashboardInnerLeadModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context

            if(!arrayList[position].FirstName.isNullOrEmpty()  && !arrayList[position].GroupCode.isNullOrEmpty()) {
                itemView.txtLeadName.text = arrayList[position].FirstName + " " + arrayList[position].LastName + " - " + arrayList[position].GroupCode
            } else if (!arrayList[position].FirstName.isNullOrEmpty()) {
                itemView.txtLeadName.text = arrayList[position].FirstName + " " + arrayList[position].LastName
            }

            if(!arrayList[position].EmailID.isNullOrEmpty()) {
                itemView.txtEmail.text = " :  " + arrayList[position].EmailID
            } else {
                itemView.LLEmailAddress.gone()
            }

            if(!arrayList[position].MobileNo.isNullOrEmpty()) {
                itemView.txtMobileNo.text = " :  " + arrayList[position].MobileNo
            } else {
                itemView.LLMobileNo.gone()
            }

            if(!arrayList[position].CreatedByName.isNullOrEmpty()) {
                itemView.txtCreatedByName.text = " :  " + arrayList[position].CreatedByName
            } else {
                itemView.LLEmployeeName.gone()
            }

            if (arrayList[position].CategoryID != null && arrayList[position].CategoryID != 0) {
                itemView.rating_bar.rating = arrayList[position].CategoryID!!.toFloat()
                if (arrayList[position].LeadStage != null && arrayList[position].LeadStage != 0){
                    if (arrayList[position].LeadStage == 1) {
                        itemView.rating_bar.progressTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.gold
                            )
                        )
                    } else if (arrayList[position].LeadStage == 2) {
                        itemView.rating_bar.progressTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.silver
                            )
                        )
                    }
                }
            } else {
                itemView.rating_bar.rating = 0.0f
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