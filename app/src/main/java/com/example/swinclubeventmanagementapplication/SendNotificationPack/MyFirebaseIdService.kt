package com.example.swinclubeventmanagementapplication.SendNotificationPack

import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService


class MyFirebaseIdService:FirebaseMessagingService(){
    override fun onNewToken(s:String){
        super.onNewToken(s)
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var currUserEmail: String = FirebaseAuth.getInstance().currentUser!!.email!!

        val regex = Regex("^([^@]+)")
        val stdID = regex.find(currUserEmail)?.value.toString()

        var refreshToken = ""
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    println("Fetching FCM registration token failed" + task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                refreshToken = task.result
                if(firebaseUser!=null){
                    updateToken(refreshToken,stdID)
                }
        })

    }
    private fun updateToken(refreshToken: String, stdID: String){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        var token:Token= Token(refreshToken)

        val student = mapOf<String,String>(
            "token" to token.token.toString()
        )
        FirebaseDatabase.getInstance().getReference("Students").child(stdID).updateChildren(student).addOnSuccessListener {
//            Toast.makeText(this,"Successfuly Updated", Toast.LENGTH_SHORT).show()
            Log.e("token", "Successfully Updated");
        }.addOnFailureListener{
//            Toast.makeText(this,"Failed to Update", Toast.LENGTH_SHORT).show()
            Log.e("token", "Failed to Update");
        }
    }


}