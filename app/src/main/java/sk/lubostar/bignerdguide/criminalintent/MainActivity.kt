package sk.lubostar.bignerdguide.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, CrimeListFragment.newInstance())
                .commit()
        }
    }

    override fun onCrimeSelected(crimeId: UUID) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, CrimeFragment.newInstance(crimeId))
            .addToBackStack(null)
            .commit()
    }
}