package com.app.insurancevala.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.CallsModel
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.convertDateStringToString
import kotlinx.android.synthetic.main.adapter_calls_item.view.*
import kotlinx.android.synthetic.main.adapter_calls_item.view.txtCreatedBy
import kotlinx.android.synthetic.main.adapter_calls_item.view.txtTitle

class CallsListAdapter(private val context: Context?, private val arrayList: ArrayList<CallsModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<CallsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_calls_item, parent, false)
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
        var images = arrayOf(R.drawable.ic_call_inbound, R.drawable.ic_call_outbound, R.drawable.ic_call_missed, R.drawable.ic_call_schedule)
        var colors = arrayOf(R.color.button_green, R.color.colorBlue, R.color.colorRed, R.color.color5)

        fun bindItems(
            context: Context,
            position: Int,
            arrayList : ArrayList<CallsModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context

            if(!arrayList[position].CallStatus.isNullOrEmpty()) {
                if(arrayList[position].CallStatus.equals("Scheduled")) {
                    itemView.imgCallAction.setImageResource(R.drawable.ic_call_schedule)
                    itemView.imgCallAction.setColorFilter(ContextCompat.getColor(context,R.color.color5), android.graphics.PorterDuff.Mode.SRC_IN)
                } else {
                    if(arrayList[position].CallTypeID == 1 && arrayList[position].CallType.equals("Inbound")) {
                        itemView.imgCallAction.setImageResource(R.drawable.ic_call_inbound)
                        itemView.imgCallAction.setColorFilter(ContextCompat.getColor(context,R.color.button_green), android.graphics.PorterDuff.Mode.SRC_IN)
                    } else if(arrayList[position].CallTypeID == 2 && arrayList[position].CallType.equals("Outbound")) {
                        itemView.imgCallAction.setImageResource(R.drawable.ic_call_outbound)
                        itemView.imgCallAction.setColorFilter(ContextCompat.getColor(context,R.color.colorBlue), android.graphics.PorterDuff.Mode.SRC_IN)
                    } else if(arrayList[position].CallTypeID == 4 && arrayList[position].CallType.equals("Missed")) {
                        itemView.imgCallAction.setImageResource(R.drawable.ic_call_missed)
                        itemView.imgCallAction.setColorFilter(ContextCompat.getColor(context,R.color.colorRed), android.graphics.PorterDuff.Mode.SRC_IN)
                    } else {
                        itemView.imgCallAction.setImageResource(R.drawable.ic_call)
                        itemView.imgCallAction.setColorFilter(ContextCompat.getColor(context,R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN)
                    }
                }
            } else {
                itemView.imgCallAction.setImageResource(R.drawable.ic_call)
                itemView.imgCallAction.setColorFilter(ContextCompat.getColor(context,R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN)
            }

            if(!arrayList[position].Subject.isNullOrEmpty()) {
                itemView.txtTitle.text = arrayList[position].Subject
            }
            if(!arrayList[position].CallStatus.isNullOrEmpty()) {
                itemView.txtStatus.text = arrayList[position].CallStatus
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

        }
    }
}