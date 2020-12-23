package sk.lubostar.bignerdguide.criminalintent.crimelist

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_crime_list.*
import sk.lubostar.bignerdguide.criminalintent.Crime
import sk.lubostar.bignerdguide.criminalintent.R
import java.text.DateFormat
import java.util.*

class CrimeListFragment: Fragment() {
    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    /**
     * Required interface for hosting activities
     */
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private val viewModel: CrimeListViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_crime_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crime_recycler_view.layoutManager = LinearLayoutManager(context)
        crime_recycler_view.adapter = CrimeAdapter(emptyList())

        viewModel.crimeListLiveData.observe(viewLifecycleOwner, { crimes ->
            crime_recycler_view.adapter = CrimeAdapter(crimes)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.new_crime -> with(Crime()){
                    viewModel.addCrime(this)
                    callbacks?.onCrimeSelected(id)
                    true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
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
                callbacks?.onCrimeSelected(crime.id)
            }
        }
    }

    private inner class CrimeAdapter(var crimes: List<Crime>)
        : RecyclerView.Adapter<CrimeViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            CrimeViewHolder(layoutInflater.inflate(R.layout.list_item_crime, parent, false))

        override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) =
            holder.bind(crimes[position])

        override fun getItemCount() = crimes.size
    }
}