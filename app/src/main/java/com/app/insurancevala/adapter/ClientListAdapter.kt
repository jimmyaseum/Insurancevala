package com.app.insurancevala.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.LeadModel
import com.app.insurancevala.utils.PrefConstants
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.loadUrl
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.adapter_client_item.view.*

class ClientListAdapter(private val context: Context?, private val arrayList: ArrayList<LeadModel>, private  val type: Int, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<ClientListAdapter.ViewHolder>() {

    var adapterPositionGet: Int = -1
    var sharedPreference: SharedPreference? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_client_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        adapterPositionGet = position
        val sharedPreference = SharedPreference(context!!)

        if (type == 2 && sharedPreference.getPreferenceString(PrefConstants.PREF_USER_TYPE_ID)!!.toInt() == 1){
            holder.itemView.imgDelete.visible()
        }
        holder.bindItems(context, position, type, arrayList, recyclerItemClickListener)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun getAdapterPosition(): Int {
        return adapterPositionGet
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(
            context: Context,
            position: Int,
            type: Int,
            arrayList : ArrayList<LeadModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {

            if(!arrayList[position].FirstName.isNullOrEmpty()) {
                itemView.txtName.text = arrayList[position].FirstName +" "+ arrayList[position].LastName
            }

            if(!arrayList[position].MobileNo.isNullOrEmpty()) {
                itemView.txtMobileNumber.text = arrayList[position].MobileNo
                itemView.txtMobileNumber.visible()
            } else {
                itemView.txtMobileNumber.gone()
            }

            if(!arrayList[position].EmailID.isNullOrEmpty()) {
                itemView.txtEmail.text = arrayList[position].EmailID
                itemView.txtEmail.visible()
            } else {
                itemView.txtEmail.gone()
            }

            if(!arrayList[position].CreatedByName.isNullOrEmpty()) {
                itemView.txtCreatedByName.text = arrayList[position].CreatedByName
                if (type == 2) {
                    itemView.txtCreatedByName.visible()
                }
            } else {
                itemView.txtCreatedByName.gone()
            }

            if (arrayList[position].CategoryID != null && arrayList[position].CategoryID != 0) {
                itemView.rating_bar.rating = arrayList[position].CategoryID!!.toFloat()
                if (arrayList[position].LeadStage != null && arrayList[position].LeadStage != 0){
                    if (arrayList[position].LeadStage == 1) {
                        itemView.rating_bar.progressTintList = ColorStateList.valueOf(getColor(context, R.color.gold))
                    } else if (arrayList[position].LeadStage == 2) {
                        itemView.rating_bar.progressTintList = ColorStateList.valueOf(getColor(context, R.color.silver))
                    }
                }
            } else {
                itemView.rating_bar.rating = 0.0f
            }

            if(!arrayList[position].LeadImage.isNullOrEmpty()) {
                itemView.imgprofile.loadUrl(arrayList[position].LeadImage, R.drawable.ic_profile)
            } else {
                 itemView.imgprofile.setImageResource(R.drawable.ic_profile)
            }

            if(arrayList[position].IsFamilyDetails == true) {
                itemView.imgFamilyMember.gone()
            } else {
                 itemView.imgFamilyMember.visible()
            }

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }

            itemView.imgEdit.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }

            itemView.imgprofile.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 103)
            }

            itemView.imgDelete.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 104)
            }
        }
    }
}