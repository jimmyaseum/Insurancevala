package com.app.insurancevala.adapter

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerItemClickListener
import com.app.insurancevala.model.pojo.FamilyMemberInfoModel
import com.app.insurancevala.model.pojo.PlanNameInfoModel
import com.app.insurancevala.utils.LogUtil
import com.app.insurancevala.utils.TAG
import com.app.insurancevala.utils.errortint
import kotlinx.android.synthetic.main.adapter_add_more_plan_name.view.*

class AddMorePlanNameAdapter (val arrayList: ArrayList<PlanNameInfoModel>?, val recyclerItemClickListener: RecyclerItemClickListener) : RecyclerView.Adapter<AddMorePlanNameAdapter .ViewHolder>()
{
    var context: Context? = null
    var views: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_add_more_plan_name, parent, false)
        context = parent.context
        views = view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context!!, position, arrayList!!, recyclerItemClickListener)

        arrayList[position].tilPlanName = holder.itemView.edtPlanName

        if (position == 0 && position == arrayList.size - 1) {
            holder.itemView.imgDelete.visibility = View.GONE
        }
        else if (position == arrayList.size - 1) {
            holder.itemView.imgDelete.visibility = View.VISIBLE
        }
        else {
            holder.itemView.imgDelete.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            context: Context,
            position: Int,
            arrayList: ArrayList<PlanNameInfoModel>?,
            recyclerItemClickListener: RecyclerItemClickListener
        ) {

            LogUtil.d(TAG,""+arrayList!![position].PlanName)

            itemView.edtPlanName.setText(arrayList[position].PlanName)

            itemView.imgDelete.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1,"")
            }

            /*itemView.tvAddMore.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, "")
            }*/


            itemView.edtPlanName.setSimpleListener {
                arrayList[adapterPosition].PlanName = it.toString()
                itemView.edtPlanName.setError(null)
            }
        }

        private fun EditText.setSimpleListener(listener: (p0: CharSequence?) -> Unit) {
            this.addTextChangedListener(TextWatcherFactory().create(listener))
        }
        class TextWatcherFactory {
            fun create(onTextChanged: (p0: CharSequence?) -> Unit): android.text.TextWatcher {
                return object : android.text.TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = onTextChanged(p0)
                }
            }
        }

    }

    fun getAdapterArrayList(): ArrayList<PlanNameInfoModel>? {
        return arrayList
    }

    fun addItem(model: PlanNameInfoModel, mode: Int): Boolean {

        if (!arrayList.isNullOrEmpty()) {

            val flag = isValidateItem()

            if (flag && mode == 0) {
                return true
            } else {

                if (flag) {
                    arrayList.add(model)
                    notifyDataSetChanged()
                    return true

                } else {
                    return false
                }
            }
        }

        return false
    }

    fun isValidateItem(): Boolean {
        var emptyBox = true
        if (!arrayList.isNullOrEmpty()) {
            for (model in arrayList) {
                if (TextUtils.isEmpty(model.tilPlanName?.text!!.trim())) {
                    model.tilPlanName?.setError("Enter Plan Name", errortint(context!!))
                    emptyBox = false
                }
            }
        }

        return emptyBox
    }

    fun remove(position: Int) {
        if (!arrayList.isNullOrEmpty()) {
            hideErrorTIL(position)
            arrayList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    private fun hideErrorTIL(position: Int) {
        if (!arrayList.isNullOrEmpty()) {
            for (i in arrayList.indices) {
                arrayList[i].tilPlanName?.setError(null)
            }
        }
    }

}
