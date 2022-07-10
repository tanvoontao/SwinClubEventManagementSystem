package com.example.swinclubeventmanagementapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class AddEventActivity :
    AppCompatActivity(), View.OnClickListener,
    FirebaseDBHelper.itemOnClickListener  {

    private lateinit var firebaseDbHelper: FirebaseDBHelper
    private lateinit var database: DatabaseReference

    private lateinit var idEdtEventTitle: TextInputEditText
    private lateinit var idEdtEventActivities: TextInputEditText
    private lateinit var idEdtEventLoc: TextInputEditText
    private lateinit var editTextEventDate: TextView
    private lateinit var editTextEventTime: TextView
    private lateinit var idEdtEventPosterIMG: TextInputEditText
    private lateinit var textViewParticipationFee: TextView
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchOnlineInPerson: Switch
    private lateinit var idEdtEventDescription: TextInputEditText
    private lateinit var actionBar: ActionBar
    private lateinit var imageButton_decrease: ImageButton
    private lateinit var imageButton_increase: ImageButton
    private lateinit var addEventButton: Button
    private lateinit var audience: String
    private var participationFee:Int = 0
    private lateinit var currStdID: String

    private lateinit var editTextAudience:TextView
    private lateinit var sClubs:ArrayList<SimplifiedClub>
    private lateinit var student: Student
    private lateinit var event: Event

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val intent = result.data
            audience = intent?.getStringExtra("audience")!!
            editTextAudience.text = "To: $audience"
//            dbHelper.insert(item)
//            Toast.makeText(this, "Item has been added!", Toast.LENGTH_LONG).show()
//            home.UpdateLayout()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        if (getIntent().hasExtra("event")) {
            val intent = getIntent()
            event = intent?.getParcelableExtra<Event>("event")!!
            getSupportActionBar()?.setTitle("Edit Event");
        }else{
            getSupportActionBar()?.setTitle("Add Event");
        }

        initUI()
        firebaseDbHelper.readProfile(currStdID)
        firebaseDbHelper.getSimplifiedClubData()

        if (getIntent().hasExtra("event")) {
            updateContent()
            addEventButton.setText("Update Event")
        }
    }

    private fun updateContent() {

        idEdtEventTitle.setText(event.EventTitle.toString())
        idEdtEventActivities.setText(event.EventActivities.toString())
        idEdtEventLoc.setText(event.EventLoc.toString())
        editTextEventDate.setText(event.EventDate.toString())
        editTextEventTime.setText(event.EventTime.toString())
        idEdtEventPosterIMG.setText(event.EventPosterIMG.toString())
        textViewParticipationFee.setText(event.EventParticipationFee.toString())

        switchOnlineInPerson.isChecked = (event.EventType.toString() == "In Person")

        idEdtEventDescription.setText(event.EventDescription.toString())
        editTextAudience.setText(event.Audience.toString())
    }

    private fun initUI() {
        currStdID =
            Regex("^([^@]+)").find(FirebaseAuth.getInstance().currentUser!!.email!!)?.value.toString()
        database = FirebaseDatabase.getInstance().getReference("Students")
        firebaseDbHelper = FirebaseDBHelper(this,null,database,this)
        sClubs = ArrayList<SimplifiedClub>()
//        firebaseDbHelper = FirebaseDBHelper(requireContext(),null,database,this)
//        database = FirebaseDatabase.getInstance().getReference("Students")

        idEdtEventTitle = findViewById(R.id.idEdtEventTitle)
        idEdtEventActivities = findViewById(R.id.idEdtEventActivities)
        idEdtEventLoc = findViewById(R.id.idEdtEventLoc)
        editTextEventDate = findViewById(R.id.editTextEventDate)
        editTextEventTime = findViewById(R.id.editTextEventTime)
        idEdtEventPosterIMG = findViewById(R.id.idEdtEventPosterIMG)
        textViewParticipationFee = findViewById(R.id.textViewParticipationFee)
        switchOnlineInPerson = findViewById(R.id.switchOnlineInPerson)
        idEdtEventDescription = findViewById(R.id.idEdtEventDescription)
        editTextAudience = findViewById(R.id.editTextAudience)

        imageButton_decrease = findViewById(R.id.imageButton_decrease)
        imageButton_increase = findViewById(R.id.imageButton_increase)
        addEventButton = findViewById(R.id.addEventButton)

        if(getIntent().hasExtra("event")) {
            idEdtEventTitle.setFocusable(false)
        }

        imageButton_decrease.setOnClickListener(this)
        imageButton_increase.setOnClickListener(this)
        addEventButton.setOnClickListener(this)
        editTextEventDate.setOnClickListener(this)
        editTextEventTime.setOnClickListener(this)
        editTextAudience.setOnClickListener(this)




        switchOnlineInPerson.setOnCheckedChangeListener(
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    switchOnlineInPerson.text = "In Person"
                }else{
                    switchOnlineInPerson.text = "Online"
                }
        })
    }

    override fun onClick(view: View?) {
        when (view?.id){
            R.id.editTextEventDate -> pickDate()
            R.id.editTextEventTime -> pickTime()
            R.id.imageButton_increase -> increaseFee()
            R.id.imageButton_decrease -> decreaseFee()
            R.id.addEventButton -> addEvent()
            R.id.editTextAudience -> chooseAudience()
        }
        textViewParticipationFee.setText(participationFee.toString())
    }

    private fun chooseAudience() {
        var audiences = getAudience()
        val intent: Intent = Intent(this,ChooseAudienceActivity::class.java)
        intent.putExtra("audience", audiences)
        resultLauncher.launch(intent)
    }

    private fun addEvent() {
        if(isEventTitleOk()){
            println("OK")
            val event = createEvent()
            resetForm()
            val intent = Intent()
            intent.putExtra("event", event)
            setResult(Activity.RESULT_OK, intent)
            finish() // redirect back to profile activity
        }
    }


    private fun isEventTitleOk():Boolean{
        return if (idEdtEventTitle.length() == 0){
            idEdtEventTitle.setError("Please enter the event name")
            false
        }else{
            true
        }
    }

    private fun decreaseFee() {
        if(participationFee > 0){
            participationFee--
        }
    }

    private fun increaseFee() {
        participationFee++
    }

    private fun pickTime() {
        // Get Current Time
        val calendar: Calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        var minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener
        {view, hour, minute ->
            var time1 = "$hour:$minute"
            if(hour.toString().length == 1) {
                time1 = "0"+"$hour:$minute"
            }
            if(minute.toString().length == 1 && minute != 0) {
                time1 = "$hour:0$minute"
            }
            if(minute.toString().length == 1 && minute != 0 && hour.toString().length == 1) {
                time1 = "0" + "$hour:0$minute"
            }
            if(minute == 0) {
                time1 += "0"
            }

            val time2: LocalTime = LocalTime.parse(time1)
            val df: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
            val time3: String = time2.format(df)

            editTextEventTime.setText(time3)
        }, hour, minute, false
        )
        timePickerDialog.show()
    }

    private fun pickDate() {
        // set the curr date as the default date
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener
        {view, year, month, day ->
            val date = year.toString() + "-" + (month + 1).toString() + "-" + day.toString()
            editTextEventDate.setText( date )
        }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun resetForm() {
        editTextAudience.text = "To: ..."
        idEdtEventActivities.text!!.clear()
        editTextEventDate.text = "Enter Event Date"
        idEdtEventDescription.text!!.clear()
        idEdtEventLoc.text!!.clear()
        textViewParticipationFee.text = "0"
        idEdtEventPosterIMG.text!!.clear()
        editTextEventTime.text = "Enter Event Time"
        idEdtEventTitle.text!!.clear()
        switchOnlineInPerson.isChecked = false
    }

    private fun createEvent():Event? {
        var event:Event? = null
        var a = editTextAudience.text.toString().replace("To: ","")
        if (getIntent().hasExtra("event")) {
            event = Event(
                a,
                idEdtEventActivities.text.toString(),
                editTextEventDate.text.toString(),
                idEdtEventDescription.text.toString(),
                idEdtEventLoc.text.toString(),
                textViewParticipationFee.text.toString(),
                idEdtEventPosterIMG.text.toString(),
                editTextEventTime.text.toString(),
                idEdtEventTitle.text.toString(),
                switchOnlineInPerson.text.toString(),
                "Up Coming"
            )
        }else{
            event = Event(
                audience,
                idEdtEventActivities.text.toString(),
                editTextEventDate.text.toString(),
                idEdtEventDescription.text.toString(),
                idEdtEventLoc.text.toString(),
                textViewParticipationFee.text.toString(),
                idEdtEventPosterIMG.text.toString(),
                editTextEventTime.text.toString(),
                idEdtEventTitle.text.toString(),
                switchOnlineInPerson.text.toString(),
                "Up Coming"
            )
        }

        return event
    }

    override fun passData(std: Student) {
        student = std
    }

    override fun passSimplifiedClubs(clubs: ArrayList<SimplifiedClub>) {
        sClubs = clubs
    }

    override fun isCommitteMember(ya: Boolean){}
    override fun passMemberListing(memberListing: ArrayList<String>){}
    override fun passEventListing(eventListing: ArrayList<Event>){}
    override fun passAllClubs(sClubs: ArrayList<Club>) {}
    override fun passAllNotification(notifsList: ArrayList<StudentNotification>){}


    private fun getAudience(): ArrayList<SimplifiedClub> {
        var audiences = ArrayList<SimplifiedClub>()
        audiences.add(SimplifiedClub("All Students", "https://www.eschoolnews.com/files/2019/02/student-belonging.jpg"))
        for (c in sClubs){
            for(stdCRole in student.Role!!){
                println(stdCRole.clubID)
                println(c.clubName)
                if(stdCRole.clubID == c.clubName){
                    audiences.add(c)
                }
            }
        }
        return audiences
    }


}