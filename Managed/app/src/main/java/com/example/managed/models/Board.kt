package com.example.managed.models

import android.os.Parcel
import android.os.Parcelable

data class Board (
    val name : String = "",
    val Image: String = "",
    val createdBy: String = "",
    val assignedTo :ArrayList<String> = ArrayList()

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel){
        parcel.writeString(name)
        parcel.writeString(Image)
        parcel.writeString(createdBy)
        parcel.writeStringList(assignedTo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}
