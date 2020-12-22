package sk.lubostar.bignerdguide.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*
import java.util.Calendar.*

class DatePickerFragment: DialogFragment() {
    companion object{
        private const val ARG_DATE = "date_arg"

        fun newInstance(date: Date) = DatePickerFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog{
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val resultDate: Date = GregorianCalendar(year, month, dayOfMonth).time
            targetFragment?.let {
                (it as Callbacks).onDateSelected(resultDate)
            }
        }

        val date = arguments?.getSerializable(ARG_DATE) as Date
        return with(Calendar.getInstance().apply { time = date }){
            DatePickerDialog(requireContext(), dateListener, get(YEAR), get(MONTH), get(DAY_OF_MONTH))
        }
    }

    interface Callbacks {
        fun onDateSelected(date: Date)
    }
}