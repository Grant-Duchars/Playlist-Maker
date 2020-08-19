package edu.rosehulman.ducharg.playlistmaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyService

var mSpotifyAppRemote: SpotifyAppRemote? = null
var spotify: SpotifyService? = null

class MainActivity : AppCompatActivity() {

    private lateinit var spotifyAccessToken: String

    private val connectionParams = ConnectionParams.Builder(Constants.CLIENT_ID)
        .setRedirectUri(Constants.REDIRECT_URI)
        .showAuthView(true)
        .build()

    private val spotifyApi = SpotifyApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Login with spotify for api access
        val authBuilder = AuthenticationRequest.Builder(
            Constants.CLIENT_ID,
            AuthenticationResponse.Type.TOKEN,
            Constants.REDIRECT_URI
        )
            .setScopes(arrayOf("streaming"))
        val request = authBuilder.build()
        AuthenticationClient.openLoginActivity(this, Constants.REQUEST_CODE, request)
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(Constants.providers)
                    .build(),
                Constants.RC_SIGN_IN
            )
        } else {
            Log.d(Constants.TAG, "Current User: ${user.uid}")
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_LoginFragment_to_PlaylistFragment)
        }
        // Make sure there isn't already a connected app remote
        if (mSpotifyAppRemote != null && mSpotifyAppRemote!!.isConnected) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote)
        }
        SpotifyAppRemote.connect(this, connectionParams, SpotifyRemoteConnector())
    }

    override fun onStop() {
        super.onStop()
        SpotifyAppRemote.disconnect(mSpotifyAppRemote)
    }

    /**
     * Class that implements Spotify's ConnectionListener interface for
     * connecting with the remote player
     */
    class SpotifyRemoteConnector : Connector.ConnectionListener {

        override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
            mSpotifyAppRemote = spotifyAppRemote
            Log.d(Constants.TAG, "Remote Connected")
            connected()
        }

        override fun onFailure(throwable: Throwable?) {
            Log.e(Constants.TAG, "Remote Connection Error: ${throwable?.message}")
        }

        private fun connected() {
            mSpotifyAppRemote?.playerApi?.subscribeToPlayerState()
                ?.setEventCallback { playerState ->
                    if (!playerState.isPaused) {
                        val track = playerState.track
                        if (track != null) {
                            Log.d(
                                Constants.TAG,
                                "Now Playing: '${track.name}' by ${track.artist.name}"
                            )
                        }
                    } else if (playerState.isPaused) {
                        Log.d(Constants.TAG, "Playback Paused")
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == Constants.REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    Log.d(Constants.TAG, "Auth Success")
                    spotifyAccessToken = response.accessToken
                    spotifyApi.setAccessToken(spotifyAccessToken)
                    spotify = spotifyApi.service
                }
                AuthenticationResponse.Type.ERROR -> Log.d(Constants.TAG, "Auth Failure")
                else -> Log.d(Constants.TAG, "Handle Default")
            }
        } else if (requestCode == Constants.RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(intent)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                Log.d(Constants.TAG, "User Logged In: ${user?.uid}")
                findNavController(R.id.nav_host_fragment).navigate(R.id.PlaylistFragment)
            } else {
                // do something
            }
        }
    }
}