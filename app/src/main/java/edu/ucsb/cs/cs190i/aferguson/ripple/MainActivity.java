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
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.CurrentlyPlaying;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;
//import retrofit.Callback;
//import retrofit.RequestInterceptor;
//import retrofit.RestAdapter;
//import retrofit.RetrofitError;
//import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements ConnectionStateCallback, PlayerNotificationCallback {

    //Client ID for individual user (must fetch from spotify)
    private static final String CLIENT_ID = "5aa6208c6cd54357ad6b55bd67197d51";
    //Redirect URI after Spotify grants/denies permission (specified on Spotify registration)
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;
    private SpotifyService spotify;

    private String accessToken;
    public static final String SPOTIFY_WEB_API_ENDPOINT = "https://api.spotify.com/v1";

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

        builder.setScopes(new String[]{"user-read-private", "user-read-currently-playing","streaming", "user-read-playback-state"});
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
                    accessToken = response.getAccessToken();
                    Config playerConfig = new Config(this, accessToken, CLIENT_ID);

                    //WEB API CODE
//                    aferguson uri: spotify:user:1255003849xxx
                    SpotifyApi api = new SpotifyApi();
                    api.setAccessToken(accessToken);

                    spotify = api.getService();


//                    RestAdapter restAdapter = new RestAdapter.Builder()
//                            .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
//                            .setRequestInterceptor(new RequestInterceptor() {
//                                @Override
//                                public void intercept(RequestFacade request) {
//                                    request.addHeader("Authorization", "Bearer " + accessToken);
//                                }
//                            })
//                            .build();
//
//                    SpotifyService spotify = restAdapter.create(SpotifyService.class);

                    spotify.getCurrentTrack(new Callback<CurrentlyPlaying>(){
                        @Override
                        public void success(CurrentlyPlaying currentTrack, Response response) {
                            if(currentTrack !=null) {
                                Log.d("restapi", "SUCCESS");
                                if (currentTrack != null) {
                                    Log.d("restapi", "timestamp: " + currentTrack.timestamp);
                                    Log.d("restapi", "progress_ms: " + currentTrack.progress_ms);
                                }
                            }
                            else
                                Log.d("restapi", "FAILURE");
//                            Toast.makeText(MainActivity.this, track.uri, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Track failure", error.toString());
                        }
                    });



//                    spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>() {
//                        @Override
//                        public void success(Album album, Response response) {
//                            Log.d("Album success", album.name);
//                            Toast.makeText(MainActivity.this, album.name, Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void failure(RetrofitError error) {
//                            Log.d("Album failure", error.toString());
//                        }
//                    });



                    //METHOD2

//                    final String accessToken = "myAccessToken";
//
//                    RestAdapter restAdapter = new RestAdapter.Builder()
//                            .setEndpoint(SPOTIFY_WEB_API_ENDPOINT + "/me/player/currently-playing")
//                            .setRequestInterceptor(new RequestInterceptor() {
//                                @Override
//                                public void intercept(RequestFacade request) {
//                                    request.addHeader("Authorization", "Bearer " + accessToken);
//                                }
//                            })
//                            .build();
//
//                    SpotifyService spotify = restAdapter.create(SpotifyService.class);

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

    public void getPlayerCurrentTrackAndTime(){
        mPlayer.getPlayerState(new PlayerStateCallback() {
            @Override
            public void onPlayerState(PlayerState playerState) {
                String currentTrackUri = playerState.trackUri;
                int currentPosition = playerState.positionInMs;
            }
        });
    }

}
