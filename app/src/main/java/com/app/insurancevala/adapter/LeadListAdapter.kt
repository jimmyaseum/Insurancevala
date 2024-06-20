package com.app.insurancevala.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.NBLeadListModel
import com.app.insurancevala.utils.PrefConstants
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.preventTwoClick
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.adapter_lead_item.view.*

class LeadListAdapter(private val context: Context?, private val arrayList: ArrayList<NBLeadListModel>, private val sharedPreference: SharedPreference, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<LeadListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_lead_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context!!, position, arrayList, recyclerItemClickListener)
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
            arrayList : ArrayList<NBLeadListModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {

            this.context = context

            if (!arrayList[position].InquiryType.isNullOrEmpty()) {
                itemView.txtInquiryType.text = arrayList[position].InquiryType
            }

            var colors = arrayOf(R.drawable.bg_shadow_orange, R.drawable.bg_shadow_purple, R.drawable.bg_shadow_blue)
            val reminder = position % 3
            itemView.v1.setBackgroundResource(colors[reminder])

            if(!arrayList[position].InquirySubType.isNullOrEmpty()) {
                itemView.txtInquirySubType.text = arrayList[position].InquirySubType
            }

            if(!arrayList[position].NBLeadByName.isNullOrEmpty()) {
                itemView.txtInquiryPerson.text = arrayList[position].NBLeadByName
            }

            if(arrayList[position].LeadNo != null && arrayList[position].LeadNo != 0) {
                itemView.txtLeadNo.text = arrayList[position].LeadNo.toString()
            }

            if(!arrayList[position].LeadAllotmentName.isNullOrEmpty()) {
                itemView.txtAllottedPerson.text = arrayList[position].LeadAllotmentName
            }

            if(!arrayList[position].CreatedByName.isNullOrEmpty()) {
                itemView.txtAllottedToPerson.text = arrayList[position].CreatedByName
            }

            if(!arrayList[position].LeadDate.isNullOrEmpty()) {
                itemView.txtLeadDate.text = arrayList[position].LeadDate
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

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }

            if (arrayList[position].LeadAllotmentID == sharedPreference.getPreferenceString(PrefConstants.PREF_USER_ID)!!.toInt() ||
                sharedPreference.getPreferenceString(PrefConstants.PREF_USER_TYPE_ID)!!.toInt() == 1) {
                itemView.txtEdit.visible()
            } else {
                itemView.txtEdit.gone()
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
