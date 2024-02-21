package com.app.insurancevala.adapter.bottomsheetadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.CountriesModel
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.row_select.view.*

class BottomSheetCountriesListAdapter(val context: Context?, private val arrayList : ArrayList<CountriesModel>) : RecyclerView.Adapter<BottomSheetCountriesListAdapter.ViewHolder>() {

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
            arrayList : CountriesModel
        ) {
            this.context = context


            itemView.txtName.text = arrayList.CountryName

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