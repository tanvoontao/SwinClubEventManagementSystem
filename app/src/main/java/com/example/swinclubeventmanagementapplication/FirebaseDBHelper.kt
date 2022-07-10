package com.example.swinclubeventmanagementapplication

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class FirebaseDBHelper(
    private val context:Context,
    private val firebaseAuth: FirebaseAuth?,
    private val database: DatabaseReference?,
    val listener: itemOnClickListener?) {
    fun logout() {
        firebaseAuth!!.signOut()
    }

    fun readProfile(stdID:String){
        var student:Student? =null
        val joinedClubs:ArrayList<JoinedClub>? = ArrayList<JoinedClub>()
        val roles:ArrayList<Role>? = ArrayList<Role>()


        Log.e("stdID",stdID);
        val database = FirebaseDatabase.getInstance().getReference("Students")
        database.child(stdID).get().addOnSuccessListener {

            if (it.exists()){
                Log.e("stddID","go inside");

                val stdEmail = it.child("studentEmail").value.toString()

                val gender = it.child("gender").value.toString()
                val email = it.child("email").value.toString()
                val dob = it.child("dob").value.toString()
                val pw = it.child("password").value.toString()
                val contactNo = it.child("contactNo").value.toString()
                val name = it.child("name").value.toString()
                val profileIMG = it.child("profileIMG").value.toString()
                val enrollmentDate = it.child("enrollmentDate").value.toString()
                if(it.child("joinedClubs").exists()){
                    val joinedClub:HashMap<String,Boolean> = it.child("joinedClubs").value as HashMap<String, Boolean>
                    for ((key, value) in joinedClub) {
                        val club:String = key.replace("(.)([A-Z])".toRegex(), "$1 $2")
                        //                    val club:String = key.replace("\\d+".toRegex(), "").replace("(.)([A-Z])".toRegex(), "$1 $2")
                        joinedClubs!!.add(JoinedClub(club))
                    }
                }
                if(it.child("roles").exists()){
//                    val role: HashMap<String, ArrayList<String>> = it.child("roles").value as HashMap<String, ArrayList<String>>
                    val role = it.child("roles").value as HashMap<String, ArrayList<String>>
                    for ((key, value) in role) {
                        val club:String = key.replace("(.)([A-Z])".toRegex(), "$1 $2")
                        val positions = value
                        roles!!.add(Role(club, positions!!))
                    }
                }

//                println(name!!::class.java.typeName) //java.util.HashMap


                student = Student(stdID,stdEmail,gender,email,dob,pw,contactNo,name,profileIMG,enrollmentDate,
                    joinedClubs, roles
                )
                listener!!.passData(student!!)
            }else{
                Toast.makeText(context,"User Doesn't Exist", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener{
            Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()
        }

    }

    fun isRoleExist(stdID:String){
        val database = FirebaseDatabase.getInstance().getReference("Students")
        database.child(stdID).get().addOnSuccessListener {
            if(it.child("roles").exists()){
                listener!!.isCommitteMember(true)
            }else{
                listener!!.isCommitteMember(false)
            }

        }.addOnFailureListener{
            Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun getSimplifiedClubData() {
        var sClubs:ArrayList<SimplifiedClub> = ArrayList<SimplifiedClub>()
        val dbref = FirebaseDatabase.getInstance().getReference("Clubs")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){
                        var clubName = userSnapshot.child("ClubName").value.toString()
                        var clubLogoUrl = userSnapshot.child("ClubLogoIMG").value.toString()
                        sClubs.add(SimplifiedClub(clubName, clubLogoUrl))
                    }
                    listener!!.passSimplifiedClubs(sClubs)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getAllClubsData() {
        var sClubs:ArrayList<Club> = ArrayList<Club>()
        val dbref = FirebaseDatabase.getInstance().getReference("Clubs")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){
                        val Advisor: String? = userSnapshot.child("Advisor").value.toString()
                        val Category: String? = userSnapshot.child("Category").value.toString()
                        val ClubAbout: String? = userSnapshot.child("ClubAbout").value.toString()
                        val ClubEmail: String? = userSnapshot.child("ClubEmail").value.toString()
                        val ClubLogoIMG: String? = userSnapshot.child("ClubLogoIMG").value.toString()
                        val ClubName: String? = userSnapshot.child("ClubName").value.toString()
                        val MembershipFee: String? = userSnapshot.child("MembershipFee").value.toString()
                        val President: String? = userSnapshot.child("President").value.toString()
                        val Secretary: String? = userSnapshot.child("Secretary").value.toString()
                        val SubscriptionMethod: String? = userSnapshot.child("SubscriptionMethod").value.toString()
                        val Treasurer: String? = userSnapshot.child("Treasurer").value.toString()
                        val VicePresident: String = userSnapshot.child("VicePresident").value.toString()

                        val c = Club(
                             Advisor,
                         Category,
                         ClubAbout,
                         ClubEmail,
                         ClubLogoIMG,
                         ClubName,
                         MembershipFee,
                         President,
                         Secretary,
                         SubscriptionMethod,
                         Treasurer,
                         VicePresident
                        )
                        sClubs.add(c)

                    }
                    listener!!.passAllClubs(sClubs)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun addEvent(e:Event){
        val database = FirebaseDatabase.getInstance().getReference("Clubs")

        val clubId = (e.Audience)!!.replace(" ","")
        val eventId = (e.EventTitle)!!.replace(" ","")


        database.child(clubId).child("ClubEvents").child(eventId).setValue(e).addOnSuccessListener {
            Toast.makeText(context,"Successfully Saved",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
        }
    }

    fun addStudent(s: Student){
        val database = FirebaseDatabase.getInstance().getReference("Students")

        database.child(s.StudentID!!).setValue(s).addOnSuccessListener {
            Toast.makeText(context,"Successfully Saved",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
        }
    }

    fun getAudienceList(clubName:String) {


        val clubId = (clubName)!!.replace(" ","")
        val database = FirebaseDatabase.getInstance().getReference("Clubs")
        var memberListing:ArrayList<String> = ArrayList<String>()

        Log.e("clubName",clubId);

        database.child(clubId).get().addOnSuccessListener {
            if (it.exists()){
                if(it.child("ClubMembers").exists()){
                    val clubMembers:HashMap<String,Boolean> = it.child("ClubMembers").value as HashMap<String, Boolean>
                    for ((key, value) in clubMembers) {
                        memberListing!!.add(key)
                    }
                }
                Log.e("memberListing", memberListing.size.toString());
                listener!!.passMemberListing(memberListing!!)
            }else{
                Toast.makeText(context,"User Doesn't Exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun getAllStudent(){
        val database = FirebaseDatabase.getInstance().getReference("Students")
        val stdList = ArrayList<String>()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){


                    for (userSnapshot in snapshot.children){
                        stdList.add(userSnapshot.key.toString())
                    }
                    listener!!.passMemberListing(stdList)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getAllEvent(clubId: String){

        var eList:ArrayList<Event> = ArrayList<Event>()
        val dbref = FirebaseDatabase.getInstance().getReference("Clubs")

        if(clubId != ""){
            dbref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (userSnapshot in snapshot.children){
                            if (snapshot.child(clubId).exists()){
                                var currStdSnapshot = snapshot.child(clubId)
                                if(currStdSnapshot.child("ClubEvents").exists()){
                                    for(e in currStdSnapshot.child("ClubEvents").children){
                                        println(e.child("eventTitle").value)
                                        Log.e("first", "first");
                                        var event = Event(
                                            e.child("audience").value.toString(),
                                            e.child("eventActivities").value.toString(),
                                            e.child("eventDate").value.toString(),
                                            e.child("eventDescription").value.toString(),
                                            e.child("eventLoc").value.toString(),
                                            e.child("eventParticipationFee").value.toString(),
                                            e.child("eventPosterIMG").value.toString(),
                                            e.child("eventTime").value.toString(),
                                            e.child("eventTitle").value.toString(),
                                            e.child("eventType").value.toString(),
                                            e.child("status").value.toString()
                                        )
                                        eList.add(event)
                                    }
                                }
                                break
                            }

                        }
                        }
                        listener!!.passEventListing(eList)
                    }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }else{
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        if(userSnapshot.child("ClubEvents").exists()){
                            for(e in userSnapshot.child("ClubEvents").children){
                                println(e.child("eventTitle").value)

                                Log.e("second", "second");

                                var event = Event(
                                    e.child("audience").value.toString(),
                                    e.child("eventActivities").value.toString(),
                                    e.child("eventDate").value.toString(),
                                    e.child("eventDescription").value.toString(),
                                    e.child("eventLoc").value.toString(),
                                    e.child("eventParticipationFee").value.toString(),
                                    e.child("eventPosterIMG").value.toString(),
                                    e.child("eventTime").value.toString(),
                                    e.child("eventTitle").value.toString(),
                                    e.child("eventType").value.toString(),
                                    e.child("status").value.toString()
                                )
                                eList.add(event)
                            }
                        }
                    }
                    listener!!.passEventListing(eList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        }
    }

    fun updateEvent(e:Event){
        val database = FirebaseDatabase.getInstance().getReference("Clubs")
        val clubId = (e.Audience)!!.replace(" ","")
        val eventId = (e.EventTitle)!!.replace(" ","")

        val event = mapOf<String,String>(
            "audience" to e.Audience.toString(),
            "eventActivities" to e.EventActivities.toString(),
            "eventDate" to e.EventDate.toString(),
            "eventDescription" to e.EventDescription.toString(),
            "eventLoc" to e.EventLoc.toString(),
            "eventParticipationFee" to e.EventParticipationFee.toString(),
            "eventPosterIMG" to e.EventPosterIMG.toString(),
            "eventTime" to e.EventTime.toString(),
            "eventTitle" to e.EventTitle.toString(),
            "eventType" to e.EventType.toString(),
            "status" to e.Status.toString()
        )

        database.child(clubId).child("ClubEvents").child(eventId).updateChildren(event).addOnSuccessListener {
            Toast.makeText(context,"Successfuly Updated",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context,"Failed to Update",Toast.LENGTH_SHORT).show()
        }
    }

    fun cancelEvent(e:Event){
        val database = FirebaseDatabase.getInstance().getReference("Clubs")
        val clubId = (e.Audience)!!.replace(" ","")
        val eventId = (e.EventTitle)!!.replace(" ","")
        val event = mapOf<String,String>(
            "status" to "cancelled"
        )
        database.child(clubId).child("ClubEvents").child(eventId).updateChildren(event).addOnSuccessListener {
            Toast.makeText(context,"cancelled Updated",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context,"Failed to cancel",Toast.LENGTH_SHORT).show()
        }
    }

    fun readEvent(eventTitle: String) {
        var event:Event? =null
        val eventId = (eventTitle)!!.replace(" ","")
        var eList:ArrayList<Event> = ArrayList<Event>()

        val database = FirebaseDatabase.getInstance().getReference("Clubs")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        if(userSnapshot.child("ClubEvents").exists()){
                            for(e in userSnapshot.child("ClubEvents").children){
                                Log.e("e.key", e.key!!);
                                if(e.key == eventId){
                                    println(e.key)
                                    event = Event(
                                        e.child("audience").value.toString(),
                                        e.child("eventActivities").value.toString(),
                                        e.child("eventDate").value.toString(),
                                        e.child("eventDescription").value.toString(),
                                        e.child("eventLoc").value.toString(),
                                        e.child("eventParticipationFee").value.toString(),
                                        e.child("eventPosterIMG").value.toString(),
                                        e.child("eventTime").value.toString(),
                                        e.child("eventTitle").value.toString(),
                                        e.child("eventType").value.toString(),
                                        e.child("status").value.toString()
                                    )
                                    println(event)
                                    eList.add(event!!)
                                }

                            }
                        }
                    }
                    listener!!.passEventListing(eList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    fun getAllNotification(currStdID:String){
        var notificationList:ArrayList<StudentNotification> = ArrayList<StudentNotification>()
        val dbref = FirebaseDatabase.getInstance().getReference("Notifications")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.child(currStdID).exists()){
                    var currStdSnapshot = snapshot.child(currStdID)
                    if(currStdSnapshot.child("events").exists()){
                        println("------------------userSnapshot------------------")
                        for (notif in currStdSnapshot.child("events").children){
                            println(notif.key)
                            val n = StudentNotification(
                                notif.key,
                                notif.child("content").value.toString(),
                                "event",
                                notif.child("date").value.toString())
                            notificationList.add(n)
                        }
                    }
                    if(currStdSnapshot.child("clubs").exists()){
                        println("------------------userSnapshot------------------")
                        for (notif in currStdSnapshot.child("clubs").children){
                            println(notif.key)
                            val n = StudentNotification(
                                notif.key,
                                notif.child("content").value.toString(),
                                "club",
                                notif.child("date").value.toString())
                            notificationList.add(n)
                        }
                    }

                    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val result = notificationList.sortedByDescending {
                        LocalDate.parse(it.date, dateTimeFormatter)
                    }
                    listener!!.passAllNotification(result.toCollection(ArrayList()))

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun addNotification(stdId:String, notif: StudentNotification){
        val database = FirebaseDatabase.getInstance().getReference("Notifications")

        var notifType = ""
        if(notif.type == "event"){
            notifType = "events"
        }else{
            notifType = "clubs"
        }
        val notifDb = mapOf<String,String>(
            "content" to notif.msg.toString(),
            "date" to notif.date.toString(),
        )

        database.child(stdId).child(notifType).child(notif.title!!.replace(" ","")).setValue(notifDb).addOnSuccessListener {
            Toast.makeText(context,"Successfully Saved",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
        }
    }

    fun studentJoinClub(stdId:String, clubId:String){

        var clubID = clubId.replace(" ","")

        val clubsDb = FirebaseDatabase.getInstance().getReference("Clubs")
        val studentsDb = FirebaseDatabase.getInstance().getReference("Students")

        clubsDb.child(clubID).child("ClubMembers").child(stdId).setValue(true).addOnSuccessListener {
            Toast.makeText(context,"You has been added into $clubId !",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
        }

        studentsDb.child(stdId).child("joinedClubs").child(clubID).setValue(true).addOnSuccessListener {
            Toast.makeText(context,"You has been added into $clubId !",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
        }

    }

    fun updateProfile(s:Student){
        var database = FirebaseDatabase.getInstance().getReference("Students")

        val user = mapOf<String,String>(
            "contactNo" to s.ContactNo.toString(),
            "email" to s.Email.toString(),
            "dob" to s.Dob.toString(),
            "profileIMG" to s.ProfileIMG.toString()
        )

        database.child(s.StudentID!!).updateChildren(user).addOnSuccessListener {

            Toast.makeText(context,"Successfully Updated profile",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener{
            Toast.makeText(context,"Failed to Update",Toast.LENGTH_SHORT).show()
        }
    }

    interface itemOnClickListener {
        fun passData(std: Student)
        fun passSimplifiedClubs(clubs: ArrayList<SimplifiedClub>)
        fun isCommitteMember(ya: Boolean)
        fun passMemberListing(memberListing: ArrayList<String>)
        fun passEventListing(eventListing: ArrayList<Event>)
        fun passAllClubs(sClubs: ArrayList<Club>)
        fun passAllNotification(notifsList: ArrayList<StudentNotification>)
    }



}