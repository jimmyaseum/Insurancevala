package com.app.insurancevala.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.UserModel
import com.app.insurancevala.utils.gone
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adapter_user_item.view.*

class UsersListAdapter(private val context: Context?, private val arrayList: ArrayList<UserModel>, private val recyclerItemClickListener: RecyclerClickListener) : RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_user_item, parent, false)
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
            arrayList : ArrayList<UserModel>,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            this.context = context


            if(!arrayList[position].FirstName.isNullOrEmpty()) {
                itemView.txtName.text = arrayList[position].FirstName + " " + arrayList[position].LastName
            }

            if(!arrayList[position].EmailID.isNullOrEmpty()) {
                itemView.txtEmail.text = arrayList[position].EmailID
            }

            itemView.imgDelete.gone()

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }
            itemView.imgEdit.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }
            itemView.imgDelete.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 103)
            }
            itemView.imgprofile.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 104)
            }

            Glide.with(context)
                .load(arrayList[position].UserImage)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(itemView.imgprofile)

        }
    }
}