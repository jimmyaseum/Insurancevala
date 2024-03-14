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
import com.app.insurancevala.model.response.RecordingsModel
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.convertDateStringToString
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adapter_all_recordings_list.view.*
import java.util.*
import kotlin.collections.ArrayList

class AllRecordingsListAdapter(private val mContext: Context, private val arrayList: ArrayList<RecordingsModel>?, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<AllRecordingsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_all_recordings_list, parent, false)
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
            arrayList : RecordingsModel,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context

            /*itemView.rlItemContent.setOnClickListener {
                if (!arrayList.RecodingFiles.isNullOrEmpty()){
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.RecodingFiles))
                    context.startActivity(browserIntent)
                }
            }*/

            if(!arrayList.RecodingFiles.isNullOrEmpty()) {
                itemView.txtType.text = arrayList.RecodingFiles
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