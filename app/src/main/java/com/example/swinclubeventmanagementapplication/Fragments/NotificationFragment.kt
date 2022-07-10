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
import com.example.swinclubeventmanagementapplication.Adapters.JoinedClubAdapter
import com.example.swinclubeventmanagementapplication.Adapters.NotificationAdapter
import com.example.swinclubeventmanagementapplication.DisplayEventActivity
import com.example.swinclubeventmanagementapplication.FirebaseDBHelper
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.example.swinclubeventmanagementapplication.R
import com.google.firebase.auth.FirebaseAuth

class NotificationFragment : Fragment(),
    FirebaseDBHelper.itemOnClickListener,
    NotificationAdapter.itemOnClickListener{

    private lateinit var firebaseDbHelper: FirebaseDBHelper
    private lateinit var notifList: ArrayList<StudentNotification>
    private lateinit var recyclerView: RecyclerView
    private lateinit var currStdID: String

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

        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        initUI(view)
        firebaseDbHelper.getAllNotification(currStdID)
        return view
    }

    private fun initUI(view: View?) {
        firebaseDbHelper = FirebaseDBHelper(requireContext(),null,null,this)
        currStdID =
            Regex("^([^@]+)").find(FirebaseAuth.getInstance().currentUser!!.email!!)?.value.toString()
    }
    private fun loadAdapter(){
        recyclerView.layoutManager= LinearLayoutManager(context)
        val adapter = NotificationAdapter(notifList,this)
        recyclerView.adapter=adapter
    }
    override fun passData(std: Student){}
    override fun passSimplifiedClubs(clubs: ArrayList<SimplifiedClub>){}
    override fun isCommitteMember(ya: Boolean){}
    override fun passMemberListing(memberListing: ArrayList<String>){}
    override fun passEventListing(eventListing: ArrayList<Event>){}
    override fun passAllClubs(sClubs: ArrayList<Club>){}
    override fun passAllNotification(notifsList: ArrayList<StudentNotification>) {
        notifList = notifsList
        println("-------------------eList--------------")
        println(notifList.size)
        loadAdapter()
    }

    override fun OnClickDisplayEvent(notifTitle: String){
        val intent: Intent = Intent(context, DisplayEventActivity::class.java)
        intent.putExtra("eventTitle", notifTitle)
        resultLauncher.launch(intent)
    }
    override fun OnClickDisplayClub(notifTitle: String){
        println("open club")
    }
    override fun onResume() {
        super.onResume()
        notifList = ArrayList()
        notifList.clear()
        if (notifList.isEmpty()) {
            firebaseDbHelper.getAllNotification(currStdID)
        }
    }


}