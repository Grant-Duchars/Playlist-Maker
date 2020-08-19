package edu.rosehulman.ducharg.playlistmaker

import com.firebase.ui.auth.AuthUI

object Constants {
    // Console Log Tag
    const val TAG = "PMD"

    // Spotify Api Credentials
    const val CLIENT_ID = "7d0f92c3014947d6aa84e06dd624973f"
    const val REDIRECT_URI = "duchargplaylistmaker://callback"

    // Spotify Remote Response Code
    const val REQUEST_CODE = 8118

    // Firebase Request Code
    const val RC_SIGN_IN = 1234

    // Firebase Login Providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    // Firebase Collection Names
    const val USERS = "users"
    const val PLAYLISTS = "playlists"
    const val LIST = "list"

    // Playlist Detail Fragment Arg String
    const val ARG_PLAYLIST_DETAIL = "argPlaylist"

}