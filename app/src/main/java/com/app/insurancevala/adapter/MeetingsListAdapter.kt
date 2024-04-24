package com.app.insurancevala.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.MeetingsModel
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.convertDateStringToString
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.adapter_meeting_item.view.*
import kotlinx.android.synthetic.main.adapter_meeting_item.view.imgDone
import kotlinx.android.synthetic.main.adapter_meeting_item.view.imgEdit
import kotlinx.android.synthetic.main.adapter_meeting_item.view.txtCreatedBy
import kotlinx.android.synthetic.main.adapter_meeting_item.view.txtStatus
import kotlinx.android.synthetic.main.adapter_task_item.view.*

class MeetingsListAdapter(private val context: Context?, private val arrayList: ArrayList<MeetingsModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<MeetingsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_meeting_item, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context!!, position,arrayList, recyclerItemClickListener)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var context: Context? = null

        @RequiresApi(Build.VERSION_CODES.M)
        fun bindItems(
            context: Context,
            position: Int,
            arrayList: ArrayList<MeetingsModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context

            if(!arrayList[position].Purpose.isNullOrEmpty()) {
                itemView.txtTitle.text = arrayList[position].Purpose
            }

            if(!arrayList[position].MeetingStatus.isNullOrEmpty()) {
                itemView.txtStatus.text = arrayList[position].MeetingStatus
                if(arrayList[position].MeetingStatus.equals("Completed")) {
                    itemView.imgDone.gone()
                    itemView.imgEdit.gone()
                } else {
                    itemView.imgDone.visible()
                    itemView.imgEdit.visible()
                }
            }
            if(!arrayList[position].StartTime.isNullOrEmpty() && !arrayList[position].EndTime.isNullOrEmpty()) {
                val mStime = convertDateStringToString(arrayList[position].StartTime!! , AppConstant.HH_MM_SS_FORMAT, AppConstant.HH_MM_AA_FORMAT)
                val mEtime = convertDateStringToString(arrayList[position].EndTime!! , AppConstant.HH_MM_SS_FORMAT, AppConstant.HH_MM_AA_FORMAT)

                itemView.txtTime.text = mStime + " to " + mEtime
            }

            if(!arrayList[position].Location.isNullOrEmpty()) {
                itemView.txtLocation.text = arrayList[position].Location
            }

            if(arrayList[position].UpdatedBy == 0 || arrayList[position].UpdatedBy == null)
            {
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
            itemView.imgDone.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 103)
            }


        }
    }
}