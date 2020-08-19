package edu.rosehulman.ducharg.playlistmaker

import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class PlaylistDetailFragment : Fragment() {

    var playlist: Playlist? = null
    private var adapter: PlaylistDetailAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            playlist = it.getParcelable(Constants.ARG_PLAYLIST_DETAIL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_playlist_detail, container, false)
        adapter = PlaylistDetailAdapter(
            context,
            FirebaseAuth.getInstance().currentUser!!.uid,
            playlist!!.id
        )
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemRecycler = view.findViewById<RecyclerView>(R.id.playlist_detail_recycler)
        itemRecycler.layoutManager = LinearLayoutManager(context)
        itemRecycler.setHasFixedSize(true)
        itemRecycler.adapter = adapter
        view.findViewById<TextView>(R.id.playlist_detail_title).text = playlist!!.title
        view.findViewById<CardView>(R.id.playlist_detail_play_button).setOnClickListener {
            for (item in playlist!!.list) {
                SpotifyItemQueueTask().execute(item.split(",")[0])
            }
            mSpotifyAppRemote?.playerApi?.skipNext()
            mSpotifyAppRemote?.playerApi?.resume()
        }
        view.findViewById<CardView>(R.id.playlist_detail_shuffle_button).setOnClickListener {
            // TODO Shuffle
        }
        view.findViewById<FloatingActionButton>(R.id.playlist_detail_add_button)
            .setOnClickListener {
                showPlaylistAddDialog()
            }
    }

    private fun showPlaylistAddDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Add Song or Album by Search")
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_item_add, null, false)
        builder.setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val type = view.findViewById<ToggleButton>(R.id.dialog_toggle_button).isChecked
                val input = view.findViewById<EditText>(R.id.dialog_search_box).text.toString()
                if (!type) { // If Song
                    adapter?.add(SpotifySongSearchTask().execute(input).get())
                } else { // If Album
                    adapter?.add(SpotifyAlbumSearchTask().execute(input).get())
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    /**
     * Async task for spotify song searching
     */
    private class SpotifySongSearchTask : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg input: String?): String {
            val result = spotify!!.searchTracks(input[0])
            val song = result!!.tracks.items[0]
            Log.d(Constants.TAG, "Found: ${song.name}")
            return "${song.uri},${song.name},song"
        }

    }

    /**
     * Async task for spotify album searching
     */
    private class SpotifyAlbumSearchTask : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg input: String?): String {
            val result = spotify!!.searchAlbums(input[0])
            val album = result!!.albums.items[0]
            Log.d(Constants.TAG, "Found: ${album.name}")
            return "${album.uri},${album.name},album"
        }

    }

    private class SpotifyItemQueueTask : AsyncTask<String, Int, Int>() {

        override fun doInBackground(vararg params: String?): Int {
            mSpotifyAppRemote?.playerApi?.queue(params[0])
            return 0
        }

    }

}