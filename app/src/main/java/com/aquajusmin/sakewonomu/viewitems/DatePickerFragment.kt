package com.aquajusmin.sakewonomu.viewitems

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(val date: Calendar, val resultListener: (y: Int, m: Int, d: Int) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) - 1
        val day = date.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        resultListener(year, month + 1, day)
    }
}
