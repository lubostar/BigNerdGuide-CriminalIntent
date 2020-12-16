package sk.lubostar.bignerdguide.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_crime_list.*
import java.text.DateFormat

class CrimeListFragment: Fragment() {
    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    private val viewModel: CrimeListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    = inflater.inflate(R.layout.fragment_crime_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crime_recycler_view.layoutManager = LinearLayoutManager(context)
        crime_recycler_view.adapter = CrimeAdapter(viewModel.crimes)
    }

    private inner class CrimeViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val dateFormat = DateFormat.getDateInstance(DateFormat.FULL)
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val crimeSolved: ImageView = itemView.findViewById(R.id.crime_solved)

        fun bind(crime: Crime){
            titleTextView.text = crime.title
            dateTextView.text = dateFormat.format(crime.date)
            crimeSolved.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private inner class CrimeAdapter(var crimes: List<Crime>)
        : RecyclerView.Adapter<CrimeViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        = CrimeViewHolder(layoutInflater.inflate(R.layout.list_item_crime, parent, false))

        override fun onBindViewHolder(holder: CrimeViewHolder, position: Int)
        = holder.bind(crimes[position])

        override fun getItemCount() = crimes.size
    }
}