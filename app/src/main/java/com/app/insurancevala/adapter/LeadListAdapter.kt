package com.app.insurancevala.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.LeadModel
import kotlinx.android.synthetic.main.adapter_lead_item.view.*

class LeadListAdapter(private val context: Context?, private val arrayList: ArrayList<LeadModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<LeadListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_lead_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context!!, position, arrayList, recyclerItemClickListener)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            context: Context,
            position: Int,
            arrayList : ArrayList<LeadModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {

            if(!arrayList[position].FirstName.isNullOrEmpty()) {
                itemView.txtName.text = arrayList[position].FirstName +" "+ arrayList[position].LastName
            }

            if(!arrayList[position].EmailID.isNullOrEmpty()) {
                itemView.txtEmail.text = arrayList[position].EmailID
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