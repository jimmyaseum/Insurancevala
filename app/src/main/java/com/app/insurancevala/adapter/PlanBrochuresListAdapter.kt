package com.app.insurancevala.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.PlanBrochuresModel
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.convertDateStringToString
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.adapter_plan_brochure_item.view.*

class PlanBrochuresListAdapter(private val context: Context?, private val arrayList: ArrayList<PlanBrochuresModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<PlanBrochuresListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_plan_brochure_item, parent, false)
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
            arrayList : ArrayList<PlanBrochuresModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context

            if(!arrayList[position].CompanyName.isNullOrEmpty()) {
                itemView.txtCompanyName.text = arrayList[position].CompanyName
            }

            if(!arrayList[position].PlanName.isNullOrEmpty()) {
                itemView.txtPlanName.text = arrayList[position].PlanName
            }

            itemView.rvAttachment.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            itemView.rvAttachment.isNestedScrollingEnabled = false

            if(!arrayList[position].PlanBrochureAttachmentList.isNullOrEmpty()) {
                val arrayListAttachment = arrayList[position].PlanBrochureAttachmentList!!
                val adapter = BrochureMultipleAttachmentListAdapter(context, arrayListAttachment, recyclerItemClickListener, false)
                itemView.rvAttachment.adapter = adapter

                itemView.rvAttachment.visible()
            } else {
                itemView.rvAttachment.gone()
            }

            if(arrayList[position].UpdatedBy == 0 || arrayList[position].UpdatedBy == null) {
                if(!arrayList[position].CreatedOn.isNullOrEmpty()) {
                    val mdate = convertDateStringToString(arrayList[position].CreatedOn!! , AppConstant.ddMMyyyy_HHmmss, AppConstant.dd_LLL_yyyy)
                    val mtime = convertDateStringToString(arrayList[position].CreatedOn!! , AppConstant.ddMMyyyy_HHmmss, AppConstant.HH_MM_AA_FORMAT)
                    itemView.txtCreatedBy.text = "Created By " + arrayList[position].CreatedByName + " On " + mdate + " " + mtime
                }

            } else {
                if(!arrayList[position].UpdatedOn.isNullOrEmpty()) {
                    val mdate = convertDateStringToString(arrayList[position].UpdatedOn!! , AppConstant.ddMMyyyy_HHmmss, AppConstant.dd_LLL_yyyy)
                    val mtime = convertDateStringToString(arrayList[position].UpdatedOn!! , AppConstant.ddMMyyyy_HHmmss, AppConstant.HH_MM_AA_FORMAT)
                    itemView.txtCreatedBy.text = "Updated By " + arrayList[position].UpdatedByName + " On " + mdate + " " + mtime
                }
            }

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }
            itemView.imgEdit.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }
            itemView.imgDelete.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 103)
            }
        }
    }
}