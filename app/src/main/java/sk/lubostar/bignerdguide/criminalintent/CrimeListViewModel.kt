package sk.lubostar.bignerdguide.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.getInstance()
    val crimeListLiveData = crimeRepository.getCrimes()
}

