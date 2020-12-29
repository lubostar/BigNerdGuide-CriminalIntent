package sk.lubostar.bignerdguide.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import sk.lubostar.bignerdguide.criminalintent.database.CrimeDatabase
import sk.lubostar.bignerdguide.criminalintent.database.migration_1_2
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

class CrimeRepository private constructor(context: Context){
    companion object{
        private const val DB_NAME = "crime-db"

        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context){
            if(INSTANCE == null) INSTANCE = CrimeRepository(context)
        }

        fun getInstance() = INSTANCE ?:
        throw IllegalStateException("CrimeRepository mus be initialized!")
    }

    private val db = Room.databaseBuilder(context.applicationContext,
        CrimeDatabase::class.java, DB_NAME)
        .addMigrations(migration_1_2)
        .build()

    private val crimeDao = db.crimeDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)
    fun updateCrime(crime: Crime) = executor.execute { crimeDao.updateCrime(crime) }
    fun insertCrime(crime: Crime) = executor.execute { crimeDao.insertCrime(crime) }
}