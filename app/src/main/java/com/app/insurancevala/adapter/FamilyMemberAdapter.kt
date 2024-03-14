package com.app.insurancevala.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.FamilyDetailsModel
import com.app.insurancevala.utils.gone
import kotlinx.android.synthetic.main.adapter_family_details.view.*
import java.util.*

class FamilyMemberAdapter(val mContext: Context,
                          val arrayList: ArrayList<FamilyDetailsModel>?,
                          private val recyclerItemClickListener: RecyclerClickListener
) : RecyclerView.Adapter<FamilyMemberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_family_details, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(mContext, arrayList!![position]!!, recyclerItemClickListener)
        if (position == arrayList.size - 1) {
            holder.itemView.view.gone()
        }
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            mContext: Context,
            model: FamilyDetailsModel,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            if (!model.RelationName.isNullOrEmpty()) {
                itemView.txtRelation.text = model.RelationName
            }
            if (model.FirstName != null && model.LastName != null) {
                itemView.txtName.text = model.FirstName + " " + model.LastName
            }
            if (!model.BirthDate.isNullOrEmpty()) {
                itemView.txtBirthDate.text = model.BirthDate
            }
        }
    }
}