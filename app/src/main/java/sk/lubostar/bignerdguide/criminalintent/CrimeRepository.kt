package sk.lubostar.bignerdguide.criminalintent

import android.content.Context
import androidx.room.Room
import sk.lubostar.bignerdguide.criminalintent.database.CrimeDatabase
import java.lang.IllegalStateException
import java.util.*

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
        CrimeDatabase::class.java, DB_NAME).build()

    private val crimeDao = db.crimeDao()

    fun getCrimes() = crimeDao.getCrimes()
    fun getCrime(id: UUID) = crimeDao.getCrime(id)
}