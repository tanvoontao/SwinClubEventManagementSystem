package com.example.swinclubeventmanagementapplication.JSONResponse

import android.os.Parcel
import android.os.Parcelable

data class SimplifiedClub(
    var clubName: String?,
    var clubLogoUrl: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(clubName)
        parcel.writeString(clubLogoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SimplifiedClub> {
        override fun createFromParcel(parcel: Parcel): SimplifiedClub {
            return SimplifiedClub(parcel)
        }

        override fun newArray(size: Int): Array<SimplifiedClub?> {
            return arrayOfNulls(size)
        }
    }
}
