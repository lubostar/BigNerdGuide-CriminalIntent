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
import sk.lubostar.bignerdguide.criminalintent.R
import java.util.*

class CrimeFragment: Fragment() {
    companion object{
        private const val ARG_CRIME_ID = "arg_crime_id"

        fun newInstance(uuid: UUID) = CrimeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CRIME_ID, uuid)
                }}
    }

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
            text = crime.date.toString()
            isEnabled = false
        }
    }
}