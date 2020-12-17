package sk.lubostar.bignerdguide.criminalintent

import android.app.Application

class CriminalIntentApp: Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}