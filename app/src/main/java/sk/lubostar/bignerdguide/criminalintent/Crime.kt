package sk.lubostar.bignerdguide.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity data class Crime(@PrimaryKey val id: UUID = UUID.randomUUID(),
                 var title: String = "",
                 var date: Date = Date(),
                         var hour: Int? = null,
                         var minute: Int? = null,
                 var isSolved: Boolean = false) {
}