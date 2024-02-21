package com.app.insurancevala.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.NotesModel
import com.app.insurancevala.utils.*
import com.devs.readmoreoption.ReadMoreOption
import kotlinx.android.synthetic.main.adapter_notes_item.view.*
import kotlin.time.measureTime

class NotesListAdapter(private val context: Context?, private val arrayList: ArrayList<NotesModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<NotesListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_notes_item, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context!!, position, arrayList, recyclerItemClickListener)
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
            arrayList : ArrayList<NotesModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context

            if(!arrayList[position].Title.isNullOrEmpty()) {
                itemView.txtTitle.text = arrayList[position].Title
            }

            if(!arrayList[position].Description.isNullOrEmpty()) {
                itemView.txtDescription.text = arrayList[position].Description
            }

            if(arrayList[position].UpdatedBy == 0)
            {
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
            val readMoreOption = ReadMoreOption.Builder(context)
                .textLength(120, ReadMoreOption.TYPE_CHARACTER)
                .moreLabel("Read More")
                .lessLabel("Read Less")
                .moreLabelColor(context.getColor(R.color.color5))
                .lessLabelColor(context.getColor(R.color.color5))
                .labelUnderLine(true)
                .expandAnimation(true)
                .build()

            readMoreOption.addReadMoreTo(itemView.txtDescription, arrayList[position].Description)

            // attachment
            itemView.rvAttachment.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            itemView.rvAttachment.isNestedScrollingEnabled = false

            if(arrayList[position].NoteAttachmentList!!.size > 0) {
                val arrayListAttachment = arrayList[position].NoteAttachmentList!!
                val adapter = AttachmentListAdapter(context, arrayListAttachment)
                itemView.rvAttachment.adapter = adapter

                itemView.rvAttachment.visible()
            } else {
                itemView.rvAttachment.gone()
            }

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }
            itemView.imgEdit.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }
        }
    }
}