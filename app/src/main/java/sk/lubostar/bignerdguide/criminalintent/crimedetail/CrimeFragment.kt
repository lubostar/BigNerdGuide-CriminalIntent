package sk.lubostar.bignerdguide.criminalintent.crimedetail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.fragment_crime.*
import sk.lubostar.bignerdguide.criminalintent.Crime
import sk.lubostar.bignerdguide.criminalintent.DatePickerFragment
import sk.lubostar.bignerdguide.criminalintent.R
import sk.lubostar.bignerdguide.criminalintent.TimePickerFragment
import java.text.DateFormat
import java.util.*

class CrimeFragment: Fragment(), DatePickerFragment.Callbacks, TimePickerFragment.Callbacks {
    companion object{
        private const val DIALOG_DATE_TAG = "dialog_date_tag"
        private const val DIALOG_TIME_TAG = "dialog_time_tag"
        private const val REQUEST_DATE = 0
        private const val REQUEST_TIME = 1

        private const val ARG_CRIME_ID = "arg_crime_id"

        fun newInstance(uuid: UUID) = CrimeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CRIME_ID, uuid)
                }}
    }

    private val dateFormat = DateFormat.getDateInstance(DateFormat.FULL)

    private lateinit var crime: Crime
    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.fragment_crime, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner, { crime ->
            crime?.let {
                this.crime = crime
                updateUi() }
        })
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int,
                after: Int) {}

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int,
                count: Int) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {}
        }

        crime_title.addTextChangedListener(titleWatcher)
        crime_solved.setOnCheckedChangeListener{ _, isChecked -> crime.isSolved = isChecked }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    private fun updateUi(){
        crime_title.setText(crime.title)
        crime_solved.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        crime_date.apply {
            text = dateFormat.format(crime.date)
            setOnClickListener {
                DatePickerFragment.newInstance(crime.date).apply {
                    setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                    show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE_TAG)
                }
            }
        }
        crime_time.apply {
            text = if(crime.hour == null) "N/A" else "${crime.hour} : ${crime.minute}"
            setOnClickListener {
                TimePickerFragment.newInstance(crime.hour, crime.minute).apply {
                    setTargetFragment(this@CrimeFragment, REQUEST_TIME)
                    show(this@CrimeFragment.parentFragmentManager, DIALOG_TIME_TAG)
                }
            }
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUi()
    }

    override fun onTimeSelected(hour: Int, minute: Int) {
        crime.hour = hour
        crime.minute = minute
        updateUi()
    }
}