package com.app.insurancevala.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.pojo.DocumentsModel
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.convertDateStringToString
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adapter_all_attachment_list.view.*
import java.util.*
import kotlin.collections.ArrayList

class AllAttachmentListAdapter(private val mContext: Context, private val arrayList: ArrayList<DocumentsModel>?, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<AllAttachmentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_all_attachment_list, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(mContext, position, arrayList!![position], recyclerItemClickListener)
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
            arrayList : DocumentsModel,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context

            if (!arrayList.AttachmentURL.isNullOrEmpty()) {
                if(!arrayList.AttachmentName.equals("")) {
                    itemView.txxtname.text = arrayList.AttachmentName
                }

                if(!arrayList.AttachmentType.equals("")) {
                    itemView.txxtType.text = arrayList.AttachmentType
                }
                //pdf
                if(arrayList.AttachmentURL.contains(".pdf")) {
                    itemView.imgFile.setImageResource(R.drawable.ic_file)
                }
                // image
                else {
                    Glide.with(context)
                        .load(arrayList.AttachmentURL)
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                        )
                        .into(itemView.imgFile)
                }
            }

            itemView.rlItemContent.setOnClickListener {
                if (!arrayList.AttachmentURL.isNullOrEmpty()){
                    if(arrayList.AttachmentURL!!.contains(".pdf")) {
                        var format = "https://docs.google.com/gview?embedded=true&url=%s"
                        val fullPath: String = java.lang.String.format(Locale.ENGLISH, format, arrayList.AttachmentURL)
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullPath))
                        context.startActivity(browserIntent)
                    } else {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.AttachmentURL))
                        context.startActivity(browserIntent)
                    }

                }
            }

            if(arrayList.UpdatedOn.isNullOrEmpty()) {
                if(!arrayList.CreatedOn.isNullOrEmpty()) {
                    val mdate = convertDateStringToString(arrayList.CreatedOn!! , AppConstant.dd_MM_yyyy_Slash, AppConstant.ddmmmyyyy)
                    itemView.txxtdate.text = mdate
                }

            }
            else {
                if(!arrayList.UpdatedOn.isNullOrEmpty()) {
                    val mdate = convertDateStringToString(arrayList.UpdatedOn!! , AppConstant.dd_MM_yyyy_Slash, AppConstant.ddmmmyyyy)
                    itemView.txxtdate.text = mdate
                }
            }

            itemView.imgEdit.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }

            itemView.imgDelete.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }

        }
    }
}