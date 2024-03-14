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
import com.app.insurancevala.utils.LogUtil
import com.app.insurancevala.utils.TAG
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.adapter_add_more_inquiry.view.*

class AddMoreInquiryAdapter (val arrayList: ArrayList<InquiryInformationModel>?, val  AddMore: Boolean, val recyclerItemClickListener: RecyclerItemClickListener) : RecyclerView.Adapter<AddMoreInquiryAdapter .ViewHolder>()
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
        arrayList[position].tilInquiryDate = holder.itemView.edtInquiryDate

        if (AddMore){
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
        } else {
            holder.itemView.tvAddMore.gone()
            holder.itemView.imgDelete.gone()
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
            itemView.edtInquiryDate.setText(arrayList!![position].InquiryDate)

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

            itemView.edtInquiryDate.setOnClickListener {
                if(!arrayList[adapterPosition].InquiryDate!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 6, arrayList[adapterPosition].InquiryDate)
                } else{
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 6, arrayList[adapterPosition].InquiryDate)
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
                if (!arrayList[adapterPosition].Inquirytype.isNullOrEmpty()) {
                    itemView.edtInquiryType.setError(null)
                }
            }
            itemView.edtInquirySub.setSimpleListener {
                arrayList[adapterPosition].Inquirysubtype = it.toString()
                if (!arrayList[adapterPosition].Inquirysubtype.isNullOrEmpty()) {
                    itemView.edtInquirySub.setError(null)
                }
            }
            itemView.edtFrequency.setSimpleListener {
                if (!arrayList[adapterPosition].Frequency.isNullOrEmpty()) {
                    arrayList[adapterPosition].Frequency = it.toString()
                    itemView.edtFrequency.setError(null)
                }
            }
            itemView.edtInquiryDate.setSimpleListener {
                if (!arrayList[adapterPosition].InquiryDate.isNullOrEmpty()) {
                    arrayList[adapterPosition].InquiryDate = it.toString()
                    itemView.edtInquiryDate.setError(null)
                }
            }
            itemView.edtLeadType.setSimpleListener {
                if (!arrayList[adapterPosition].Leadtype.isNullOrEmpty()) {
                    arrayList[adapterPosition].Leadtype = it.toString()
                    itemView.edtLeadType.setError(null)
                }
            }
            itemView.edtLeadStatus.setSimpleListener {
                if (!arrayList[adapterPosition].Leadstatus.isNullOrEmpty()) {
                    arrayList[adapterPosition].Leadstatus = it.toString()
                    itemView.edtLeadStatus.setError(null)
                }
            }

            itemView.edtAllotmentTo.setSimpleListener {
                if (!arrayList[adapterPosition].AllotmentTo.isNullOrEmpty()) {
                    arrayList[adapterPosition].AllotmentTo = it.toString()
                    itemView.edtAllotmentTo.setError(null)
                }
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
                LogUtil.d(TAG,"===>111 = "+model.tilInquiryType?.text!!.trim())
                LogUtil.d(TAG,"===>222 = "+model.tilInquirysubtype?.text!!.trim())
                LogUtil.d(TAG,"===>333 = "+model.tilLeadstatus?.text!!.trim())
                LogUtil.d(TAG,"===>444 = "+model.tilLeadtype?.text!!.trim())
                LogUtil.d(TAG,"===>555 = "+model.tilAllotmentTo?.text!!.trim())
                LogUtil.d(TAG,"===>666 = "+model.tilFrequency?.text!!.trim())
                LogUtil.d(TAG,"===>777 = "+model.tilProposed?.text!!.trim())
                LogUtil.d(TAG,"===>888 = "+model.tilInquiryDate?.text!!.trim())
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
                if (TextUtils.isEmpty(model.tilInquiryDate?.text!!.trim())) {
                    model.tilInquiryDate?.error = "Select Inquiry Date"
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
    fun updateInquiryDateItem(position: Int , date: String , mdate: String) {
        arrayList!![position].InquiryDate = date
        arrayList!![position].mInquiryDate = mdate
        arrayList!![position].tilInquiryDate?.setText(date)
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
