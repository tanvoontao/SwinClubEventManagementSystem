package com.example.swinclubeventmanagementapplication.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.swinclubeventmanagementapplication.Adapters.ChooseAudienceAdapter
import com.example.swinclubeventmanagementapplication.Adapters.ClubAdapter
import com.example.swinclubeventmanagementapplication.Adapters.EventAdapter
import com.example.swinclubeventmanagementapplication.DisplayEventActivity
import com.example.swinclubeventmanagementapplication.FirebaseDBHelper
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.example.swinclubeventmanagementapplication.R
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class UpComingEventFragment :
    Fragment(),
    FirebaseDBHelper.itemOnClickListener,
    EventAdapter.itemOnClickListener{
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var firebaseDbHelper: FirebaseDBHelper
    private lateinit var eList: ArrayList<Event>
    private lateinit var recyclerView: RecyclerView
    private lateinit var textView:TextView
    private lateinit var imageViewEmptyList:ImageView

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val intent = result.data
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("-------------------back from club event3-------------------")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_up_coming_event, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        initUI(view)
//        if(param2 != null){
//            println("-------param2---------")
//            firebaseDbHelper.getAllEvent(param2!!)
//        }else{
//            println("-------param22---------")
//            firebaseDbHelper.getAllEvent("")
//        }

//        updateEventData()
        return view
    }
    private fun initUI(view: View) {
        firebaseDbHelper = FirebaseDBHelper(requireContext(),null,null,this)
        textView = view.findViewById(R.id.textView)
        imageViewEmptyList = view.findViewById(R.id.imageViewEmptyList)

    }

    private fun loadAdapter(){
        recyclerView.layoutManager= LinearLayoutManager(context)
        if(param1 != "past"){
            val adapter = EventAdapter(eList,this,true)
            recyclerView.adapter=adapter
        }else{
            val adapter = EventAdapter(eList,this,false)
            recyclerView.adapter=adapter
        }
        if(eList.isEmpty()){
            recyclerView.visibility = View.GONE
            textView.visibility = View.VISIBLE
            imageViewEmptyList.visibility = View.VISIBLE
        }
    }

    override fun passData(std: Student) {}
    override fun passSimplifiedClubs(clubs: ArrayList<SimplifiedClub>){}
    override fun passAllNotification(notifsList: ArrayList<StudentNotification>){}

    override fun isCommitteMember(ya: Boolean){}
    override fun passMemberListing(memberListing: ArrayList<String>){}
    override fun passEventListing(eventListing: ArrayList<Event>) {
        eList = ArrayList()
        eList.clear()
        eList = eventListing
        loadAdapter()
        println("-------------------eList--------------")
        println(eList.size)

    }

    override fun passAllClubs(sClubs: ArrayList<Club>){}

    override fun OnClickDisplayEvent(e: Event) {
        val intent: Intent = Intent(context,DisplayEventActivity::class.java)
        intent.putExtra("event", e)
        resultLauncher.launch(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String,param2: String) =
            UpComingEventFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        println("-------param222---------")
        eList = ArrayList()
        eList.clear()
        if (eList.isEmpty()) {
            if(param2 != null){
                firebaseDbHelper.getAllEvent(param2!!)
            }else{
                firebaseDbHelper.getAllEvent("")
            }
        }
    }

}