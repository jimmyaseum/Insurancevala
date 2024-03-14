package com.app.insurancevala.adapter.master

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.StateModel
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.adapter_master_item.view.*

class StateListAdapter(private val context: Context?, private val arrayList: ArrayList<StateModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<StateListAdapter.ViewHolder>() {

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
            arrayList : ArrayList<StateModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context

            itemView.txtSubType.visible()

            if(!arrayList[position].StateName.isNullOrEmpty()) {
                itemView.txtSubType.text = "State -  " + arrayList[position].StateName
            }

            if(!arrayList[position].CountryName.isNullOrEmpty()) {
                itemView.txtType.text = "Country -  " + arrayList[position].CountryName
                itemView.txtType.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
            }

            if(arrayList[position].IsActive != null) {
                if (arrayList[position].IsActive!!) {
                    itemView.txtStatus.setText("Active")
                    itemView.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.button_green))
                } else{
                    itemView.txtStatus.setText("InActive")
                    itemView.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                }
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