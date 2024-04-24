package com.app.insurancevala.adapter

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.AddBrochureActivity
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.pojo.DocumentsModel
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adapter_multiple_attachment_list.view.*
import java.io.File
import java.net.URL
import java.util.Locale


class MultipleAttachmentListAdapter(
    private val mContext: Context,
    private val deleteIcon: Boolean,
    private val arrayList: ArrayList<DocumentsModel>?,
    private val recyclerItemClickListener: RecyclerClickListener
) : RecyclerView.Adapter<MultipleAttachmentListAdapter.ViewHolder>() {

    var selectedItemPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_multiple_attachment_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (deleteIcon) {
            holder.itemView.imgRemove.visible()
        } else {
            holder.itemView.imgRemove.gone()
        }
        holder.itemView.img_Download.gone()
        holder.itemView.img_Share.gone()

        holder.bindItems(mContext, arrayList!![position], recyclerItemClickListener, position)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            mContext: Context,
            model: DocumentsModel, recyclerItemClickListener: RecyclerClickListener, position: Int
        ) {
            if (!model.AttachmentURL.isNullOrEmpty()) {
                if (!model.AttachmentName.isNullOrEmpty()) {
                    itemView.txtname.text = model.AttachmentName
                }

                // PDF
                if (model.AttachmentURL.contains(".pdf")) {
                    itemView.imgFile.setImageResource(R.drawable.pdficon)
                }
                // Excel
                else if (model.AttachmentURL.contains(".xls")) {
                    itemView.imgFile.setImageResource(R.drawable.excel_icon)
                }
                // Image
                else if (model.AttachmentURL.endsWith(".jpg") ||
                    model.AttachmentURL.endsWith(".jpeg") ||
                    model.AttachmentURL.endsWith(".png") ||
                    model.AttachmentURL.endsWith(".gif")
                ) {
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
                // Other file types
                else {
                    // Show document icon
                    itemView.imgFile.setImageResource(R.drawable.ic_file)
                }
            }

            itemView.txtname.setText(model.AttachmentName)

            itemView.imgRemove.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1)
            }

            itemView.rlItemContent.setOnClickListener {
                if (!model.AttachmentURL.isNullOrEmpty()) {
                    if (model.AttachmentURL!!.contains(".pdf")) {
                        var format = "https://docs.google.com/gview?embedded=true&url=%s"
                        val fullPath: String =
                            java.lang.String.format(Locale.ENGLISH, format, model.AttachmentURL)
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullPath))
                        mContext.startActivity(browserIntent)
                    } else {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(model.AttachmentURL))
                        mContext.startActivity(browserIntent)
                    }

                }
            }
        }
    }

    fun removeItem(position: Int) {
        if (!arrayList.isNullOrEmpty()) {
            arrayList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getSelectedPosition(): Int {
        return selectedItemPosition
    }

    fun addItem(name: String, uri: Uri, attachmentType: Int) {
        val newItem = DocumentsModel(
            AttachmentName = name,
            AttachmentURL = uri.toString(),
            AttachmentType = if (attachmentType == 1) "Document" else "Image"
        )
        arrayList?.add(newItem)
        notifyDataSetChanged()
    }
}
