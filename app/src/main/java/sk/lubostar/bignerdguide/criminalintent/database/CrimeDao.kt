package sk.lubostar.bignerdguide.criminalintent.database

import androidx.room.Dao
import androidx.room.Query
import sk.lubostar.bignerdguide.criminalintent.Crime
import java.util.*

@Dao interface CrimeDao {

    @Query("SELECT * FROM crime")
    fun getCrimes(): List<Crime>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): Crime?
}