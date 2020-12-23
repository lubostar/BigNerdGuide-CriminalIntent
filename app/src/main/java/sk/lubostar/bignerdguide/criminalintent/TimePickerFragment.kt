package sk.lubostar.bignerdguide.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class TimePickerFragment: DialogFragment() {
    companion object{
        private const val ARG_HOUR = "arg_hour"
        private const val ARG_MINUTE = "arg_minute"

        fun newInstance(hour: Int?, minute: Int?) = TimePickerFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_HOUR, hour ?: 0)
                putInt(ARG_MINUTE, minute ?: 0)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            targetFragment?.let {
                (it as Callbacks).onTimeSelected(hourOfDay, minute)
            }
        }

        val hour = arguments?.getInt(ARG_HOUR) ?: 0
        val minute = arguments?.getInt(ARG_MINUTE) ?: 0
        return TimePickerDialog(requireContext(), timeListener, hour, minute, true)
    }

    interface Callbacks{
        fun onTimeSelected(hour: Int, minute: Int)
    }
}