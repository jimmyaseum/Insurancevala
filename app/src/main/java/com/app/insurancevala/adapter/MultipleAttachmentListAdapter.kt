package com.app.insurancevala.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.pojo.AttachmentModel
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.loadURI
import com.app.insurancevala.utils.loadUrl
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.adapter_multiple_attachment_list.view.*

class MultipleAttachmentListAdapter(private val mContext: Context,
                                    private val arrayList: ArrayList<AttachmentModel>?,
                                    private val recyclerItemClickListener: RecyclerClickListener
) : RecyclerView.Adapter<MultipleAttachmentListAdapter.ViewHolder>() {

    var selectedItemPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_multiple_attachment_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(mContext, arrayList!![position], recyclerItemClickListener, position)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            mContext: Context,
            model: AttachmentModel, recyclerItemClickListener: RecyclerClickListener, position: Int
        ) {

            //Photo
            if (model.attachmentType == 2) {
                if (model.isUri) {
                    itemView.imgFile.loadURI(model.attachmentUri!!, R.drawable.ic_profile)
                } else {
                    itemView.imgFile.loadUrl(model.attachmentPath, R.drawable.ic_profile)
                    itemView.imgRemove.gone()
                }

                //Document
            } else {
                if (model.isUri) {
                    itemView.imgFile.setImageResource(R.drawable.ic_file)
                } else {
                    itemView.imgFile.setImageResource(R.drawable.ic_file)
                    itemView.imgRemove.gone()
                }
            }

            itemView.txtname.setText(model.name)

            itemView.imgRemove.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1)
            }

            itemView.rlItemContent.setOnClickListener {
                if (model.attachmentPath != ""){
                    if (!model.isUri) {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(model.attachmentPath))
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
}