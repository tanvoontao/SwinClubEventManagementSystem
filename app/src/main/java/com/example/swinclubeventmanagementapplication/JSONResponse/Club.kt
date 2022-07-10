package com.example.swinclubeventmanagementapplication.JSONResponse

import android.os.Parcel
import android.os.Parcelable

data class Club(
    val Advisor: String?,
    val Category: String?,
    val ClubAbout: String?,
    val ClubEmail: String?,
    val ClubLogoIMG: String?,
    val ClubName: String?,
    val MembershipFee: String?,
    val President: String?,
    val Secretary: String?,
    val SubscriptionMethod: String?,
    val Treasurer: String?,
    val VicePresident: String?
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
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Advisor)
        parcel.writeString(Category)
        parcel.writeString(ClubAbout)
        parcel.writeString(ClubEmail)
        parcel.writeString(ClubLogoIMG)
        parcel.writeString(ClubName)
        parcel.writeString(MembershipFee)
        parcel.writeString(President)
        parcel.writeString(Secretary)
        parcel.writeString(SubscriptionMethod)
        parcel.writeString(Treasurer)
        parcel.writeString(VicePresident)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Club> {
        override fun createFromParcel(parcel: Parcel): Club {
            return Club(parcel)
        }

        override fun newArray(size: Int): Array<Club?> {
            return arrayOfNulls(size)
        }
    }
}
