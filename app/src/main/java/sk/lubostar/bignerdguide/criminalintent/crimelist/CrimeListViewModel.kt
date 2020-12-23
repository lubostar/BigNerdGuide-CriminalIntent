package sk.lubostar.bignerdguide.criminalintent.crimelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import sk.lubostar.bignerdguide.criminalintent.Crime
import sk.lubostar.bignerdguide.criminalintent.CrimeRepository

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.getInstance()
    val crimeListLiveData: LiveData<List<Crime>> = crimeRepository.getCrimes()

    fun addCrime(crime: Crime) = crimeRepository.insertCrime(crime)
}

