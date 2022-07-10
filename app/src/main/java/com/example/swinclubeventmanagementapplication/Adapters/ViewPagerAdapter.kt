package com.example.swinclubeventmanagementapplication.Adapters

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.swinclubeventmanagementapplication.Fragments.UpComingEventFragment

class ViewPagerAdapter(
    appCompatActivity: AppCompatActivity,
    private val tabTitle: Array<String>,
    private val clubId: String
):
    FragmentStateAdapter(appCompatActivity) {

    override fun getItemCount(): Int = tabTitle.size

    override fun createFragment(position: Int): Fragment {
        Log.e("ViewPagerAdapter","back from club event2");

        when (position) {
            0 -> {
                val loadFragment = UpComingEventFragment.newInstance("upcoming",clubId)
                return loadFragment
            }
            else -> {
                val loadFragment = UpComingEventFragment.newInstance("past",clubId)
                return loadFragment
            }
        }
    }
}