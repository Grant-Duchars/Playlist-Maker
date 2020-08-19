package edu.rosehulman.ducharg.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PlaylistAdapter(var context: Context?, userId: String) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    private val playlists = ArrayList<Playlist>()

    private val playlistsRef = FirebaseFirestore.getInstance()
        .collection(Constants.USERS)
        .document(userId)
        .collection(Constants.PLAYLISTS)

    init {
        playlistsRef.addSnapshotListener { snapshot, _ ->
            playlists.clear()
            if (snapshot != null) {
                for (doc in snapshot) {
                    playlists.add(Playlist.fromDocument(doc))
                }
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.playlist_view, parent, false)
        return PlaylistViewHolder(view, this)
    }

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    fun add(playlist: Playlist) {
        playlistsRef.add(playlist)
    }

    fun remove(pos: Int) {
        playlistsRef.document(playlists[pos].id).delete()
    }

}