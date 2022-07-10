package com.example.swinclubeventmanagementapplication

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.example.swinclubeventmanagementapplication.SendNotificationPack.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DisplayEventActivity : AppCompatActivity(),
    View.OnClickListener,
    FirebaseDBHelper.itemOnClickListener {
    private lateinit var event: Event
    private lateinit var textViewEventCancelled: TextView
    private lateinit var imageViewEventPoster: ImageView
    private lateinit var textViewEventTitle: TextView
    private lateinit var textViewEventParticipationFee: TextView
    private lateinit var textViewEventActivitiesDetail: TextView
    private lateinit var textViewEventStartDate: TextView
    private lateinit var textViewEventType: TextView
    private lateinit var textViewEventLoc: TextView
    private lateinit var textViewEventDescriptionDetails: TextView
    private lateinit var buttonUpdateEvent: Button
    private lateinit var firebaseDbHelper: FirebaseDBHelper
    private var isCommittee: Boolean = false
    private lateinit var updatedEvent:Event
    private lateinit var chosenAudienceList: ArrayList<String>
    private lateinit var buttonCancelEvent: Button
    private lateinit var currPrompt: String

    private lateinit var student: Student
    private lateinit var currStdID: String


    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val intent = result.data
            updatedEvent = intent?.getParcelableExtra("event")!!
            getAudienceList()
            if(updatedEvent.Audience == "All Students"){
                updatedEvent.Audience = "Swinburne Green Club"
            }
            firebaseDbHelper.updateEvent(updatedEvent)

            Toast.makeText(this, "Event has been updated!", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_event)
        initUI()
        firebaseDbHelper.readProfile(currStdID)



    }


    private fun isCurrStdCommitteeOfCurrClub(): Boolean {
        for(stdCRole in student.Role!!){
            var a = (event.Audience.toString()).replace(" ","")
            println(event.Audience.toString())
            println(a)
            println(stdCRole.clubID)
            if(stdCRole.clubID.replace(" ","") == a){
                return true
            }
        }
        return false
    }

    private fun sendNotification(usertoken:String,title: String,message: String){
        var apiService: APIService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)

        var data= Data(title,message)
        var sender: NotificationSender = NotificationSender(data,usertoken)
        println("-------------------------------------------------send liaooo")
        apiService.sendNotifcation(sender)!!.enqueue(object : Callback<MyResponse?> {
            override fun onResponse(call: Call<MyResponse?>, response: Response<MyResponse?>) {
                if (response.code() === 200) {
                    if (response.body()!!.success !== 1) {
                        Toast.makeText(this@DisplayEventActivity, "Failed ", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<MyResponse?>, t: Throwable?) {
                Toast.makeText(this@DisplayEventActivity, "Something failure ", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun sendNotification(prompt:String){
        var firebaseDatabase = FirebaseDatabase.getInstance().getReference("Students")

        println(chosenAudienceList.size)
        for(s in chosenAudienceList){
            firebaseDatabase.child(s).child("token").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var usertoken:String=dataSnapshot.getValue(String::class.java).toString()
                    sendNotification(usertoken, "${prompt}${updatedEvent.EventTitle.toString()}",updatedEvent.EventActivities.toString())

                    storeIntoNotificationDB(s, "event", updatedEvent.EventTitle.toString(), updatedEvent.EventActivities.toString())
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
    }
    private fun storeIntoNotificationDB(stdID:String, notifType:String, title:String, content:String){
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())
        firebaseDbHelper.addNotification(
            stdID,
            StudentNotification(title,content,"event",currentDate))
    }

    private fun getAudienceList(){
        if(this::updatedEvent.isInitialized == false){
            updatedEvent = event
        }
        if(updatedEvent.Audience != "All Students"){
            firebaseDbHelper.getAudienceList(updatedEvent.Audience!!)
        }else{
            firebaseDbHelper.getAllStudent()
        }
    }


    private fun initUI() {
        firebaseDbHelper = FirebaseDBHelper(this,null,null,this)
        imageViewEventPoster = findViewById(R.id.imageViewEventPoster)
        textViewEventTitle = findViewById(R.id.textViewEventTitle)
        textViewEventParticipationFee = findViewById(R.id.textViewEventParticipationFee)
        textViewEventActivitiesDetail = findViewById(R.id.textViewEventActivitiesDetail)
        textViewEventStartDate = findViewById(R.id.textViewEventStartDate)
        textViewEventType = findViewById(R.id.textViewEventType)
        textViewEventLoc = findViewById(R.id.textViewEventLoc)
        textViewEventDescriptionDetails = findViewById(R.id.textViewEventDescriptionDetails)
        buttonUpdateEvent = findViewById(R.id.buttonUpdateEvent)
        buttonCancelEvent = findViewById(R.id.buttonCancelEvent)
        textViewEventCancelled = findViewById(R.id.textViewEventCancelled)


        currStdID =
            Regex("^([^@]+)").find(FirebaseAuth.getInstance().currentUser!!.email!!)?.value.toString()
        firebaseDbHelper.isRoleExist(currStdID)
        buttonUpdateEvent.setOnClickListener(this)
        buttonCancelEvent.setOnClickListener(this)
    }
    private fun updateContent(){

        Glide.with(applicationContext)
            .load(event.EventPosterIMG)
            .into(imageViewEventPoster)

        if(event.Status == "cancelled"){
            textViewEventCancelled.visibility = View.VISIBLE
            buttonUpdateEvent.visibility = View.GONE
            buttonCancelEvent.visibility = View.GONE
        }

        textViewEventTitle.text = event.EventTitle
        textViewEventParticipationFee.text = event.EventParticipationFee
        textViewEventActivitiesDetail.text = event.EventActivities
        textViewEventStartDate.text = "${event.EventDate} ${event.EventTime}"
        textViewEventType.text = event.EventType
        textViewEventLoc.text = event.EventLoc
        textViewEventDescriptionDetails.text = event.EventDescription

    }



    override fun onClick(p0: View?) {
        when (p0?.id){
            R.id.buttonUpdateEvent -> updateEvent()
            R.id.buttonCancelEvent -> cancelEvent()
        }
    }

    private fun updateEvent(){
        currPrompt = "Event updated: "
        val intent: Intent = Intent(this,AddEventActivity::class.java)
        intent.putExtra("event", event)
        resultLauncher.launch(intent)

    }

    private fun cancelEvent(){

        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage("Are you sure you want to delete the event? A notification will be sent to inform the audience")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                currPrompt = "Event cancelled: "
                firebaseDbHelper.cancelEvent(event)
                getAudienceList()
                finish()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("Cancel Event")
        alert.requestWindowFeature(Window.FEATURE_LEFT_ICON)
        alert.setFeatureDrawableResource(
            Window.FEATURE_LEFT_ICON,
            R.drawable.ic_baseline_warning_24
        )
        alert.show()
    }

    override fun passData(std: Student) {
        student = std

        if (getIntent().hasExtra("eventTitle")) {

            val intent = getIntent()
            var eventTitle = intent?.getStringExtra("eventTitle")!!
            eventTitle = eventTitle.replace("Event cancelled: ","")
            eventTitle = eventTitle.replace("Event updated: ","")
            Log.e("eventTitle",eventTitle);
            firebaseDbHelper.readEvent(eventTitle)

        }else{
            val intent = getIntent()
            event = intent?.getParcelableExtra<Event>("event")!!
            Log.e("eventTitle","ioefbiowebfg")
            if(!isCurrStdCommitteeOfCurrClub()){
                buttonUpdateEvent.visibility = View.GONE
                buttonCancelEvent.visibility = View.GONE
            }else{
                buttonUpdateEvent.visibility = View.VISIBLE
                buttonCancelEvent.visibility = View.VISIBLE
            }
            updateContent()
        }
    }

    override fun passSimplifiedClubs(clubs: ArrayList<SimplifiedClub>){}
    override fun passAllNotification(notifsList: ArrayList<StudentNotification>){}

    override fun isCommitteMember(ya: Boolean) {
        isCommittee = ya
        if(!isCommittee){
            buttonUpdateEvent.visibility = View.GONE
            buttonCancelEvent.visibility = View.GONE
        }else{
            buttonUpdateEvent.visibility = View.VISIBLE
            buttonCancelEvent.visibility = View.VISIBLE
        }
    }

    override fun passMemberListing(memberListing: ArrayList<String>) {
        chosenAudienceList = memberListing
        sendNotification(currPrompt)
    }

    override fun passEventListing(eventListing: ArrayList<Event>){
        event = eventListing.first()
        if(!isCurrStdCommitteeOfCurrClub()){
            buttonUpdateEvent.visibility = View.GONE
            buttonCancelEvent.visibility = View.GONE
        }else{
            buttonUpdateEvent.visibility = View.VISIBLE
            buttonCancelEvent.visibility = View.VISIBLE
        }
        updateContent()
    }

    override fun passAllClubs(sClubs: ArrayList<Club>){}
}
