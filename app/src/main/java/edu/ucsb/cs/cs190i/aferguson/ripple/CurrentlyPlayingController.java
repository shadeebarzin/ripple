package edu.ucsb.cs.cs190i.aferguson.ripple;

import android.util.Log;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.CurrentlyPlaying;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Ferg on 6/5/17.
 */

public class CurrentlyPlayingController {
    private final SpotifyService mSpotifyService;

    public CurrentlyPlayingController(String accessToken){
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(accessToken);
        mSpotifyService = api.getService();
    }

    public void fetchCurrentlyPlaying(){
        mSpotifyService.getCurrentTrack(new Callback<CurrentlyPlaying>(){
            @Override
            //CurrentlyPlaying attributes: timestamp, progress_ms, item (current track), is_playing
            public void success(CurrentlyPlaying currentlyPlaying, Response response) {
                if(currentlyPlaying != null) {
                    updatedatabase();
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
    }

    private void updatedatabase(){
        //FB code
    }
}
