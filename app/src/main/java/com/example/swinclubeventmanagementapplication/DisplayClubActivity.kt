package com.example.swinclubeventmanagementapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.google.firebase.auth.FirebaseAuth

class DisplayClubActivity : AppCompatActivity(),
    View.OnClickListener,
    FirebaseDBHelper.itemOnClickListener {

    // -- UI elements -- //
    private lateinit var imageViewClubLogoIMG: ImageView
    private lateinit var textViewClubName: TextView
    private lateinit var textViewClubAboutDetail: TextView
    private lateinit var textViewCategory: TextView
    private lateinit var textViewSubscriptionMethod: TextView
    private lateinit var textViewMembershipFee: TextView
    private lateinit var textViewClubEmailDetail: TextView
    private lateinit var textViewAdvisor: TextView
    private lateinit var textViewPresident: TextView
    private lateinit var textViewVicePresident: TextView
    private lateinit var textViewSecretary: TextView
    private lateinit var textViewTreasurer: TextView
    private lateinit var textViewSeeEvent: TextView
    private lateinit var buttonJoinClub: Button
    private lateinit var textViewJoinedClub: TextView

    private lateinit var actionBar: ActionBar

    private lateinit var club: Club
    private lateinit var firebaseDbHelper: FirebaseDBHelper
    private lateinit var currClubMemberListing: ArrayList<String>
    private lateinit var currStdID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_club)
        val intent = getIntent()
        club = intent?.getParcelableExtra<Club>("club")!!
        initUI()

        firebaseDbHelper.getAudienceList(getClubID())
    }

    private fun initUI() {
        // -- UI elements -- //
        imageViewClubLogoIMG = findViewById(R.id.imageViewClubLogoIMG)
        textViewClubName = findViewById(R.id.textViewClubName)
        textViewClubAboutDetail = findViewById(R.id.textViewClubAboutDetail)
        textViewCategory = findViewById(R.id.textViewCategory)
        textViewSubscriptionMethod = findViewById(R.id.textViewSubscriptionMethod)
        textViewMembershipFee = findViewById(R.id.textViewMembershipFee)
        textViewClubEmailDetail = findViewById(R.id.textViewClubEmailDetail)
        textViewAdvisor = findViewById(R.id.textViewAdvisor)
        textViewPresident = findViewById(R.id.textViewPresident)
        textViewVicePresident = findViewById(R.id.textViewVicePresident)
        textViewSecretary = findViewById(R.id.textViewSecretary)
        textViewTreasurer = findViewById(R.id.textViewTreasurer)
        textViewSeeEvent = findViewById(R.id.textViewSeeEvent)
        buttonJoinClub = findViewById(R.id.buttonJoinClub)
        textViewJoinedClub = findViewById(R.id.textViewJoinedClub)

        actionBar = supportActionBar!!
        actionBar.title = "Club"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        currStdID = Regex("^([^@]+)")
            .find(
                FirebaseAuth.getInstance().currentUser!!.email!!
            )?.value.toString()

        firebaseDbHelper = FirebaseDBHelper(this,null,null,this)

        // -- UI elements click listener -- //
        textViewSeeEvent.setOnClickListener(this)
        buttonJoinClub.setOnClickListener(this)
    }

    private fun isUserJoinClub():Boolean{
        if(currStdID in currClubMemberListing){
            return true
        }
        return false
    }

    private fun getClubID():String{
        return (club.ClubName)!!.replace(" ","")
    }

    @SuppressLint("SetTextI18n")
    private fun updateContent(){
        Glide.with(this)
            .load(club.ClubLogoIMG)
            .into(imageViewClubLogoIMG)

        if(isUserJoinClub()){
            textViewJoinedClub.visibility = View.VISIBLE
            buttonJoinClub.visibility = View.GONE
        }

        textViewClubName.text = club.ClubName
        textViewClubAboutDetail.text = club.ClubAbout
        textViewCategory.text = club.Category
        textViewSubscriptionMethod.text = club.SubscriptionMethod
        textViewMembershipFee.text = club.MembershipFee
        textViewClubEmailDetail.text = club.ClubEmail
        textViewAdvisor.text = "Advisor: ${club.Advisor}"
        textViewPresident.text = "President: ${club.President}"
        textViewVicePresident.text = "Vice President: ${club.VicePresident}"
        textViewSecretary.text = "Secretary: ${club.Secretary}"
        textViewTreasurer.text = "Treasurer: ${club.Treasurer}"
    }

    private fun showEventActivity(){
        val intent: Intent = Intent(this,ClubEventActivity::class.java)
        intent.putExtra("clubName", club.ClubName)
        startActivity(intent)

    }
    private fun studentJoinClub(){
        firebaseDbHelper.studentJoinClub(currStdID,club.ClubName!!)
        finish()
    }

// ------------------------------override method--------------------------------------------- //

    override fun onClick(view: View?) {
        when (view?.id){
            R.id.textViewSeeEvent -> showEventActivity()
            R.id.buttonJoinClub -> studentJoinClub()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // go back to previous activity, when back button of actionbar clicked
        return super.onSupportNavigateUp()
    }
    override fun passData(std: Student){}
    override fun passSimplifiedClubs(clubs: java.util.ArrayList<SimplifiedClub>){}
    override fun isCommitteMember(ya: Boolean){}
    override fun passMemberListing(memberListing: java.util.ArrayList<String>) {
        currClubMemberListing = memberListing
        updateContent()
    }
    override fun passEventListing(eventListing: java.util.ArrayList<Event>){}
    override fun passAllClubs(sClubs: java.util.ArrayList<Club>){}
    override fun passAllNotification(notifsList: java.util.ArrayList<StudentNotification>){}
}