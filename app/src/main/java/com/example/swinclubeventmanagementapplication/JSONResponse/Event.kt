package com.example.swinclubeventmanagementapplication.JSONResponse

import android.os.Parcel
import android.os.Parcelable

data class Event(
    var Audience: String?,
    val EventActivities: String?,
    val EventDate: String?,
    val EventDescription: String?,
    val EventLoc: String?,
    val EventParticipationFee: String?,
    val EventPosterIMG: String?,
    val EventTime: String?,
    val EventTitle: String?,
    val EventType: String?,
    val Status: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Audience)
        parcel.writeString(EventActivities)
        parcel.writeString(EventDate)
        parcel.writeString(EventDescription)
        parcel.writeString(EventLoc)
        parcel.writeString(EventParticipationFee)
        parcel.writeString(EventPosterIMG)
        parcel.writeString(EventTime)
        parcel.writeString(EventTitle)
        parcel.writeString(EventType)
        parcel.writeString(Status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }
}
