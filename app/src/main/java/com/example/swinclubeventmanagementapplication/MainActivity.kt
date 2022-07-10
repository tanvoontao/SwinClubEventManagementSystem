package com.example.swinclubeventmanagementapplication

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.example.swinclubeventmanagementapplication.databinding.ActivityMainBinding
import com.google.firebase.database.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class MainActivity :
    AppCompatActivity(),
    View.OnClickListener{

    // -- UI elements -- //
    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBar: ActionBar

    private lateinit var database: DatabaseReference

    // -- Notification Channel Val -- //
    private val CHANNEL_ID = "my_channel_01"
    private val name: CharSequence = "my_channel"
    private val descriptionText = "This is my channel"
    private val importance = NotificationManager.IMPORTANCE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        createNotificationChannelForApp()


        //studentJoinClub()

//        readData()
//        addData()
//        updateData()
//        deleteData()

    }

    fun studentJoinClub(){
        //stdId:String, clubId:String
        var stdId = "101234692"
        var clubId = "SwinburneComputerScienceClub"

        clubId = clubId.replace(" ","")

        val clubsDb = FirebaseDatabase.getInstance().getReference("Clubs")
        val studentsDb = FirebaseDatabase.getInstance().getReference("Students")

        clubsDb.child(clubId).child("ClubMembers").child(stdId).setValue(true).addOnSuccessListener {
            Toast.makeText(this,"Successfully Saved",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
        }

        studentsDb.child(stdId).child("joinedClubs").child(clubId).setValue(true).addOnSuccessListener {
            Toast.makeText(this,"Successfully Saved",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
        }

    }


    private fun initUI(){
        binding.buttonCommitteeMember.setOnClickListener(this)
        binding.buttonStudent.setOnClickListener(this)
        actionBar = supportActionBar!!
        actionBar.title = "Login As ..."
    }

    private fun createNotificationChannelForApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance) // Create the NotificationChannel
            mChannel.description = descriptionText

            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun loginAs(user: String) {
        var login_signUp = "login"
        val intent: Intent = Intent(this,LoginSignUpActivity::class.java)
        intent.putExtra("user", user)
        intent.putExtra("login_signUp", login_signUp)
        startActivity(intent)
    }

    private fun deleteData() {
        var StudentID: String? = "101234666"

        database.child(StudentID!!).removeValue().addOnSuccessListener {

            Toast.makeText(this,"Successfuly del",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener{
            Toast.makeText(this,"Failed to del",Toast.LENGTH_SHORT).show()
        }
    }

    private fun readStudent(){
        println("-----------------------------------------------------")
        var stdEmail = "101234693@students.swinburne.edu.my"
//        var postReference = Firebase.database.reference

        var postReference = FirebaseDatabase.getInstance().getReference("Students")
        postReference = postReference.child("Students").child("StudentEmail").equalTo(stdEmail) as DatabaseReference

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val username = ds.child("Name").getValue(String::class.java)
                    println(username)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                println("loadPost:onCancelled" + databaseError.toException())
            }
        }
        postReference.addValueEventListener(postListener)
    }

    private fun addData(){
        var StudentID: String? = "101234666"
        var StudentEmail: String? = "test"
        var Gender: String? = "test"
        var Email: String? = "test"
        var Dob: String? = "test"
        var Password: String? = "test"
        var ContactNo: String? = "test"
        var Name: String? = "test"
        var ProfileIMG: String? = "test"
        var EnrollmentDate: String? = "test"
        var JoinedClubs: ArrayList<JoinedClub>? = null
        var Role: ArrayList<Role>? = null
        val User = Student(StudentID,StudentEmail,Gender,Email,Dob,Password,ContactNo,Name,ProfileIMG,EnrollmentDate,JoinedClubs,Role)
        database.child(StudentID!!).setValue(User).addOnSuccessListener {

            Toast.makeText(this,"Successfully Saved",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener{
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
        }
    }

    private fun readData() {
        var database = FirebaseDatabase.getInstance().getReference("Students")
        var userName = "101234691"
        println("-----------------------------------a")
        database.child(userName).get().addOnSuccessListener {

            if (it.exists()){

                val firstname = it.child("Name").value
                val t = it.child("Role").value

                println("-----------------------------------")
                println(firstname)
            }else{
                Toast.makeText(this,"User Doesn't Exist",Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener{
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateData(){
        var contactNo = "1111"
        var StudentID: String? = "101234666"

        val user = mapOf<String,String>(
            "contactNo" to contactNo
        )

        database.child(StudentID!!).updateChildren(user).addOnSuccessListener {

            Toast.makeText(this,"Successfuly Updated",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener{
            Toast.makeText(this,"Failed to Update",Toast.LENGTH_SHORT).show()
        }
    }

// ------------------------------override method--------------------------------------------- //

    override fun onClick(view: View?) {
        when (view?.id){
            R.id.buttonCommitteeMember -> loginAs("CommitteeMember")
            R.id.buttonStudent -> loginAs("Student")
        }
    }
}