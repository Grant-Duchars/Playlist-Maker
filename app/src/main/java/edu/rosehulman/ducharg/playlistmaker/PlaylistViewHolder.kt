package edu.rosehulman.ducharg.playlistmaker

import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView

class PlaylistViewHolder(itemView: View, adapter: PlaylistAdapter) :
    RecyclerView.ViewHolder(itemView) {

    private val plistTitle = itemView.findViewById<TextView>(R.id.playlist_view_title)

    init {
        itemView.setOnLongClickListener {
            adapter.remove(adapterPosition)
            true
        }
    }

    fun bind(playlist: Playlist) {
        plistTitle.text = playlist.title
        itemView.setOnClickListener {
            Navigation.findNavController(itemView).navigate(
                R.id.action_PlaylistFragment_to_PlaylistDetailFragment,
                bundleOf(Constants.ARG_PLAYLIST_DETAIL to playlist)
            )
        }
    }
}