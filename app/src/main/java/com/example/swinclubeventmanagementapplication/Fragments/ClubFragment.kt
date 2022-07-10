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
import com.example.swinclubeventmanagementapplication.Adapters.ClubAdapter
import com.example.swinclubeventmanagementapplication.Adapters.JoinedClubAdapter
import com.example.swinclubeventmanagementapplication.DisplayClubActivity
import com.example.swinclubeventmanagementapplication.FirebaseDBHelper
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.example.swinclubeventmanagementapplication.R
import com.google.firebase.database.DatabaseReference


class ClubFragment :
    Fragment(),
    FirebaseDBHelper.itemOnClickListener,
    JoinedClubAdapter.itemOnClickListener{
    private lateinit var recyclerView: RecyclerView
    private lateinit var firebaseDbHelper: FirebaseDBHelper
    private lateinit var sClubs:ArrayList<Club>

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val intent = result.data
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_club, container, false)
        initUI(view)
        recyclerView = view.findViewById(R.id.recycler_view)
//        firebaseDbHelper.getSimplifiedClubData()
        firebaseDbHelper.getAllClubsData()
        return view
    }

    private fun loadAdapter(){
        recyclerView.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
        val adapter = JoinedClubAdapter(sClubs,this)
        recyclerView.adapter=adapter
    }


    private fun initUI(view: View?) {
        firebaseDbHelper = FirebaseDBHelper(requireContext(),null,null,this)
        sClubs = ArrayList<Club>()
    }

    override fun passData(std: Student) {}
    override fun passSimplifiedClubs(clubs: ArrayList<SimplifiedClub>) {
//        sClubs = clubs
//        loadAdapter()
    }

    override fun isCommitteMember(ya: Boolean){}
    override fun passMemberListing(memberListing: ArrayList<String>){}
    override fun passEventListing(eventListing: ArrayList<Event>){}
    override fun passAllNotification(notifsList: ArrayList<StudentNotification>){}
    override fun passAllClubs(allClubs: ArrayList<Club>) {
        sClubs = allClubs
        loadAdapter()
    }


    override fun OnClickDisplayClub(c: Club) {
        val intent: Intent = Intent(context, DisplayClubActivity::class.java)
        intent.putExtra("club", c)
        resultLauncher.launch(intent)
    }
    override fun onResume() {
        super.onResume()
        sClubs = ArrayList()
        sClubs.clear()
        if (sClubs.isEmpty()) {
            firebaseDbHelper.getAllClubsData()
        }
    }
}