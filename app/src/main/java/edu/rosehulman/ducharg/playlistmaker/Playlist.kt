package edu.rosehulman.ducharg.playlistmaker

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class Playlist(var title: String? = "My List", var list: ArrayList<String> = ArrayList()) :
    Parcelable {

    @get:Exclude
    var id = ""

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createStringArrayList() as ArrayList<String>
    )

    fun add(item: String, pos: Int = list.size) {
        list.add(pos, item)
    }

    fun remove(pos: Int) {
        list.removeAt(pos)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeStringList(list)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Playlist> {
        override fun createFromParcel(parcel: Parcel): Playlist {
            return Playlist(parcel)
        }

        override fun newArray(size: Int): Array<Playlist?> {
            return arrayOfNulls(size)
        }

        fun fromDocument(doc: DocumentSnapshot): Playlist {
            val playlist = doc.toObject(Playlist::class.java)!!
            playlist.id = doc.id
            return playlist
        }
    }

}