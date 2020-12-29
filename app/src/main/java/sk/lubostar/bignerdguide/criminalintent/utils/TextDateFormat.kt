package sk.lubostar.bignerdguide.criminalintent.utils

import java.text.DateFormat

class TextDateFormat {
    companion object{
        const val FULL = DateFormat.FULL

        fun getDateInstance(style: Int): DateFormat = DateFormat.getDateInstance(style)
    }
}