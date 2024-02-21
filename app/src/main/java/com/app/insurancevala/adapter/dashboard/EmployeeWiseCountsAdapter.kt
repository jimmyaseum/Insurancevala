package com.app.insurancevala.adapter.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.EmployeeWiseCountsModel
import kotlinx.android.synthetic.main.adapter_dashboard_employee_item.view.*
import kotlinx.android.synthetic.main.adapter_dashboard_lead_status_item.view.ll
import java.util.*

class EmployeeWiseCountsAdapter(val mContext: Context,
                                val arrayList: ArrayList<EmployeeWiseCountsModel>?,
                                private val recyclerItemClickListener: RecyclerClickListener
) : RecyclerView.Adapter<EmployeeWiseCountsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_dashboard_employee_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(mContext, arrayList!![position]!!, position, recyclerItemClickListener)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            mContext: Context,
            model: EmployeeWiseCountsModel,
            position: Int,
            recyclerItemClickListener: RecyclerClickListener
        ) {

            itemView.txtSRNO.text = (position + 1).toString()

            if (!model.UserName.isNullOrEmpty()) {
                itemView.txtEmployeeName.text = model.UserName
            }
            if (model.InquiryTotalCount != null) {
                itemView.txtValue.text = model.InquiryTotalCount.toString()
            }

            itemView.ll3.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 103)
            }
        }
    }
}