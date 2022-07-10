package com.example.swinclubeventmanagementapplication.JSONResponse

import android.os.Parcel
import android.os.Parcelable

data class StudentNotification(
    val title: String?,
    val msg: String?,
    val type: String?,
    val date: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(msg)
        parcel.writeString(type)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StudentNotification> {
        override fun createFromParcel(parcel: Parcel): StudentNotification {
            return StudentNotification(parcel)
        }

        override fun newArray(size: Int): Array<StudentNotification?> {
            return arrayOfNulls(size)
        }
    }
}
