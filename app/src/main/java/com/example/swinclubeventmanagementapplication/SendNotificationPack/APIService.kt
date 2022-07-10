package com.example.swinclubeventmanagementapplication.SendNotificationPack

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
public interface APIService {
    @Headers(
        "Content-Type:application/json", // server key on below
        "Authorization:key=AAAA92JAz_A:APA91bE0_kdIlaI_DnmOLDZeHL_GYUqGgwY7PqHdE8OrFCrEInXnnYQ92HHIWHQCE0zcPqkqMlHqlusOAb0yy8tXNBSVSpamCUuhVFOKeGJidWq9LErWFOmw9FNMPUazXblgprIifXdv"
    )

    @POST("fcm/send")
    open fun sendNotifcation(@Body body: NotificationSender?): Call<MyResponse?>?
}

