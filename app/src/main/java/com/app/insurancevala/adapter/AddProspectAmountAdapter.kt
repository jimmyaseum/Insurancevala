package com.app.insurancevala.adapter

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerItemClickListener
import com.app.insurancevala.model.pojo.InquiryTypeModel
import com.app.insurancevala.model.pojo.ProposedAmountInfoModel
import com.app.insurancevala.utils.LogUtil
import com.app.insurancevala.utils.TAG
import com.app.insurancevala.utils.errortint
import kotlinx.android.synthetic.main.adapter_add_prospect_amount.view.*

class AddProspectAmountAdapter(
    val arrayList: ArrayList<ProposedAmountInfoModel>?,
    val inquiryTypeModel: ArrayList<InquiryTypeModel>,
    val recyclerItemClickListener: RecyclerItemClickListener
) : RecyclerView.Adapter<AddProspectAmountAdapter.ViewHolder>() {
    var context: Context? = null
    var views: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_add_prospect_amount, parent, false)
        context = parent.context
        views = view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(
            context!!,
            position,
            arrayList!!,
            inquiryTypeModel,
            recyclerItemClickListener
        )

        arrayList[position].tilProspectAmount = holder.itemView.edtProspectAmount
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            context: Context,
            position: Int,
            arrayList: ArrayList<ProposedAmountInfoModel>?,
            arrayListInquiryType: ArrayList<InquiryTypeModel>?,
            recyclerItemClickListener: RecyclerItemClickListener
        ) {

            LogUtil.d(TAG, "" + arrayList!![position].ProspectAmount)

            itemView.edtProspectAmount.setText(arrayList[position].ProspectAmount)

            itemView.txtInquiryType.text = arrayListInquiryType!![position].InquiryType


            itemView.edtProspectAmount.setSimpleListener {
                arrayList[adapterPosition].ProspectAmount = it.toString()
                itemView.edtProspectAmount.setError(null)
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

    fun getAdapterArrayList(): ArrayList<ProposedAmountInfoModel>? {
        return arrayList
    }

    fun isValidateItem(): Boolean {
        LogUtil.d(TAG,"Hello==>111 "+ arrayList)
        var emptyBox = true
        if (!arrayList.isNullOrEmpty()) {
            for (model in arrayList) {
                if (TextUtils.isEmpty(model.tilProspectAmount?.text!!.trim())) {
                    model.tilProspectAmount?.setError("Enter Proposed Amount", errortint(context!!))
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

    private fun hideErrorTIL(position: Int) {
        if (!arrayList.isNullOrEmpty()) {
            for (i in arrayList.indices) {
                arrayList[i].tilProspectAmount?.setError(null)
            }
        }
    }

}
