package com.example.swinclubeventmanagementapplication.JSONResponse

import android.os.Parcel
import android.os.Parcelable

data class Student(
    var StudentID: String?,
    var StudentEmail: String?,
    var Gender: String?,
    var Email: String?,
    var Dob: String?,
    var Password: String?,
    var ContactNo: String?,
    var Name: String?,
    var ProfileIMG: String?,
    var EnrollmentDate: String?,
    var JoinedClubs: ArrayList<JoinedClub>?,
    var Role: ArrayList<Role>?
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
        parcel.readArrayList(ClassLoader.getSystemClassLoader()) as ArrayList<JoinedClub>,
        parcel.readArrayList(ClassLoader.getSystemClassLoader()) as ArrayList<Role>
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(StudentID)
        parcel.writeString(StudentEmail)
        parcel.writeString(Gender)
        parcel.writeString(Email)
        parcel.writeString(Dob)
        parcel.writeString(Password)
        parcel.writeString(ContactNo)
        parcel.writeString(Name)
        parcel.writeString(ProfileIMG)
        parcel.writeString(EnrollmentDate)
        parcel.writeList(JoinedClubs)
        parcel.writeList(Role)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Student> {
        override fun createFromParcel(parcel: Parcel): Student {
            return Student(parcel)
        }

        override fun newArray(size: Int): Array<Student?> {
            return arrayOfNulls(size)
        }
    }
}
