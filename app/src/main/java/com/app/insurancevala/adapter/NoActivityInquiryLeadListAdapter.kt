package com.app.insurancevala.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.NBNoLeadInquiryModel
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.preventTwoClick
import kotlinx.android.synthetic.main.adapter_no_inquiry_lead_item.view.*

class NoActivityInquiryLeadListAdapter(private val context: Context?, private val arrayList: ArrayList<NBNoLeadInquiryModel>, private val sharedPreference: SharedPreference, private val int: Int, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<NoActivityInquiryLeadListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_no_inquiry_lead_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context!!, position, arrayList, recyclerItemClickListener)
        if (int == 1) {
            holder.itemView.txtDate.text = "Inquiry Date : "
            holder.itemView.txtNumber.text = "Inquiry No : "
            holder.itemView.txtPerson.text = "Inquiry Person : "
        } else if (int == 2) {
            holder.itemView.txtDate.text = "Lead Date : "
            holder.itemView.txtNumber.text = "Lead No : "
            holder.itemView.txtPerson.text = "Lead Person : "
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            // Set click listener for imgMore
            itemView.imgMore.setOnClickListener(this)
        }

        var context: Context? = null

        fun bindItems(
            context: Context,
            position: Int,
            arrayList : ArrayList<NBNoLeadInquiryModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {

            this.context = context

            var colors = arrayOf(R.drawable.bg_shadow_orange, R.drawable.bg_shadow_purple, R.drawable.bg_shadow_blue)
            val reminder = position % 3
            itemView.v1.setBackgroundResource(colors[reminder])

            if(!arrayList[position].InquiryType.isNullOrEmpty()) {
                itemView.txtInquiryType.text = arrayList[position].InquiryType
            }

            if(!arrayList[position].InquirySubType.isNullOrEmpty()) {
                itemView.txtInquirySubType.text = arrayList[position].InquirySubType
            }

            if(!arrayList[position].InquiryPerson.isNullOrEmpty()) {
                itemView.txtInquiryPerson.text = arrayList[position].InquiryPerson
            }

            if(arrayList[position].InquiryNo != null && arrayList[position].InquiryNo != 0) {
                itemView.txtInquiryNo.text = arrayList[position].InquiryNo.toString()
            }

            if(!arrayList[position].CoPersonAllotmentName.isNullOrEmpty()) {
                itemView.txtCoAllottedPerson.text = arrayList[position].CoPersonAllotmentName
            }

            if(!arrayList[position].AllotmentName.isNullOrEmpty()) {
                itemView.txtAllottedToPerson.text = arrayList[position].AllotmentName
            }

            if(!arrayList[position].CreatedByName.isNullOrEmpty()) {
                itemView.txtAllottedByPerson.text = arrayList[position].CreatedByName
            }

            if(!arrayList[position].InquiryDate.isNullOrEmpty()) {
                itemView.txtInquiryDate.text = arrayList[position].InquiryDate
            }

            if(!arrayList[position].LeadStatus.isNullOrEmpty()) {
                itemView.txtLeadStatus.text = arrayList[position].LeadStatus
            }

            if(!arrayList[position].LeadType.isNullOrEmpty()) {
                itemView.txtLeadType.text = arrayList[position].LeadType
            }

            if(!arrayList[position].Frequency.isNullOrEmpty()) {
                itemView.txtFrequency.text = arrayList[position].Frequency
            }

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }

            itemView.txtEdit.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }

            itemView.imgMore.tag = position
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.imgMore -> {
                    preventTwoClick(v)
                    val position = v.tag as Int
                    showPopupMenu(v, position)
                }
            }
        }

        private fun showPopupMenu(view: View, position: Int) {
            val popupMenu = PopupMenu(context, view)
            popupMenu.inflate(R.menu.popup_menu_items)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_notes -> {
                        recyclerItemClickListener.onItemClickEvent(view, position, 104)
                        true
                    }
                    R.id.menu_tasks -> {
                        recyclerItemClickListener.onItemClickEvent(view, position, 105)
                        true
                    }
                    R.id.menu_calls -> {
                        recyclerItemClickListener.onItemClickEvent(view, position, 106)
                        true
                    }
                    R.id.menu_meetings -> {
                        recyclerItemClickListener.onItemClickEvent(view, position, 107)
                        true
                    }
                    R.id.menu_attachments -> {
                        recyclerItemClickListener.onItemClickEvent(view, position, 108)
                        true
                    }
                    R.id.menu_closed_tasks -> {
                        recyclerItemClickListener.onItemClickEvent(view, position, 109)
                        true
                    }
                    R.id.menu_closed_calls -> {
                        recyclerItemClickListener.onItemClickEvent(view, position, 110)
                        true
                    }
                    R.id.menu_closed_meetings -> {
                        recyclerItemClickListener.onItemClickEvent(view, position, 111)
                        true
                    }
                    R.id.menu_recordings -> {
                        recyclerItemClickListener.onItemClickEvent(view, position, 112)
                        true
                    }
                    R.id.menu_activity_log -> {
                        recyclerItemClickListener.onItemClickEvent(view, position, 113)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }
}
