package com.app.insurancevala.adapter.bottomsheetadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.StatesModel
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.row_select.view.*

class BottomSheetStatesListAdapter(val context: Context?, private val arrayList : ArrayList<StatesModel>) : RecyclerView.Adapter<BottomSheetStatesListAdapter.ViewHolder>() {

    private var recyclerItemClickListener: RecyclerClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_select, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context!!, position, recyclerItemClickListener!!, arrayList!![position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var context: Context? = null

        fun bindItems(
            context: Context,
            position: Int,
            recyclerItemClickListener: RecyclerClickListener,
            arrayList : StatesModel
        ) {
            this.context = context


            itemView.txtName.text = arrayList.StateName

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }
        }
    }

    fun setRecyclerRowClick(recyclerRowClick: RecyclerClickListener) {
        this.recyclerItemClickListener = recyclerRowClick
    }

    fun updateItem(position: Int) {
        for (i in 0 until arrayList.size) {
            arrayList[i].IsSelected = false
        }
        arrayList[position].IsSelected = true
        notifyItemChanged(position)
    }
}