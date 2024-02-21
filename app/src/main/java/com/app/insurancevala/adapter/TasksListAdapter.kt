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
import com.app.insurancevala.model.response.TasksModel
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.convertDateStringToString
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.adapter_task_item.view.*
import kotlinx.android.synthetic.main.adapter_task_item.view.imgEdit

class TasksListAdapter(private val context: Context?, private val arrayList: ArrayList<TasksModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<TasksListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_task_item, parent, false)
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
            arrayList : ArrayList<TasksModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context

            if(!arrayList[position].Subject.isNullOrEmpty()) {
                itemView.txtSubject.text = arrayList[position].Subject
            }

            if(!arrayList[position].DueDate.isNullOrEmpty())
            {
                itemView.txtDueDate.text = arrayList[position].DueDate!!
            }

            if(arrayList[position].TaskStatus != "" && arrayList[position].TaskStatus != null) {
                    itemView.txtStatus.text = arrayList[position].TaskStatus

                if(arrayList[position].TaskStatus.equals("Completed")) {
                    itemView.imgDone.gone()
                    itemView.imgEdit.gone()
                } else {
                    itemView.imgDone.visible()
                    itemView.imgEdit.visible()
                }
            }
            if(arrayList[position].Priority != "" && arrayList[position].Priority != null) {
                itemView.txtPriority.text = arrayList[position].Priority
            }

            if(arrayList[position].UpdatedBy == 0) {
                if(!arrayList[position].CreatedOn.isNullOrEmpty()) {
                    val mdate = convertDateStringToString(arrayList[position].CreatedOn!! , AppConstant.ddMMyyyy_HHmmss, AppConstant.dd_LLL_yyyy)
                    val mtime = convertDateStringToString(arrayList[position].CreatedOn!! , AppConstant.ddMMyyyy_HHmmss, AppConstant.HH_MM_AA_FORMAT)
                    itemView.txtCreatedBy.text = "created by " + arrayList[position].CreatedByName + " on " + mdate + " " + mtime
                }

            } else {
                if(!arrayList[position].UpdatedOn.isNullOrEmpty()) {
                    val mdate = convertDateStringToString(arrayList[position].UpdatedOn!! , AppConstant.ddMMyyyy_HHmmss, AppConstant.dd_LLL_yyyy)
                    val mtime = convertDateStringToString(arrayList[position].UpdatedOn!! , AppConstant.ddMMyyyy_HHmmss, AppConstant.HH_MM_AA_FORMAT)
                    itemView.txtCreatedBy.text = "updated by " + arrayList[position].UpdatedByName + " on " + mdate + " " + mtime
                }
            }

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }
            itemView.imgEdit.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }
            itemView.imgDone.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, absoluteAdapterPosition, 103)
            }
        }
    }
}