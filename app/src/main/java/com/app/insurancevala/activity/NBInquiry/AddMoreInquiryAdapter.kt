package com.app.insurancevala.activity.NBInquiry

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.AddClosingAmountAdapter
import com.app.insurancevala.adapter.AddProspectAmountAdapter
import com.app.insurancevala.interFase.RecyclerItemClickListener
import com.app.insurancevala.model.pojo.ClosingAmountInfoModel
import com.app.insurancevala.model.pojo.InquiryInformationModel
import com.app.insurancevala.model.pojo.InquiryTypeModel
import com.app.insurancevala.model.pojo.ProposedAmountInfoModel
import com.app.insurancevala.utils.LogUtil
import com.app.insurancevala.utils.TAG
import com.app.insurancevala.utils.errortint
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.adapter_add_more_inquiry.view.*

class AddMoreInquiryAdapter(
    val arrayList: ArrayList<InquiryInformationModel>?,
    var arrayListInquiryType: ArrayList<InquiryTypeModel>?,
    val AddMore: Boolean,
    val view: Boolean,
    val recyclerItemClickListener: RecyclerItemClickListener
) : RecyclerView.Adapter<AddMoreInquiryAdapter.ViewHolder>() {
    var context: Context? = null
    var views: View? = null

    private var addProspectAmountAdapter: AddProspectAmountAdapter? = null
    private var addClosingAmountAdapter: AddClosingAmountAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_add_more_inquiry, parent, false)
        context = parent.context
        views = view
        return ViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindItems(context!!, position, arrayList!!, recyclerItemClickListener)

        arrayList[position].tilFamilyMember = holder.itemView.edtFamilyMember
        arrayList[position].tilInquiryType = holder.itemView.edtInquiryType
        arrayList[position].tilInquirysubtype = holder.itemView.edtInquirySub
        arrayList[position].tilFrequency = holder.itemView.edtFrequency
        arrayList[position].tilLeadtype = holder.itemView.edtLeadType
        arrayList[position].tilLeadstatus = holder.itemView.edtLeadStatus
        arrayList[position].tilAllotmentTo = holder.itemView.edtAllotmentTo
        arrayList[position].tilCoPersonAllotmentTo = holder.itemView.edtCoPersonAllotmentTo
        arrayList[position].tilInquiryDate = holder.itemView.edtInquiryDate

        if (AddMore) {
            if (position == 0 && position == arrayList.size - 1) {
                holder.itemView.imgDelete.visibility = View.GONE
                holder.itemView.tvAddMore.visibility = View.VISIBLE
            } else if (position == arrayList.size - 1) {
                holder.itemView.imgDelete.visibility = View.VISIBLE
                holder.itemView.tvAddMore.visibility = View.VISIBLE
            } else {
                holder.itemView.imgDelete.visibility = View.VISIBLE
                holder.itemView.tvAddMore.visibility = View.GONE
            }
        } else {
            holder.itemView.tvAddMore.gone()
            holder.itemView.imgDelete.gone()
            if (view) {
                (context as BaseActivity).enableDisableViewGroup(holder.itemView.llContent, false)
            }
        }

        if (holder.itemView.edtInquiryType.text.toString() == "") {
            holder.itemView.cvProposedAmount.gone()
        }

        if (arrayList[position].LeadstatusId == 7) {
            holder.itemView.cvClosingAmount.visible()
        }

        val arrayListInquiryName: ArrayList<InquiryTypeModel> = ArrayList()

        for (i in arrayListInquiryType!!.indices) {
            if (arrayListInquiryType!![i].ID == position) {
                arrayListInquiryName.add(InquiryTypeModel(InquiryType = arrayListInquiryType!![i].InquiryType))
            }
        }

        if (arrayList[position].ProposedAmount != null && !arrayList[position].tilInquiryType!!.text.isNullOrEmpty()) {
            if (arrayList[position].ProposedAmount!!.size > 0) {
                holder.itemView.cvProposedAmount.visible()
                val arraylistProposedAmountInfoModel: ArrayList<ProposedAmountInfoModel> = ArrayList()

                for (i in arrayList[position].ProposedAmount!!.indices) {
                    if (arrayList[position].ProposedAmount!![i].ID == position) {
                        arraylistProposedAmountInfoModel.add(arrayList[position].ProposedAmount!![i])
                    }
                }

                addProspectAmountAdapter =
                    AddProspectAmountAdapter(
                        arraylistProposedAmountInfoModel,
                        arrayListInquiryName,
                        recyclerItemClickListener
                    )
                holder.itemView.rvProposedAmount.adapter = addProspectAmountAdapter
            }
        }

        if (arrayList[position].ClosingAmount != null && !arrayList[position].tilInquiryType!!.text.isNullOrEmpty()) {
            if (arrayList[position].ClosingAmount!!.size > 0) {
                val arraylistClosingAmountInfoModel: ArrayList<ClosingAmountInfoModel> = ArrayList()

                for (i in arrayList[position].ClosingAmount!!.indices) {
                    if (arrayList[position].ClosingAmount!![i].ID == position) {
                        arraylistClosingAmountInfoModel.add(arrayList[position].ClosingAmount!![i])
                    }
                }

                addClosingAmountAdapter =
                    AddClosingAmountAdapter(
                        arraylistClosingAmountInfoModel,
                        arrayListInquiryName,
                        recyclerItemClickListener
                    )
                holder.itemView.rvClosingAmount.adapter = addClosingAmountAdapter
            }
        }
    }

    fun setAddProspectAmountAdapter(adapter: AddProspectAmountAdapter) {
        addProspectAmountAdapter = adapter
    }

    fun setAddClosingAmountAdapter(adapter: AddClosingAmountAdapter) {
        addClosingAmountAdapter = adapter
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View, private val adapter: AddMoreInquiryAdapter) :
        RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            context: Context,
            position: Int,
            arrayList: ArrayList<InquiryInformationModel>?,
            recyclerItemClickListener: RecyclerItemClickListener
        ) {
            itemView.edtFamilyMember.setText(arrayList!![position].FamilyMember)
            itemView.edtInquiryType.setText(arrayList!![position].Inquirytype)
            itemView.edtInquirySub.setText(arrayList!![position].Inquirysubtype)
            itemView.edtFrequency.setText(arrayList!![position].Frequency)
            itemView.edtLeadType.setText(arrayList!![position].Leadtype)
            itemView.edtLeadStatus.setText(arrayList!![position].Leadstatus)
            itemView.edtAllotmentTo.setText(arrayList!![position].AllotmentTo)
            itemView.edtCoPersonAllotmentTo.setText(arrayList!![position].CoPersonAllotmentTo)
            itemView.edtInquiryDate.setText(arrayList!![position].InquiryDate)

            itemView.edtFamilyMember.setOnClickListener {
                if (!arrayList[adapterPosition].FamilyMember!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        1,
                        arrayList[adapterPosition].FamilyMember
                    )
                } else {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        1,
                        arrayList[adapterPosition].FamilyMember
                    )
                }
            }

            itemView.edtInquiryType.setOnClickListener {
                if (!arrayList[adapterPosition].Inquirytype!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        1,
                        arrayList[adapterPosition].Inquirytype
                    )
                } else {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        1,
                        arrayList[adapterPosition].Inquirytype
                    )
                }
            }

            itemView.edtInquirySub.setOnClickListener {
                if (!arrayList[adapterPosition].Inquirysubtype!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        2,
                        arrayList[adapterPosition].Inquirysubtype
                    )
                } else {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        2,
                        arrayList[adapterPosition].Inquirysubtype
                    )
                }
            }

            itemView.edtFrequency.setOnClickListener {
                if (!arrayList[adapterPosition].Frequency!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        3,
                        arrayList[adapterPosition].Frequency
                    )
                } else {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        3,
                        arrayList[adapterPosition].Frequency
                    )
                }
            }

            itemView.edtInquiryDate.setOnClickListener {
                if (!arrayList[adapterPosition].InquiryDate!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        6,
                        arrayList[adapterPosition].InquiryDate
                    )
                } else {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        6,
                        arrayList[adapterPosition].InquiryDate
                    )
                }
            }

            itemView.edtLeadType.setOnClickListener {
                if (!arrayList[adapterPosition].Leadtype!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        4,
                        arrayList[adapterPosition].Leadtype
                    )
                } else {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        4,
                        arrayList[adapterPosition].Leadtype
                    )
                }
            }

            itemView.edtLeadStatus.setOnClickListener {
                if (!arrayList[adapterPosition].Leadstatus!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        5,
                        arrayList[adapterPosition].Leadstatus
                    )
                } else {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        5,
                        arrayList[adapterPosition].Leadstatus
                    )
                }
            }

            itemView.edtAllotmentTo.setOnClickListener {
                if (!arrayList[adapterPosition].AllotmentTo!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        1,
                        arrayList[adapterPosition].AllotmentTo
                    )
                } else {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        1,
                        arrayList[adapterPosition].AllotmentTo
                    )
                }
            }

            itemView.edtCoPersonAllotmentTo.setOnClickListener {
                if (!arrayList[adapterPosition].CoPersonAllotmentTo!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        1,
                        arrayList[adapterPosition].CoPersonAllotmentTo
                    )
                } else {
                    recyclerItemClickListener.onItemClickEvent(
                        it,
                        adapterPosition,
                        1,
                        arrayList[adapterPosition].CoPersonAllotmentTo
                    )
                }
            }

            itemView.tvAddMore.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, "")
            }

            itemView.imgDelete.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, "")
            }

            itemView.edtFamilyMember.setSimpleListener {
                arrayList[adapterPosition].FamilyMember = it.toString()
                if (!arrayList[adapterPosition].FamilyMember.isNullOrEmpty()) {
                    itemView.edtFamilyMember.setError(null)
                }
            }

            itemView.edtInquiryType.setSimpleListener {
                arrayList[adapterPosition].Inquirytype = it.toString()
                if (!arrayList[adapterPosition].Inquirytype.isNullOrEmpty()) {
                    itemView.edtInquiryType.setError(null)
                }

                if (!itemView.edtInquiryType.equals("")) {
                    itemView.rvProposedAmount.layoutManager =
                        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    itemView.rvProposedAmount.isNestedScrollingEnabled = false

                    val arrayListProposedAmount = ArrayList<ProposedAmountInfoModel>()
                    val arrayListClosingAmount = ArrayList<ClosingAmountInfoModel>()
                    val arrayListInquiryType = ArrayList<InquiryTypeModel>()

                    val InquiryType = itemView.edtInquiryType.text.split(", ")
                    for (i in InquiryType.indices) {
                        arrayListProposedAmount.add(ProposedAmountInfoModel(ID = adapterPosition))
                        arrayListClosingAmount.add(ClosingAmountInfoModel(ID = adapterPosition))
                        arrayListInquiryType.add(InquiryTypeModel(InquiryType = InquiryType[i]))
                    }

                    val adapterProposedAmount =
                        AddProspectAmountAdapter(
                            arrayListProposedAmount,
                            arrayListInquiryType,
                            recyclerItemClickListener
                        )
                    itemView.rvProposedAmount.adapter = adapterProposedAmount

                    this.adapter.setAddProspectAmountAdapter(adapterProposedAmount)

                    val adapterClosingAmount = AddClosingAmountAdapter(
                        arrayListClosingAmount,
                        arrayListInquiryType,
                        recyclerItemClickListener
                    )
                    itemView.rvClosingAmount.adapter = adapterClosingAmount

                    this.adapter.setAddClosingAmountAdapter(adapterClosingAmount)

                    itemView.cvProposedAmount.visible()
                } else {
                    itemView.cvProposedAmount.gone()
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

                    if (arrayList[absoluteAdapterPosition].LeadstatusId == 7) {
                        itemView.cvClosingAmount.visible()
                    } else {
                        itemView.cvClosingAmount.gone()
                    }
                }
            }

            itemView.edtAllotmentTo.setSimpleListener {
                if (!arrayList[adapterPosition].AllotmentTo.isNullOrEmpty()) {
                    arrayList[adapterPosition].AllotmentTo = it.toString()
                    itemView.edtAllotmentTo.setError(null)
                }
            }

            itemView.edtCoPersonAllotmentTo.setSimpleListener {
                if (!arrayList[adapterPosition].CoPersonAllotmentTo.isNullOrEmpty()) {
                    arrayList[adapterPosition].CoPersonAllotmentTo = it.toString()
                    itemView.edtCoPersonAllotmentTo.setError(null)
                }
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

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) =
                        onTextChanged(p0)
                }
            }
        }

    }

    /*fun getProspectAmountAdapterArrayList(): ArrayList<ProposedAmountInfoModel>? {
        return addProspectAmountAdapter!!.getAdapterArrayList()
    }

    fun getClosingAmountAdapterArrayList(): ArrayList<ClosingAmountInfoModel>? {
        return addClosingAmountAdapter!!.getAdapterArrayList()
    }*/

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
            LogUtil.d(TAG, "" + arrayList)
            for ((index, model) in arrayList.withIndex()) {
                if (TextUtils.isEmpty(model.tilFamilyMember?.text!!.trim())) {
                    model.tilFamilyMember?.setError("Select Family Member", errortint(context!!))
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilInquiryType?.text!!.trim())) {
                    model.tilInquiryType?.setError("Select Inquiry Type", errortint(context!!))
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilInquirysubtype?.text!!.trim())) {
                    model.tilInquirysubtype?.setError(
                        "Select Inquiry Sub Type",
                        errortint(context!!)
                    )
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilLeadstatus?.text!!.trim())) {
                    model.tilLeadstatus?.setError("Select Lead Status", errortint(context!!))
                    emptyBox = false
                }
                if (addProspectAmountAdapter != null) {
                    if (!addProspectAmountAdapter!!.isValidateItem()) {
                        emptyBox = false
                    } else {
                        updateProposedAmountItem(
                            index,
                            addProspectAmountAdapter!!.getAdapterArrayList()!!
                        )
                    }
                }
                if (model.LeadstatusId == 7) {
                    if (addClosingAmountAdapter != null) {
                        if (!addClosingAmountAdapter!!.isValidateItem()) {
                            emptyBox = false
                        } else {
                            updateClosingAmountItem(
                                index,
                                addClosingAmountAdapter!!.getAdapterArrayList()!!
                            )
                        }
                    }
                }
                if (TextUtils.isEmpty(model.tilLeadtype?.text!!.trim())) {
                    model.tilLeadtype?.setError("Select Lead Type", errortint(context!!))
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilAllotmentTo?.text!!.trim())) {
                    model.tilAllotmentTo?.setError("Select Allotment To", errortint(context!!))
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilFrequency?.text!!.trim())) {
                    model.tilFrequency?.setError("Select Frequency", errortint(context!!))
                    emptyBox = false
                }
                /*if (TextUtils.isEmpty(model.tilClosingAmount?.text!!.trim())) {
                    model.tilClosingAmount?.setError("Enter Closing Premium Amount", errortint(context!!))
                    emptyBox = false
                }*/
                if (TextUtils.isEmpty(model.tilInquiryDate?.text!!.trim())) {
                    model.tilInquiryDate?.setError("Select Inquiry Date", errortint(context!!))
                    emptyBox = false
                }
            }
        }

        return emptyBox
    }

    fun clearAllFamilyMembers() {
        for (item in arrayList.orEmpty()) {
            item.FamilyMember = ""
            item.tilFamilyMember?.setText("")
        }
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        if (!arrayList.isNullOrEmpty()) {
            hideErrorTIL(position)
            arrayList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun updateProposedAmountItem(position: Int, arraylist: ArrayList<ProposedAmountInfoModel>) {
        if (arrayList!![position].ProposedAmount.isNullOrEmpty()) {
            arrayList[position].ProposedAmount = ArrayList()
        }

        if (position == arraylist[0].ID) {
            arrayList[position].ProposedAmount = arraylist
        }
    }

    fun updateClosingAmountItem(position: Int, arraylist: ArrayList<ClosingAmountInfoModel>) {
        if (arrayList!![position].ClosingAmount.isNullOrEmpty()) {
            arrayList[position].ClosingAmount = ArrayList()
        }

        if (position == arraylist[0].ID) {
            arrayList[position].ClosingAmount = arraylist
        }
    }

    fun updateFamilyMemberItem(position: Int, name: String, id: Int) {
        arrayList!![position].FamilyMember = name
        arrayList!![position].FamilyMemberId = id
        arrayList!![position].tilFamilyMember?.setText(name)
    }

    fun updateInquiryTypeItem(position: Int, name: String, id: Int) {
        arrayList!![position].Inquirytype = name
        arrayList!![position].InquirytypeId = id.toString()
        arrayList!![position].tilInquiryType?.setText(name)
    }

    fun updateInquirySubTypeItem(position: Int, name: String, id: Int) {
        arrayList!![position].Inquirysubtype = name
        arrayList!![position].InquirysubtypeId = id.toString()
        arrayList!![position].tilInquirysubtype?.setText(name)
    }

    fun updateInquiryTypeItem(position: Int, name: List<String>, id: List<String>) {
        var inquiryType = ""
        var inquiryTypeId = ""
        for (i in name.indices) {
            if (i > 0) {
                inquiryType = inquiryType + ", " + name[i]
                inquiryTypeId = inquiryTypeId + ", " + id[i]
            } else {
                inquiryType = inquiryType + name[i]
                inquiryTypeId = inquiryTypeId + id[i]
            }
        }
        arrayList!![position].Inquirytype = inquiryType
        arrayList!![position].InquirytypeId = inquiryTypeId
        arrayList!![position].tilInquiryType?.setText(inquiryType)
    }


    fun updateInquirySubTypeItem(position: Int, name: List<String>, id: List<String>) {
        var inquirySubType = ""
        var inquirySubTypeId = ""
        for (i in name.indices) {
            if (i > 0) {
                inquirySubType = inquirySubType + ", " + name[i]
                inquirySubTypeId = inquirySubTypeId + ", " + id[i]
            } else {
                inquirySubType = inquirySubType + name[i]
                inquirySubTypeId = inquirySubTypeId + id[i]
            }
        }
        arrayList!![position].Inquirysubtype = inquirySubType
        arrayList!![position].InquirysubtypeId = inquirySubTypeId
        arrayList!![position].tilInquirysubtype?.setText(inquirySubType)
    }

    fun updateFrequencyItem(position: Int, name: String, id: Int) {
        arrayList!![position].Frequency = name
        arrayList!![position].tilFrequency?.setText(name)
    }

    fun updateLeadTypeItem(position: Int, name: String, id: Int) {
        arrayList!![position].Leadtype = name
        arrayList!![position].LeadtypeId = id
        arrayList!![position].tilLeadtype?.setText(name)
    }

    fun updateLeadStatusItem(position: Int, name: String, id: Int) {
        arrayList!![position].Leadstatus = name
        arrayList!![position].LeadstatusId = id
        arrayList!![position].tilLeadstatus?.setText(name)
    }

    fun updateAllotmentToItem(position: Int, name: String, id: Int) {
        arrayList!![position].AllotmentTo = name
        arrayList!![position].AllotmentToId = id
        arrayList!![position].tilAllotmentTo?.setText(name)
    }

    fun updateCoPersonAllotmentToItem(position: Int, name: String, id: Int) {
        arrayList!![position].CoPersonAllotmentTo = name
        arrayList!![position].CoPersonAllotmentToId = id
        arrayList!![position].tilCoPersonAllotmentTo?.setText(name)
    }

    fun updateInquiryDateItem(position: Int, date: String, mdate: String) {
        arrayList!![position].InquiryDate = date
        arrayList!![position].mInquiryDate = mdate
        arrayList!![position].tilInquiryDate?.setText(date)
    }

    private fun hideErrorTIL(position: Int) {
        if (!arrayList.isNullOrEmpty()) {
            for (i in arrayList.indices) {
                arrayList[i].tilFamilyMember?.setError(null)
                arrayList[i].tilInquiryDate?.setError(null)
                arrayList[i].tilInquiryType?.setError(null)
                arrayList[i].tilInquirysubtype?.setError(null)
                arrayList[i].tilLeadtype?.setError(null)
                arrayList[i].tilLeadstatus?.setError(null)
                arrayList[i].tilFrequency?.setError(null)
                arrayList[i].tilAllotmentTo?.setError(null)
                arrayList[i].tilCoPersonAllotmentTo?.setError(null)
            }
        }
    }
}
