package com.example.swinclubeventmanagementapplication

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewpager2.widget.ViewPager2
import com.example.swinclubeventmanagementapplication.Adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ClubEventActivity : AppCompatActivity() {
    private lateinit var clubName:String

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val intent = result.data
            updateContent()
            println("-------------------back from club event1-------------------")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_event)
        val intent = getIntent()
        clubName = intent?.getStringExtra("clubName").toString()
        updateContent()
    }

    private fun updateContent(){
        // 2 fragments is needed for 2 tabs
        val tabTitles = arrayOf("Up Coming Event", "Past Event")
        val tabLayout = findViewById<TabLayout>(R.id.tablayout)
        val view_pager = findViewById<ViewPager2>(R.id.viewpager)
        view_pager.adapter= ViewPagerAdapter(this,tabTitles, clubName.replace(" ",""))

        TabLayoutMediator(tabLayout,view_pager){
                tab,position -> tab.text = tabTitles[position]
        }.attach()
    }
}