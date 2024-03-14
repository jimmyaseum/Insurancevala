package com.app.insurancevala.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.model.pojo.AttachmentModel
import com.app.insurancevala.model.pojo.DocumentsModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adapter_attachment_list.view.*
import java.util.*


class AttachmentListAdapter(private val mContext: Context,
                            private val arrayList: ArrayList<DocumentsModel>?
) : RecyclerView.Adapter<AttachmentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_attachment_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(mContext, arrayList!![position]!!)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            mContext: Context,
           model: DocumentsModel,
        ) {

            if (!model.AttachmentURL.isNullOrEmpty()) {
                if(!model.AttachmentName.equals("")) {
                    itemView.txtname.text = model.AttachmentName
                }
                //pdf
                if(model.AttachmentURL.contains(".pdf")) {
                    itemView.imgFile.setImageResource(R.drawable.ic_file)
                }
                // image
                else {
                    Glide.with(mContext)
                        .load(model.AttachmentURL)
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
                if (!model.AttachmentURL.isNullOrEmpty()){
                    if(model.AttachmentURL!!.contains(".pdf")) {
                        var format = "https://docs.google.com/gview?embedded=true&url=%s"
                        val fullPath: String = java.lang.String.format(Locale.ENGLISH, format, model.AttachmentURL)
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullPath))
                        mContext.startActivity(browserIntent)
                    } else {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(model.AttachmentURL))
                        mContext.startActivity(browserIntent)
                    }

                }
            }
        }
    }

}