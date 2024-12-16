package com.aquajusmin.sakewonomu.viewitems

import android.widget.TextView
import androidx.databinding.*


object KomeWoNomuBindingAdapter {

    @BindingAdapter("pointValue")
    @JvmStatic
    fun setSelectedPointValue(view: SelectPointView, newValue: Int?) {
        // Important to break potential infinite loops.
        if (newValue != null && view.getValue() != newValue) {
            view.setValue(newValue)
        }
    }
    @InverseBindingAdapter(attribute = "pointValue")
    @JvmStatic
    fun getSelectedPointValue(view: SelectPointView) : Int {
        return view.getValue()
    }
}
