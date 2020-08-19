package edu.rosehulman.ducharg.playlistmaker

import android.os.Parcel
import android.os.Parcelable

data class PlaylistItem(val uri: String?, val name: String?, val type: Boolean) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uri)
        parcel.writeString(name)
        parcel.writeByte(if (type) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlaylistItem> {
        override fun createFromParcel(parcel: Parcel): PlaylistItem {
            return PlaylistItem(parcel)
        }

        override fun newArray(size: Int): Array<PlaylistItem?> {
            return arrayOfNulls(size)
        }
    }
}