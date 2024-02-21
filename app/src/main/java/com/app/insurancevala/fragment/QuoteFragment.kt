package com.app.insurancevala.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.insurancevala.R

class QuoteFragment : Fragment() {

    private var views: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        views = inflater.inflate(R.layout.fragment_quote, container, false)
//        SetInitListner()
        return views
    }
}