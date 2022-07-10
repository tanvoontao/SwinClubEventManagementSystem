package com.example.swinclubeventmanagementapplication.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.swinclubeventmanagementapplication.FirebaseDBHelper
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.example.swinclubeventmanagementapplication.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment :
    Fragment(),
    FirebaseDBHelper.itemOnClickListener{
//    private var param1: String? = null
//    private var param2: String? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
    private lateinit var firebaseDbHelper: FirebaseDBHelper
    private lateinit var database: DatabaseReference
    private lateinit var student: Student
    private lateinit var currStdID: String
    private lateinit var imageViewProfile: ImageView
    private lateinit var textViewStudentName: TextView
    private lateinit var imageViewGenderIcon: ImageView
    private lateinit var textViewStudentEmail: TextView
    private lateinit var textViewDobDetail: TextView
    private lateinit var textViewContactNoDetail: TextView
    private lateinit var textViewGeneralEmailDetail: TextView
    private lateinit var textViewEnrollmentDateDetail: TextView
    private lateinit var textViewAboutRole: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initUI(view)
        firebaseDbHelper.readProfile(currStdID)

        return view
    }

    private fun initUI(view: View) {
        currStdID =
            Regex("^([^@]+)").find(FirebaseAuth.getInstance().currentUser!!.email!!)?.value.toString()
        database = FirebaseDatabase.getInstance().getReference("Students")
        firebaseDbHelper = FirebaseDBHelper(requireContext(),null,database,this)

        imageViewProfile = view.findViewById(R.id.imageViewProfile)
        textViewStudentName = view.findViewById(R.id.textViewStudentName)
        imageViewGenderIcon = view.findViewById(R.id.imageViewGenderIcon)
        textViewStudentEmail = view.findViewById(R.id.textViewStudentEmail)
        textViewDobDetail = view.findViewById(R.id.textViewDobDetail)
        textViewContactNoDetail = view.findViewById(R.id.textViewContactNoDetail)
        textViewGeneralEmailDetail = view.findViewById(R.id.textViewGeneralEmailDetail)
        textViewEnrollmentDateDetail = view.findViewById(R.id.textViewEnrollmentDateDetail)
        textViewAboutRole = view.findViewById(R.id.textViewAboutRole)

    }

    private fun updateProfile(){
        var text = ""
        Glide.with(this)
            .load(student.ProfileIMG)
            .into(imageViewProfile)
        textViewStudentName.text = student.Name

        if(student.Gender.equals("male") || student.Gender.equals("Male")){
            imageViewGenderIcon.setImageResource(R.drawable.male);
        }else{
            imageViewGenderIcon.setImageResource(R.drawable.female);
        }
        if(!student.Role!!.isEmpty() || student.Role != null){
            for(r in student.Role!!){
                val clubName = (r.clubID).replace("(.)([A-Z])".toRegex(), "$1 $2")
                for (p in r.position){
                    text += "$p "
                }
                text += "in $clubName"
            }
        }
        textViewAboutRole.text = text
        textViewStudentEmail.text = student.StudentEmail
        textViewDobDetail.text = student.Dob
        textViewContactNoDetail.text = student.ContactNo
        textViewGeneralEmailDetail.text = student.Email
        textViewEnrollmentDateDetail.text = student.EnrollmentDate
    }

    override fun passData(std: Student) {
        student = std
        updateProfile()
        println(student)
    }
    override fun passSimplifiedClubs(clubs: ArrayList<SimplifiedClub>) {}
    override fun isCommitteMember(ya: Boolean){}
    override fun passMemberListing(memberListing: ArrayList<String>){}
    override fun passEventListing(eventListing: ArrayList<Event>){}
    override fun passAllClubs(sClubs: ArrayList<Club>){}
    override fun passAllNotification(notifsList: ArrayList<StudentNotification>){}

}