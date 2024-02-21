package com.app.insurancevala.activity.NBInquiry

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerItemClickListener
import com.app.insurancevala.model.pojo.InquiryInformationModel
import kotlinx.android.synthetic.main.adapter_add_more_inquiry.view.*

class AddMoreInquiryAdapter (val arrayList: ArrayList<InquiryInformationModel>?, val recyclerItemClickListener: RecyclerItemClickListener) : RecyclerView.Adapter<AddMoreInquiryAdapter .ViewHolder>()
{
    var context: Context? = null
    var views: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_add_more_inquiry, parent, false)
        context = parent.context
        views = view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context!!, position, arrayList!!, recyclerItemClickListener)

        arrayList[position].tilInquiryType = holder.itemView.edtInquiryType
        arrayList[position].tilInquirysubtype = holder.itemView.edtInquirySub
        arrayList[position].tilProposed = holder.itemView.edtProposedAmount
        arrayList[position].tilFrequency = holder.itemView.edtFrequency
        arrayList[position].tilLeadtype = holder.itemView.edtLeadType
        arrayList[position].tilLeadstatus = holder.itemView.edtLeadStatus
        arrayList[position].tilAllotmentTo = holder.itemView.edtAllotmentTo

        if (position == 0 && position == arrayList.size - 1) {
            holder.itemView.imgDelete.visibility = View.GONE
            holder.itemView.tvAddMore.visibility = View.VISIBLE
        }
        else if (position == arrayList.size - 1) {
            holder.itemView.imgDelete.visibility = View.VISIBLE
            holder.itemView.tvAddMore.visibility = View.VISIBLE
        }
        else {
            holder.itemView.imgDelete.visibility = View.VISIBLE
            holder.itemView.tvAddMore.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            context: Context,
            position: Int,
            arrayList: ArrayList<InquiryInformationModel>?,
            recyclerItemClickListener: RecyclerItemClickListener
        ) {

            itemView.edtAllotmentTo.setText(arrayList!![position].AllotmentTo)

            itemView.edtInquiryType.setText(arrayList!![position].Inquirytype)
            itemView.edtInquirySub.setText(arrayList!![position].Inquirysubtype)
            itemView.edtProposedAmount.setText(arrayList!![position].Proposed.toString())
            itemView.edtFrequency.setText(arrayList!![position].Frequency)
            itemView.edtLeadType.setText(arrayList!![position].Leadtype)
            itemView.edtLeadStatus.setText(arrayList!![position].Leadstatus)
            itemView.edtAllotmentTo.setText(arrayList!![position].AllotmentTo)

            itemView.edtInquiryType.setOnClickListener {
                if(!arrayList[adapterPosition].Inquirytype!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, arrayList[adapterPosition].Inquirytype)
                } else{
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, arrayList[adapterPosition].Inquirytype)
                }
            }

            itemView.edtInquirySub.setOnClickListener {
                if(!arrayList[adapterPosition].Inquirysubtype!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 2, arrayList[adapterPosition].Inquirysubtype)
                } else{
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 2, arrayList[adapterPosition].Inquirysubtype)
                }
            }

            itemView.edtFrequency.setOnClickListener {
                if(!arrayList[adapterPosition].Frequency!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 3, arrayList[adapterPosition].Frequency)
                } else{
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 3, arrayList[adapterPosition].Frequency)
                }
            }

            itemView.edtLeadType.setOnClickListener {
                if(!arrayList[adapterPosition].Leadtype!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 4, arrayList[adapterPosition].Leadtype)
                } else{
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 4, arrayList[adapterPosition].Leadtype)
                }
            }

            itemView.edtLeadStatus.setOnClickListener {
                if(!arrayList[adapterPosition].Leadstatus!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 5, arrayList[adapterPosition].Leadstatus)
                } else{
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 5, arrayList[adapterPosition].Leadstatus)
                }
            }

            itemView.edtAllotmentTo.setOnClickListener {
                if(!arrayList[adapterPosition].AllotmentTo!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, arrayList[adapterPosition].AllotmentTo)
                } else{
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, arrayList[adapterPosition].AllotmentTo)
                }
            }

            itemView.tvAddMore.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, "")
            }

            itemView.imgDelete.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, "")
            }

            itemView.edtInquiryType.setSimpleListener {
                arrayList[adapterPosition].Inquirytype = it.toString()
            }
            itemView.edtInquirySub.setSimpleListener {
                arrayList[adapterPosition].Inquirysubtype = it.toString()
            }
            itemView.edtFrequency.setSimpleListener {
                arrayList[adapterPosition].Frequency = it.toString()
            }
            itemView.edtLeadType.setSimpleListener {
                arrayList[adapterPosition].Leadtype = it.toString()
            }
            itemView.edtLeadStatus.setSimpleListener {
                arrayList[adapterPosition].Leadstatus = it.toString()
            }

            itemView.edtAllotmentTo.setSimpleListener {
                arrayList[adapterPosition].AllotmentTo = it.toString()
            }
            itemView.edtProposedAmount.setSimpleListener {
                arrayList[adapterPosition].Proposed = it.toString()
                itemView.edtProposedAmount.setError(null)
            }
        }

        private fun EditText.setSimpleListener(listener: (p0: CharSequence?) -> Unit) {
            this.addTextChangedListener(TextWatcherFactory().create(listener))
        }
        class TextWatcherFactory {
            fun create(onTextChanged: (p0: CharSequence?) -> Unit): android.text.TextWatcher {
                return object : android.text.TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = onTextChanged(p0)
                }
            }
        }

    }

    fun getAdapterArrayList(): ArrayList<InquiryInformationModel>? {
        return arrayList
    }

    fun addItem(model: InquiryInformationModel, mode: Int): Boolean {

        if (!arrayList.isNullOrEmpty()) {

            val flag = isValidateItem()

            if (flag && mode == 0) {
                return true
            } else {

                if (flag) {
                    arrayList.add(model)
                    notifyDataSetChanged()
                    return true

                } else {
                    return false
                }
            }
        }

        return false
    }

    fun isValidateItem(): Boolean {
        var emptyBox = true
        if (!arrayList.isNullOrEmpty()) {
            for (model in arrayList) {
                if (TextUtils.isEmpty(model.tilInquiryType?.text!!.trim())) {
                    model.tilInquiryType?.error = "Select Inquiry Type"
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilInquirysubtype?.text!!.trim())) {
                    model.tilInquirysubtype?.error = "Select Inquiry Sub Type"
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilLeadstatus?.text!!.trim())) {
                    model.tilLeadstatus?.error = "Select Lead Status"
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilLeadtype?.text!!.trim())) {
                    model.tilLeadtype?.error = "Select Lead Type"
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilAllotmentTo?.text!!.trim())) {
                    model.tilAllotmentTo?.error = "Select Allotment To"
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilFrequency?.text!!.trim())) {
                    model.tilFrequency?.error = "Select Frequency"
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilProposed?.text!!.trim())) {
                    model.tilProposed?.error = "Enter Proposed Amount"
                    emptyBox = false
                }
            }
        }

        return emptyBox
    }

    fun remove(position: Int) {
        if (!arrayList.isNullOrEmpty()) {
            hideErrorTIL(position)
            arrayList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun updateInquiryTypeItem(position: Int , name: String , id: Int) {
        arrayList!![position].Inquirytype = name
        arrayList!![position].InquirytypeId = id
        arrayList!![position].tilInquiryType?.setText(name)
    }
    fun updateInquirySubTypeItem(position: Int , name: String , id: Int) {
        arrayList!![position].Inquirysubtype = name
        arrayList!![position].InquirysubtypeId = id
        arrayList!![position].tilInquirysubtype?.setText(name)
    }
    fun updateFrequencyItem(position: Int , name: String , id: Int) {
        arrayList!![position].Frequency = name

        arrayList!![position].tilFrequency?.setText(name)
    }
    fun updateLeadTypeItem(position: Int , name: String , id: Int) {
        arrayList!![position].Leadtype = name
        arrayList!![position].LeadtypeId = id
        arrayList!![position].tilLeadtype?.setText(name)
    }
    fun updateLeadStatusItem(position: Int , name: String , id: Int) {
        arrayList!![position].Leadstatus = name
        arrayList!![position].LeadstatusId = id
        arrayList!![position].tilLeadstatus?.setText(name)
    }
    fun updateAllotmentToItem(position: Int , name: String , id: Int) {
        arrayList!![position].AllotmentTo = name
        arrayList!![position].AllotmentToId = id
        arrayList!![position].tilAllotmentTo?.setText(name)
    }
    private fun hideErrorTIL(position: Int) {
        if (!arrayList.isNullOrEmpty()) {
            for (i in arrayList.indices) {
                arrayList[i].tilInquiryType?.error = null
                arrayList[i].tilInquirysubtype?.error = null
                arrayList[i].tilLeadtype?.error = null
                arrayList[i].tilLeadstatus?.error = null
                arrayList[i].tilFrequency?.error = null
                arrayList[i].tilAllotmentTo?.error = null
                arrayList[i].tilProposed?.error = null
            }
        }
    }

}
