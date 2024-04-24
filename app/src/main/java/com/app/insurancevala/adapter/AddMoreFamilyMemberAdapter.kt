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
import com.app.insurancevala.utils.LogUtil
import com.app.insurancevala.utils.TAG
import com.app.insurancevala.utils.errortint
import kotlinx.android.synthetic.main.activity_add_lead.edtMobileNo
import kotlinx.android.synthetic.main.adapter_add_more_family_member.view.*

class AddMoreFamilyMemberAdapter (val arrayList: ArrayList<FamilyMemberInfoModel>?, val recyclerItemClickListener: RecyclerItemClickListener) : RecyclerView.Adapter<AddMoreFamilyMemberAdapter .ViewHolder>()
{
    var context: Context? = null
    var views: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_add_more_family_member, parent, false)
        context = parent.context
        views = view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context!!, position, arrayList!!, recyclerItemClickListener)

        arrayList[position].tilRelation = holder.itemView.edtRelation
        arrayList[position].tilInitial = holder.itemView.edtInitialItem
        arrayList[position].tilFirstName = holder.itemView.edtFirstName
        arrayList[position].tilLastName = holder.itemView.edtLastName
        arrayList[position].tilMobileNo = holder.itemView.edtMobileNo
        arrayList[position].tilDOB = holder.itemView.edtDOB

        /*if (position == 0 && position == arrayList.size - 1) {
            holder.itemView.imgDelete.visibility = View.GONE
            holder.itemView.tvAddMore.visibility = View.VISIBLE
        }
        else if (position == arrayList.size - 1) {
            holder.itemView.imgDelete.visibility = View.VISIBLE
            holder.itemView.tvAddMore.visibility = View.VISIBLE
        }
        else {
            holder.itemView.imgDelete.visibility = View.VISIBLE
            holder.itemView.tvAddMore.visibility = View.GONE
        }*/
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            context: Context,
            position: Int,
            arrayList: ArrayList<FamilyMemberInfoModel>?,
            recyclerItemClickListener: RecyclerItemClickListener
        ) {

            LogUtil.d(TAG,""+arrayList!![position].FirstName)

            itemView.edtRelation.setText(arrayList!![position].Relation)
            itemView.edtInitialItem.setText(arrayList!![position].Initial)
            itemView.edtFirstName.setText(arrayList!![position].FirstName)
            itemView.edtLastName.setText(arrayList!![position].LastName)
            itemView.edtMobileNo.setText(arrayList!![position].MobileNo)
            itemView.edtDOB.setText(arrayList!![position].DateOfBirth)

            itemView.edtRelation.setOnClickListener {
                if(!arrayList[adapterPosition].Relation!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, arrayList[adapterPosition].Relation)
                } else{
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, arrayList[adapterPosition].Relation)
                }
            }

            itemView.imgDelete.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1,"")
            }

            itemView.edtInitialItem.setOnClickListener {
                if(!arrayList[adapterPosition].Initial!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 2, arrayList[adapterPosition].Initial)
                } else{
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 2, arrayList[adapterPosition].Initial)
                }
            }

            itemView.edtDOB.setOnClickListener {
                if(!arrayList[adapterPosition].DateOfBirth!!.isEmpty()) {
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 5, arrayList[adapterPosition].DateOfBirth)
                } else{
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 5, arrayList[adapterPosition].DateOfBirth)
                }
            }

            /*itemView.tvAddMore.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, "")
            }*/

            itemView.imgDelete.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1, "")
            }

            itemView.edtRelation.setSimpleListener {
                arrayList[adapterPosition].Relation = it.toString()
                if (!arrayList[adapterPosition].Relation.isNullOrEmpty()) {
                    itemView.edtRelation.setError(null)
                }
            }
            itemView.edtInitialItem.setSimpleListener {
                arrayList[adapterPosition].Initial = it.toString()
                if (!arrayList[adapterPosition].Initial.isNullOrEmpty()) {
                    itemView.edtInitialItem.setError(null)
                }
            }
            itemView.edtFirstName.setSimpleListener {
                arrayList[adapterPosition].FirstName = it.toString()
                itemView.edtFirstName.setError(null)
            }
            itemView.edtLastName.setSimpleListener {
                arrayList[adapterPosition].LastName = it.toString()
                itemView.edtLastName.setError(null)
            }
            itemView.edtMobileNo.setSimpleListener {
                arrayList[adapterPosition].MobileNo = it.toString()
                itemView.edtMobileNo.setError(null)
            }
            itemView.edtDOB.setSimpleListener {
                if (!arrayList[adapterPosition].DateOfBirth.isNullOrEmpty()) {
                    arrayList[adapterPosition].DateOfBirth = it.toString()
                    itemView.edtDOB.setError(null)
                }
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

    fun getAdapterArrayList(): ArrayList<FamilyMemberInfoModel>? {
        return arrayList
    }

    fun addItem(model: FamilyMemberInfoModel, mode: Int): Boolean {

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
                if (TextUtils.isEmpty(model.tilRelation?.text!!.trim())) {
                    model.tilRelation?.setError("Select Relation", errortint(context!!))
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilInitial?.text!!.trim())) {
                    model.tilInitial?.setError("Select Inital", errortint(context!!))
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilFirstName?.text!!.trim())) {
                    model.tilFirstName?.setError("Enter First Name", errortint(context!!))
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilLastName?.text!!.trim())) {
                    model.tilLastName?.setError("Enter Last Name", errortint(context!!))
                    emptyBox = false
                }
                if (TextUtils.isEmpty(model.tilDOB?.text!!.trim())) {
                    model.tilDOB?.setError("Select Date Of Birth", errortint(context!!))
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

    fun updateRelationItem(position: Int , name: String , id: Int) {
        arrayList!![position].Relation = name
        arrayList[position].RelationId = id
        arrayList[position].tilRelation?.setText(name)
    }
    fun updateInitialItem(position: Int , name: String , id: Int) {
        arrayList!![position].Initial = name
        arrayList[position].InitialID = id
        arrayList[position].tilInitial?.setText(name)
    }
    fun updateDOBItem(position: Int , Date: String, mDate: String) {
        arrayList!![position].DateOfBirth = Date
        arrayList!![position].mDateOfBirth = mDate
        arrayList[position].tilDOB?.setText(Date)
    }

    private fun hideErrorTIL(position: Int) {
        if (!arrayList.isNullOrEmpty()) {
            for (i in arrayList.indices) {
                arrayList[i].tilRelation?.setError(null)
                arrayList[i].tilInitial?.setError(null)
                arrayList[i].tilFirstName?.setError(null)
                arrayList[i].tilLastName?.setError(null)
                arrayList[i].tilMobileNo?.setError(null)
                arrayList[i].tilDOB?.setError(null)
            }
        }
    }

}
