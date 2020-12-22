package sk.lubostar.bignerdguide.criminalintent.crimedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import sk.lubostar.bignerdguide.criminalintent.Crime
import sk.lubostar.bignerdguide.criminalintent.CrimeRepository
import java.util.*

class CrimeDetailViewModel: ViewModel() {
    private val crimeRepository = CrimeRepository.getInstance()
    private val crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> = Transformations.switchMap(crimeIdLiveData){
        crimeId -> crimeRepository.getCrime(crimeId)
    }

    fun loadCrime(crimeId: UUID){
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime: Crime) = crimeRepository.updateCrime(crime)
}