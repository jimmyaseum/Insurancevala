package com.app.insurancevala.adapter.master

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.InquiryTypeModel
import com.app.insurancevala.utils.gone
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adapter_master_item.view.*

class InquiryTypeListAdapter(private val context: Context?, private val arrayList: ArrayList<InquiryTypeModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<InquiryTypeListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_master_item, parent, false)
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
            arrayList : ArrayList<InquiryTypeModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context


            if(!arrayList[position].InquiryType.isNullOrEmpty()) {
                itemView.txtType.text = arrayList[position].InquiryType
            }

            if(arrayList[position].IsActive != null) {
                itemView.txtStatus.text = arrayList[position].IsActive.toString()
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