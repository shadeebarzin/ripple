package edu.ucsb.cs.cs190i.aferguson.ripple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ConnectionStateCallback, PlayerNotificationCallback {

    //Client ID for individual user (must fetch from spotify)
    private static final String CLIENT_ID = "5aa6208c6cd54357ad6b55bd67197d51";
    //Redirect URI after Spotify grants/denies permission (specified on Spotify registration)
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;
    private List<User> users = new ArrayList<>();
    private List<Broadcast> broadcasts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        Log.d("onCreate", "initing DB");
        // TODO TESTING NEED TO REMOVE
        FirebaseHelper.Initialize();
        FirebaseHelper.GetInstance().getUsers(users);
        FirebaseHelper.GetInstance().getBroadcasts(broadcasts);


//        FirebaseHelper.GetInstance().addUser(new User("sb123", "shadee", "shadee_photo"));
//        FirebaseHelper.GetInstance().addUser(new User("af123", "andrew", "andrew_photo"));
//        FirebaseHelper.GetInstance().addUser(new User("pw123", "peter", "peter_photo"));

//        FirebaseHelper.GetInstance().addBroadcast("af123");
//        FirebaseHelper.GetInstance().addListener("sb123", "pw123");

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
                    mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                        @Override
                        public void onInitialized(Player player) {
                            mPlayer.addConnectionStateCallback(MainActivity.this);
                            mPlayer.addPlayerNotificationCallback(MainActivity.this);
                            mPlayer.play("spotify:track:5bgYTzUDzerRFN7fp86MkQ");

                            // init db on successful login
                            FirebaseHelper.Initialize();
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
        //setContentView(R.layout.activity_main);
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
        //setContentView(R.layout.login_page);
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
