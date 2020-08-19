package edu.rosehulman.ducharg.playlistmaker

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class PlaylistFragment : Fragment() {

    private var adapter: PlaylistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(Constants.TAG, "Playlists View Created")
        adapter = PlaylistAdapter(context, FirebaseAuth.getInstance().currentUser!!.uid)
        val playlistRecycler = view.findViewById<RecyclerView>(R.id.playlist_recycler)
        playlistRecycler.layoutManager = LinearLayoutManager(context)
        playlistRecycler.setHasFixedSize(true)
        playlistRecycler.adapter = adapter
        view.findViewById<FloatingActionButton>(R.id.playlists_add_button).setOnClickListener {
            showPlaylistAddDialog()
        }
        view.findViewById<ImageButton>(R.id.button_account).setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_PlaylistFragment_to_AccountFragment)
        }
    }

    private fun showPlaylistAddDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Create New Playlist")
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_playlist_add, null, false)
        builder.setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                adapter?.add(Playlist(view.findViewById<EditText>(R.id.dialog_title_edit).text.toString()))
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

}