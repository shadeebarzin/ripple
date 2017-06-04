package edu.ucsb.cs.cs190i.aferguson.ripple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements ConnectionStateCallback, PlayerNotificationCallback {

    //Client ID for individual user (must fetch from spotify)
    private static final String CLIENT_ID = "5aa6208c6cd54357ad6b55bd67197d51";
    //Redirect URI after Spotify grants/denies permission (specified on Spotify registration)
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // TODO TESTING NEED TO REMOVE
        FirebaseHelper.Initialize();
        FirebaseHelper.GetInstance().addNewUser("af123", "andrew", "andrew_photo");
    }

    public void spotifyLoginClicked(View V){
        final AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.d("MainActivity", "onActivityResult");
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            Log.d("MainActivity", "authResponse:" + response.toString());

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);

                    //WEB API CODE
                    SpotifyApi api = new SpotifyApi();

// Most (but not all) of the Spotify Web API endpoints require authorisation.
// If you know you'll only use the ones that don't require authorisation you can skip this step
                    api.setAccessToken(response.getAccessToken());

                    SpotifyService spotify = api.getService();

                    spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>() {
                        @Override
                        public void success(Album album, Response response) {
                            Log.d("Album success", album.name);
                            Toast.makeText(MainActivity.this, album.name, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Album failure", error.toString());
                        }
                    });

                    //SPOTIFY PLAYER CODE
                    mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                        //Initialize Spotify Player
                        @Override
                        public void onInitialized(Player player) {
                            mPlayer.addConnectionStateCallback(MainActivity.this);
                            mPlayer.addPlayerNotificationCallback(MainActivity.this);
                            //mPlayer.play("spotify:track:5bgYTzUDzerRFN7fp86MkQ");

                            // init db on successful login
                            //FirebaseHelper.Initialize();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                        }
                    });
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }




    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
//        Log.d("MainActivity", "Playback event received: " + playerState.name());
//        switch (playerState) {
//            // Handle event type as necessary
//            default:
//                break;
//        }
        Log.d("MainActivity", "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
//        Log.d("MainActivity", "Playback error received: " + error.name());
//        switch (error) {
//            // Handle error type as necessary
//            default:
//                break;
//        }
        Log.d("MainActivity", "Playback error received: " + errorType.name());
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
        setContentView(R.layout.login_page);
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

}
