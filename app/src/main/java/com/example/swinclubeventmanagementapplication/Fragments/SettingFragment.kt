package com.example.swinclubeventmanagementapplication.Fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.swinclubeventmanagementapplication.FirebaseDBHelper
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.example.swinclubeventmanagementapplication.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class SettingFragment : Fragment(), View.OnClickListener,
    FirebaseDBHelper.itemOnClickListener {
    private lateinit var firebaseDbHelper: FirebaseDBHelper
    private lateinit var currstudent: Student
    private lateinit var currStdID: String
    private lateinit var idEdtStdEmail: TextInputEditText
    private lateinit var idEdtStdName: TextInputEditText
    private lateinit var idEdtStdGEmail: TextInputEditText
    private lateinit var editTextDob: TextView
    private lateinit var idEdtStdProfile: TextInputEditText
    private lateinit var idEdtStdPhoneNo: TextInputEditText
    private lateinit var addEventButton: Button



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        initUI(view)
        firebaseDbHelper.readProfile(currStdID)

        return view
    }

    private fun initUI(view: View) {
        firebaseDbHelper = FirebaseDBHelper(requireContext(),null,null,this)
        currStdID = Regex("^([^@]+)")
            .find(
                FirebaseAuth.getInstance().currentUser!!.email!!
            )?.value.toString()
        idEdtStdEmail = view.findViewById(R.id.idEdtStdEmail)
        idEdtStdName = view.findViewById(R.id.idEdtStdName)
        idEdtStdGEmail = view.findViewById(R.id.idEdtStdGEmail)
        editTextDob = view.findViewById(R.id.editTextDob)
        idEdtStdProfile = view.findViewById(R.id.idEdtStdProfile)
        idEdtStdPhoneNo = view.findViewById(R.id.idEdtStdPhoneNo)
        addEventButton = view.findViewById(R.id.addEventButton)
        addEventButton.setOnClickListener(this)
        editTextDob.setOnClickListener(this)

        idEdtStdEmail.setFocusable(false)
    }
    private fun updateForm(){
        idEdtStdEmail.setText(currstudent.StudentEmail.toString())
        idEdtStdName.setText(currstudent.Name.toString())
        idEdtStdGEmail.setText(currstudent.Email.toString())
        editTextDob.setText(currstudent.Dob.toString())
        idEdtStdProfile.setText(currstudent.ProfileIMG.toString())
        idEdtStdPhoneNo.setText(currstudent.ContactNo.toString())
    }
    private fun updateProfileDetail(){
        var std = Student(
            currStdID,
            idEdtStdEmail.text.toString(),
            null,
            idEdtStdGEmail.text.toString(),
            editTextDob.text.toString(),
            null,
            idEdtStdPhoneNo.text.toString(),
            idEdtStdName.text.toString(),
            idEdtStdProfile.text.toString(),
            null,
            null, null
        )
        firebaseDbHelper.updateProfile(std)
        requireActivity().getFragmentManager().popBackStack();
    }
    private fun pickDate() {
        // set the curr date as the default date
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), DatePickerDialog.OnDateSetListener
        {view, year, month, day ->
            val date = year.toString() + "-" + (month + 1).toString() + "-" + day.toString()
            editTextDob.setText( date )
        }, year, month, day
        )
        datePickerDialog.show()
    }

    override fun passData(std: Student) {
        currstudent = std
        updateForm()
    }

    override fun passSimplifiedClubs(clubs: ArrayList<SimplifiedClub>){}

    override fun isCommitteMember(ya: Boolean){}

    override fun passMemberListing(memberListing: ArrayList<String>){}

    override fun passEventListing(eventListing: ArrayList<Event>){}

    override fun passAllClubs(sClubs: ArrayList<Club>){}

    override fun passAllNotification(notifsList: ArrayList<StudentNotification>){}
    override fun onClick(view: View?) {
        when (view?.id){
            R.id.addEventButton -> updateProfileDetail()
            R.id.editTextDob -> pickDate()
        }
    }

}