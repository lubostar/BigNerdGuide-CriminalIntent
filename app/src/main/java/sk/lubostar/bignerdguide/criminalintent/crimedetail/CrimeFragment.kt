package sk.lubostar.bignerdguide.criminalintent.crimedetail

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.fragment_crime.*
import sk.lubostar.bignerdguide.criminalintent.Crime
import sk.lubostar.bignerdguide.criminalintent.DatePickerFragment
import sk.lubostar.bignerdguide.criminalintent.R
import sk.lubostar.bignerdguide.criminalintent.TimePickerFragment
import sk.lubostar.bignerdguide.criminalintent.utils.TextDateFormat
import sk.lubostar.bignerdguide.criminalintent.utils.getScaledBitmap
import java.io.File
import java.util.*

class CrimeFragment: Fragment(), DatePickerFragment.Callbacks, TimePickerFragment.Callbacks {
    companion object{
        private const val DATE_FORMAT = "EEE, MMM, dd"

        private const val DIALOG_DATE_TAG = "dialog_date_tag"
        private const val DIALOG_TIME_TAG = "dialog_time_tag"

        private const val REQUEST_DATE = 0
        private const val REQUEST_TIME = 1
        private const val REQUEST_CONTACT = 2
        private const val REQUEST_PHOTO = 3

        private const val ARG_CRIME_ID = "arg_crime_id"

        fun newInstance(uuid: UUID) = CrimeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CRIME_ID, uuid)
                }}
    }

    private val dateFormat = TextDateFormat.getDateInstance(TextDateFormat.FULL)

    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels()

    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.fragment_crime, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val packageManager: PackageManager = requireActivity().packageManager

        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner, { crime ->
            crime?.let {
                this.crime = crime
                photoFile = crimeDetailViewModel.getPhotoFile(crime)
                photoUri = FileProvider.getUriForFile(requireActivity(),
                    "sk.lubostar.bignerdguide.criminalintent.fileprovider", photoFile)
                updateUi() }
        })

        crime_report.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        crime_suspect.apply {
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            if(!existDefaultAppForIntent(packageManager, pickContactIntent)){
                isEnabled = false
            }else{
                setOnClickListener { startActivityForResult(pickContactIntent, REQUEST_CONTACT) }
            }
        }

        crime_camera.apply {
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(!existDefaultAppForIntent(packageManager, captureImage)){
                isEnabled = false
            }else{
                setOnClickListener {
                    captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                    val cameraActivities: List<ResolveInfo> = packageManager
                        .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

                    for(cameraActivity in cameraActivities){
                        requireActivity().grantUriPermission(
                            cameraActivity.activityInfo.packageName, photoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    }

                    startActivityForResult(captureImage, REQUEST_PHOTO)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int,
                after: Int) {}

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int,
                count: Int) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {}
        }

        crime_title.addTextChangedListener(titleWatcher)
        crime_solved.setOnCheckedChangeListener{ _, isChecked -> crime.isSolved = isChecked }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_PHOTO -> {
                updatePhotoView()
                requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }

            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                // Specify which fields you want your query to return values for
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                // Perform your query - the contactUri is like a "where" clause here
                val cursor = requireActivity().contentResolver.query(contactUri!!,
                    queryFields, null, null, null)
                cursor?.use {
                    // Verify cursor contains at least one result
                    if (it.count == 0) {
                        return
                    }

                    // Pull out the first column of the first row of data -
                    // that is your suspect's name
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime)
                    crime_suspect.text = suspect
                }
            }
        }
    }

    private fun existDefaultAppForIntent(packageManager: PackageManager, intent: Intent) =
        packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null

    private fun updatePhotoView(){
        if(photoFile.exists()){
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            crime_photo.setImageBitmap(bitmap)
        }else{
            crime_photo.setImageDrawable(null)
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    private fun updateUi(){
        updatePhotoView()

        crime_title.setText(crime.title)
        crime_solved.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        crime_date.apply {
            text = dateFormat.format(crime.date)
            setOnClickListener {
                DatePickerFragment.newInstance(crime.date).apply {
                    setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                    show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE_TAG)
                }
            }
        }
        crime_time.apply {
            text = if(crime.hour == null) "N/A" else "${crime.hour} : ${crime.minute}"
            setOnClickListener {
                TimePickerFragment.newInstance(crime.hour, crime.minute).apply {
                    setTargetFragment(this@CrimeFragment, REQUEST_TIME)
                    show(this@CrimeFragment.parentFragmentManager, DIALOG_TIME_TAG)
                }
            }
        }
        if(crime.suspect.isNotEmpty()){
            crime_suspect.text = crime.suspect
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUi()
    }

    override fun onTimeSelected(hour: Int, minute: Int) {
        crime.hour = hour
        crime.minute = minute
        updateUi()
    }
}