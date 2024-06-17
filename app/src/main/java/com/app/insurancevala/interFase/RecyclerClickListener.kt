package com.app.insurancevala.interFase

import android.view.View

interface RecyclerClickListener {
    fun onItemClickEvent(view: View, position: Int, type: Int)

}

interface RecyclerItemClickListener {
    fun onItemClickEvent(view: View, position: Int, type: Int, name : String)
}