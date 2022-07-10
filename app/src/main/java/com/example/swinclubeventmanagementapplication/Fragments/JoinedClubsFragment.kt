package com.example.swinclubeventmanagementapplication.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.swinclubeventmanagementapplication.*
import com.example.swinclubeventmanagementapplication.Adapters.ChooseAudienceAdapter
import com.example.swinclubeventmanagementapplication.Adapters.ClubAdapter
import com.example.swinclubeventmanagementapplication.Adapters.EventAdapter
import com.example.swinclubeventmanagementapplication.Adapters.JoinedClubAdapter
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.google.firebase.auth.FirebaseAuth

class JoinedClubsFragment :
    Fragment(),
    FirebaseDBHelper.itemOnClickListener,
    JoinedClubAdapter.itemOnClickListener{

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val intent = result.data
        }
    }

    private lateinit var firebaseDbHelper: FirebaseDBHelper

    private lateinit var clubsList:ArrayList<Club>
    private lateinit var joinedClubs:ArrayList<Club>

    private lateinit var recyclerView: RecyclerView
    private lateinit var currStdID: String
    private lateinit var student: Student


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_choose_audience, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        initUI(view)
        return view
    }
    private fun loadAdapter(){
        recyclerView.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
        val adapter = JoinedClubAdapter(joinedClubs,this)
        recyclerView.adapter=adapter
    }

    private fun getJoinedClubs() {
        for (c in clubsList){
            for(stdCRole in student.JoinedClubs!!){
                if(stdCRole.clubName == c.ClubName){
                    joinedClubs.add(c)
                }
            }
        }
    }

    private fun updateData(){
        firebaseDbHelper.readProfile(currStdID)
    }

    private fun initUI(view: View?) {
        currStdID =
            Regex("^([^@]+)").find(FirebaseAuth.getInstance().currentUser!!.email!!)?.value.toString()
        firebaseDbHelper = FirebaseDBHelper(requireContext(),null,null,this)
        updateData()
        joinedClubs =ArrayList<Club>()
    }

    override fun passData(std: Student) {
        student = std
        firebaseDbHelper.getAllClubsData()
    }

    override fun passSimplifiedClubs(clubs: ArrayList<SimplifiedClub>){}

    override fun isCommitteMember(ya: Boolean){}

    override fun passMemberListing(memberListing: ArrayList<String>){}

    override fun passEventListing(eventListing: ArrayList<Event>){}
    override fun passAllNotification(notifsList: ArrayList<StudentNotification>){}

    override fun passAllClubs(sClubs: ArrayList<Club>) {
        clubsList = sClubs
        getJoinedClubs()
        loadAdapter()
    }

    override fun OnClickDisplayClub(c: Club) {
        val intent: Intent = Intent(context, DisplayClubActivity::class.java)
        intent.putExtra("club", c)
        resultLauncher.launch(intent)
    }

}