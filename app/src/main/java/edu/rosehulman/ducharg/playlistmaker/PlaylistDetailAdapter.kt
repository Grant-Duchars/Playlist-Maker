package edu.rosehulman.ducharg.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PlaylistDetailAdapter(var context: Context?, userId: String, playlistId: String) :
    RecyclerView.Adapter<PlaylistDetailViewHolder>() {

    private val list = ArrayList<String>()

    private val playlistRef = FirebaseFirestore.getInstance()
        .collection(Constants.USERS)
        .document(userId)
        .collection(Constants.PLAYLISTS)
        .document(playlistId)

    init {
        playlistRef.addSnapshotListener { snapshot, _ ->
            list.clear()
            var snapshotList = snapshot?.data?.get("list")
            if (snapshotList != null) {
                snapshotList = snapshotList as ArrayList<*>
                for (item in snapshotList) {
                    list.add(item as String)
                }
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistDetailViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.playlist_detail_view, parent, false)
        return PlaylistDetailViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: PlaylistDetailViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun add(item: String) {
        playlistRef.update("list", FieldValue.arrayUnion(item))
    }

    fun remove(pos: Int) {
        val item = list[pos]
        playlistRef.update("list", FieldValue.arrayRemove(item))
    }
}