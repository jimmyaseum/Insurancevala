package com.app.insurancevala.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.pojo.ClosingAmountInfoModel
import com.app.insurancevala.model.pojo.InquiryTypeModel
import com.app.insurancevala.model.pojo.ProposedAmountInfoModel
import com.app.insurancevala.model.response.NBInquiryModel
import com.app.insurancevala.utils.PrefConstants
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.preventTwoClick
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.adapter_dashboard_inner_item.view.txtFrequency
import kotlinx.android.synthetic.main.adapter_inquiry_item.view.*

class InquiryListAdapter(private val context: Context?, private val arrayList: ArrayList<NBInquiryModel>, private val sharedPreference: SharedPreference, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<InquiryListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_inquiry_item, parent, false)
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
            arrayList : ArrayList<NBInquiryModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {

            this.context = context

            /*if (!arrayList[position].InquiryType.isNullOrEmpty()) {
                itemView.txtInquiryType.text = arrayList[position].InquiryType
            }*/

            var colors = arrayOf(R.drawable.bg_shadow_orange, R.drawable.bg_shadow_purple, R.drawable.bg_shadow_blue)
            val reminder = position % 3
            itemView.v1.setBackgroundResource(colors[reminder])

            if(!arrayList[position].InquirySubType.isNullOrEmpty()) {
                itemView.txtInquirySubType.text = arrayList[position].InquirySubType
            } else {
                itemView.txtInquirySubType.text = ""
            }

            if(!arrayList[position].NBInquiryByName.isNullOrEmpty()) {
                itemView.txtInquiryPerson.text = arrayList[position].NBInquiryByName
            }  else {
                itemView.txtInquiryPerson.text = ""
            }

            if(arrayList[position].InquiryNo != null && arrayList[position].InquiryNo != 0) {
                itemView.txtInquiryNo.text = arrayList[position].InquiryNo.toString()
            }  else {
                itemView.txtInquiryNo.text = ""
            }

            if(!arrayList[position].CoPersonAllotmentName.isNullOrEmpty()) {
                itemView.txtCoAllottedPerson.text = arrayList[position].CoPersonAllotmentName
            } else {
                itemView.txtCoAllottedPerson.text = "NA"
            }

            if(!arrayList[position].InquiryAllotmentName.isNullOrEmpty()) {
                itemView.txtAllottedToPerson.text = arrayList[position].InquiryAllotmentName
            } else {
                itemView.txtAllottedToPerson.text = ""
            }

            if(!arrayList[position].CreatedByName.isNullOrEmpty()) {
                itemView.txtAllottedByPerson.text = arrayList[position].CreatedByName
            } else {
                itemView.txtAllottedByPerson.text = ""
            }

            if(!arrayList[position].InquiryDate.isNullOrEmpty()) {
                itemView.txtInquiryDate.text = arrayList[position].InquiryDate
            } else {
                itemView.txtInquiryDate.text = ""
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

            val arrayListProposedAmount = ArrayList<ProposedAmountInfoModel>()
            val arrayListClosingAmount = ArrayList<ClosingAmountInfoModel>()
            val arrayListInquiryType = ArrayList<InquiryTypeModel>()

            val InquiryType = arrayList[position].InquiryType!!.split(", ")
            val ProposedAmount = arrayList[position].ProposedAmount!!.split(", ")
            val ClosingAmount = arrayList[position].ClosingAmount!!.trim().split(", ")
            for (i in InquiryType.indices) {
                arrayListProposedAmount.add(ProposedAmountInfoModel(ProspectAmount = ProposedAmount[i]))
                if (arrayList[position].ClosingAmount!!.trim().isNotEmpty()) {
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

            /*if(!arrayList[position].ProposedAmount.isNullOrEmpty()) {
                itemView.txtProposedAmount.text = arrayList[position].ProposedAmount.toString()
            }*/

            if(!arrayList[position].Frequency.isNullOrEmpty()) {
                itemView.txtFrequency.text = arrayList[position].Frequency
            } else {
                itemView.txtFrequency.text = ""
            }

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }

            if (arrayList[position].InquiryAllotmentID == sharedPreference.getPreferenceString(PrefConstants.PREF_USER_ID)!!.toInt() ||
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
