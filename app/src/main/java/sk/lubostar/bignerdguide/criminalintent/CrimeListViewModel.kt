package sk.lubostar.bignerdguide.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.getInstance()
    val crimeListLiveData: LiveData<List<Crime>> = crimeRepository.getCrimes()
}

