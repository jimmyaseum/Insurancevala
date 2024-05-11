package com.app.insurancevala.adapter.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.InquiryTypeWiseCountsModel
import kotlinx.android.synthetic.main.adapter_dashboard_inquiry_type_item.view.*
import kotlinx.android.synthetic.main.adapter_dashboard_lead_status_item.view.ll
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class InquiryTypeWiseCountsAdapter(
    val mContext: Context,
    val arrayList: ArrayList<InquiryTypeWiseCountsModel>?,
    private val recyclerItemClickListener: RecyclerClickListener
) : RecyclerView.Adapter<InquiryTypeWiseCountsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_dashboard_inquiry_type_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(mContext, arrayList!![position]!!, recyclerItemClickListener)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            mContext: Context,
            model: InquiryTypeWiseCountsModel,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            if (!model.InquiryType.isNullOrEmpty()) {
                itemView.txtInquiryType.text = model.InquiryType
            }
            if (model.InquiryTypeCount != null) {
                itemView.txtInquiryTypeCount.text = model.InquiryTypeCount.toString()
            }
            if (model.ProposedAmount != null) {
                itemView.txtInquiryTypeAmount.text =
                    "₹"+ formatAmountWithRupeeSymbol(model.ProposedAmount)
            }

            itemView.ll2.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }
        }

        private fun formatAmountWithRupeeSymbol(amount: Double): String {
            val formatter = DecimalFormat("#,##,###.##")

            return when {
                amount < 1_00_000 -> {
                    // Format amounts less than 1 lakh as 0.1 to 0.99 lakh
                    val formattedAmount = amount / 1_00_000
                    formatter.format(formattedAmount) + " lakh"
                }
                amount < 1_00_00_000 -> {
                    // Format amounts between 1 lakh and 99 lakh as 1.00 to 99.99 lakh
                    val formattedAmount = amount / 1_00_000
                    formatter.format(formattedAmount) + " lakh"
                }
                else -> {
                    // Format amounts 100 lakh (1 crore) and above as crore
                    val croreValue = amount / 1_00_00_000
                    formatter.format(croreValue) + " cr"
                }
            }
        }
    }
}