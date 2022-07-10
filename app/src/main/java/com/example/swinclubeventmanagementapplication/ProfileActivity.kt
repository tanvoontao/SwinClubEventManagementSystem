package com.example.swinclubeventmanagementapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.example.swinclubeventmanagementapplication.SendNotificationPack.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ProfileActivity :
    AppCompatActivity(),
    View.OnClickListener,
    FirebaseDBHelper.itemOnClickListener{
//
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val intent = result.data
            createdEvent = intent?.getParcelableExtra("event")!!
            if(createdEvent.Audience != "All Students"){
                firebaseDbHelper.getAudienceList(createdEvent.Audience!!)
            }else{
                firebaseDbHelper.getAllStudent()
            }

            if(createdEvent.Audience == "All Students"){
                createdEvent.Audience = "Swinburne Green Club"
            }
            println(createdEvent.Audience)
            addEventIntoDatabase(createdEvent)
            Toast.makeText(this, "Event has been added!", Toast.LENGTH_LONG).show()
            // after add event -> push to chosen audiences

        }
    }

    // -- UI elements -- //
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var actionBar: ActionBar
    private lateinit var toggle:ActionBarDrawerToggle
    private lateinit var addEventIcon:FloatingActionButton
    private lateinit var imageViewBadge: ImageView
    private lateinit var profile_image: ImageView
    private lateinit var studentName: TextView
    private lateinit var studentEmail: TextView

    // --------------- --------------- //
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navBottomView : BottomNavigationView
    private lateinit var navController : NavController
    // --------------- --------------- //

    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var apiService: APIService
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var firebaseDbHelper: FirebaseDBHelper

    private lateinit var student: Student
    private lateinit var currStdID: String
    private lateinit var user: String
    private lateinit var createdEvent: Event
    private lateinit var chosenAudienceList: ArrayList<String>

    // -- const val -- //
    private val COMMITTEE_MEMBER = "CommitteeMember"
    private val STUDENT = "Student"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val intent = getIntent()
        // get user from loginSignUpActivity -> decide display what kind of menu
        user = intent?.getStringExtra("user").toString()

        initUI()
        checkUser()
        UpdateToken()
        firebaseDbHelper.readProfile(currStdID)
    }

    private fun addEventIntoDatabase(e: Event) {
        firebaseDbHelper.addEvent(e)
    }

    private fun initUI() {
        // -- UI elements -- //
        addEventIcon = findViewById(R.id.fab)
        actionBar = supportActionBar!!
        actionBar.title = "Profile"
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navBottomView = findViewById(R.id.bottom_navigation_view)
        imageViewBadge = findViewById(R.id.imageViewBadge)


        currStdID = Regex("^([^@]+)")
            .find(
                FirebaseAuth.getInstance().currentUser!!.email!!
            )?.value.toString()
        firebaseDbHelper = FirebaseDBHelper(this,null,null,this)

        if(user.equals(STUDENT)){
            navBottomView.menu.clear()
            navBottomView.inflateMenu(R.menu.student_bottom_nav_menu);
            addEventIcon.visibility = View.GONE
            navView.menu.clear()
            navView.inflateMenu(R.menu.slide_menu_nav_student)
            imageViewBadge.visibility = View.GONE
        }

        val headerView = navView.getHeaderView(0)
        profile_image = headerView.findViewById(R.id.profile_image) as CircleImageView
        studentName = headerView.findViewById(R.id.studentName) as TextView
        studentEmail = headerView.findViewById(R.id.studentEmail) as TextView

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.bottomNavHome,R.id.bottomNavClub,R.id.bottomNavUpcomingEvent,R.id.bottomNavNotification), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navBottomView.setupWithNavController(navController)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open, R.string.close)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setupWithNavController(navController)

        navBottomView.background = null
        if(!user.equals(STUDENT)){
            navBottomView.menu.getItem(2).isEnabled = false
        }

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Students")
        firebaseAuth = FirebaseAuth.getInstance()

        // -- UI elements click listener -- //
        addEventIcon.setOnClickListener(this)
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            // user not log in, redirect to login page
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun UpdateToken(){
        var currUserEmail: String = FirebaseAuth.getInstance().currentUser!!.email!!
        val regex = Regex("^([^@]+)")
        val stdID = regex.find(currUserEmail)?.value.toString()
        var refreshToken = ""
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    println("Fetching FCM registration token failed" + task.exception)
                    return@OnCompleteListener
                }
                refreshToken = task.result // Get new FCM registration token
                var token: Token =Token(refreshToken)

                // save to database
                val student = mapOf<String,String>(
                    "token" to token.token.toString()
                )
                firebaseDatabase.child(stdID).updateChildren(student).addOnSuccessListener {
                    Toast.makeText(this,"Successfully Updated",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this,"Failed to Update",Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun sendNotification(usertoken:String,title: String,message: String){
        var data= Data(title,message)
        var sender: NotificationSender = NotificationSender(data,usertoken)

        apiService.sendNotifcation(sender)!!.enqueue(object : Callback<MyResponse?> {
            override fun onResponse(call: Call<MyResponse?>, response: Response<MyResponse?>) {
                if (response.code() === 200) {
                    if (response.body()!!.success !== 1) {
                        Toast.makeText(this@ProfileActivity, "Failed ", Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(call: Call<MyResponse?>, t: Throwable?) {
                Toast.makeText(this@ProfileActivity, "Something failure ", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun sendNotification(){
        println(chosenAudienceList.size)
        for(s in chosenAudienceList){
            firebaseDatabase.child(s).child("token").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var usertoken:String=dataSnapshot.getValue(String::class.java).toString()
                    sendNotification(usertoken, createdEvent.EventTitle.toString(),createdEvent.EventActivities.toString())
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val currentDate = sdf.format(Date())
                    firebaseDbHelper.addNotification(
                        currStdID,
                        StudentNotification(createdEvent.EventTitle.toString(),createdEvent.EventActivities.toString(),"event",currentDate))
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
    }
    private fun updateProfile(){
        Glide.with(this)
            .load(student.ProfileIMG)
            .into(profile_image)
        studentName.text = student.Name
        studentEmail.text = student.StudentEmail
    }
    private fun CreateNewEvent() {
        val intent: Intent = Intent(this,AddEventActivity::class.java)
        resultLauncher.launch(intent)
    }

// ------------------------------override method--------------------------------------------- //

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onClick(view: View?) {
        when (view?.id){
            R.id.fab -> CreateNewEvent()
        }
    }
    override fun passData(std: Student) {
        student = std
        updateProfile()
    }
    override fun passMemberListing(memberListing: ArrayList<String>) {
        chosenAudienceList = memberListing
        sendNotification()
    }
    override fun isCommitteMember(ya: Boolean){}
    override fun passSimplifiedClubs(clubs: ArrayList<SimplifiedClub>){}
    override fun passEventListing(eventListing: ArrayList<Event>){}
    override fun passAllClubs(sClubs: ArrayList<Club>){}
    override fun passAllNotification(notifsList: ArrayList<StudentNotification>){}
}