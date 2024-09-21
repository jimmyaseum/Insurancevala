package com.app.insurancevala.adapter.bottomsheetadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.InquiryTypeModel
import kotlinx.android.synthetic.main.adapter_multi_select_item.view.*

class InquiryTypeMultiSelectAdapter(
    val recyclerItemClickListener: RecyclerClickListener,
    val arrayList: ArrayList<InquiryTypeModel>?
) : RecyclerView.Adapter<InquiryTypeMultiSelectAdapter.ViewHolder>() {

    var selectedItemPosition = -1;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_multi_select_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList!![position], recyclerItemClickListener, position)
        holder.itemView.cbName.isChecked = arrayList[position].IsSelected!!
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            model: InquiryTypeModel,
            recyclerItemClickListener: RecyclerClickListener,
            position: Int
        ) {
            itemView.cbName.text = model.InquiryType
            itemView.cbName.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1)
                updateItem(position)
            }
        }
    }

    fun updateItem(position: Int) {
        selectedItemPosition = position
        arrayList!![position].IsSelected = arrayList[position].IsSelected!!.not()
        notifyItemChanged(position)
    }

    fun getSelectedPosition(): Int {
        return selectedItemPosition
    }

    fun getCheckBoxSelected(): List<String>? {

        val arrayListInquiryType = ArrayList<String>()
        for (i in arrayList!!.indices) {
            if (arrayList[i].IsSelected!!) {
                arrayListInquiryType.add(arrayList[i].InquiryType!!)
            }
        }
        return arrayListInquiryType
    }

    fun getCheckBoxSelectedID(): List<String>? {

        val arrayListInquiryType = ArrayList<String>()
        for (i in arrayList!!.indices) {
            if (arrayList[i].IsSelected!!) {
                arrayListInquiryType.add(arrayList[i].ID.toString())
            }
        }
        return arrayListInquiryType
    }
}
