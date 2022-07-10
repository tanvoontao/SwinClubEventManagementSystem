package com.example.swinclubeventmanagementapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swinclubeventmanagementapplication.Adapters.ChooseAudienceAdapter
import com.example.swinclubeventmanagementapplication.JSONResponse.SimplifiedClub
import com.google.firebase.database.FirebaseDatabase

class ChooseAudienceActivity :
    AppCompatActivity(),
    ChooseAudienceAdapter.onClickListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var chosenAudience:String
    private lateinit var audiences:ArrayList<SimplifiedClub>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_audience)

        val intent = getIntent()
        audiences = intent.getParcelableArrayListExtra<SimplifiedClub>("audience")!!

        initUI()
        loadAdapter()
    }

    private fun initUI(){
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
    }
    private fun loadAdapter() {
        recyclerView.layoutManager= LinearLayoutManager(this)
        val adapter = ChooseAudienceAdapter(audiences,this)
        recyclerView.adapter=adapter
    }

    override fun OnClick(audience: String) {
        chosenAudience = audience
        // do back to add event page
        val intent = Intent()
        intent.putExtra("audience", audience)
        setResult(Activity.RESULT_OK, intent)
        finish() // redirect back to home page / display item page
    }
}