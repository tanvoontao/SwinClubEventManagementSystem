package com.example.swinclubeventmanagementapplication.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.swinclubeventmanagementapplication.FirebaseDBHelper
import com.example.swinclubeventmanagementapplication.JSONResponse.SimplifiedClub
import com.example.swinclubeventmanagementapplication.JSONResponse.Student
import com.example.swinclubeventmanagementapplication.MainActivity
import com.example.swinclubeventmanagementapplication.R
import com.google.firebase.auth.FirebaseAuth


class LogoutFragment : Fragment(){

    private lateinit var firebaseDbHelper: FirebaseDBHelper
    private lateinit var firebaseAuth: FirebaseAuth
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
        val view = inflater.inflate(R.layout.fragment_logout, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDbHelper = FirebaseDBHelper(requireContext(),firebaseAuth, null,null)
        logout()
        return view
    }

    private fun logout() {
        firebaseDbHelper.logout()
        checkUser()
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            val intent: Intent = Intent(context,MainActivity::class.java)
            resultLauncher.launch(intent)
            requireActivity().finish()
        }
    }



}