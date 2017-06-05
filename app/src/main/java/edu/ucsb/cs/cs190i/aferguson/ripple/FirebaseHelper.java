package edu.ucsb.cs.cs190i.aferguson.ripple;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shadeebarzin on 6/2/17.
 */

class FirebaseHelper {

    private static FirebaseHelper firebase;
    private DatabaseReference users;
    private DatabaseReference broadcasts;
    private DatabaseReference listeners;


    // call this method on successful login
    static void Initialize() {
        if (firebase == null) firebase = new FirebaseHelper();
    }

    // use this method when want to add or read from db in any Activity/Fragment/etc.
    static FirebaseHelper GetInstance() {
        return firebase;
    }


    private FirebaseHelper() {
        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        users = fb.getReference("users");
        broadcasts = fb.getReference("broadcasts");
    }

    // user userId should be spotify client userId
    // client userId -> { name: spotify user's name, photo: profile photo url }
    void addUser(User user) {
//        users.child(user.getUserId()).child("name").setValue(user.getName());
//        users.child(user.getUserId()).child("photo").setValue(user.getPhotoUrl());
        users.child(user.getUserId()).setValue(user);
    }

    void addUser(String userId, String name, String photo_url) {
        users.child(userId).child("name").setValue(name);
        users.child(userId).child("photo_url").setValue(photo_url);
    }

    void deleteUser(User user) { users.child(user.getUserId()).removeValue(); }

    void deleteUser(String userId) { users.child(userId).removeValue(); }

    void getUsers(final List<User> userList) {
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                        Log.d("getUsers", user.toString());
                        userList.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("getUsers", "db error: " + databaseError.getMessage());
            }
        });
    }


    // broadcaster's spotify client userId -> list of listeners
    void addBroadcast(User broadcaster) {
        Broadcast bc = new Broadcast(broadcaster.getUserId());
        broadcasts.child(broadcaster.getUserId()).setValue(bc);
    }

    void addBroadcast(String broadcasterId) {
        Broadcast bc = new Broadcast(broadcasterId);
        broadcasts.child(broadcasterId).setValue(bc);
    }

    void deleteBroadcast(User broadcaster) {
        broadcasts.child(broadcaster.getUserId()).removeValue();
    }

    void deleteBroadcast(String broadcasterId) {
        broadcasts.child(broadcasterId).removeValue();
    }

    void getBroadcasts(final List<Broadcast> broadcastList) {
        broadcasts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Broadcast bc = ds.getValue(Broadcast.class);
                        Log.d("getBroadcasts", bc.toString());
                        broadcastList.add(bc);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("getBroadcasts", "db error: " + databaseError.getMessage());
            }
        });
    }


    // broadcaster's spotify client userId -> add listener id to list of listeners
    void addListener(User listener, User broadcaster) {
        broadcasts.child(broadcaster.getUserId()).child("listeners").child(listener.getUserId()).setValue("true");
    }

    void addListener(String listenerId, String broadcasterId) {
        broadcasts.child(broadcasterId).child("listeners").child(listenerId).setValue("true");
    }

    void deleteListener(User listener, User broadcaster) {
        broadcasts.child(broadcaster.getUserId()).child("listeners").child(listener.getUserId()).removeValue();
    }

    void deleteListener(String listenerId, String broadcasterId) {
        broadcasts.child(broadcasterId).child("listeners").child(listenerId).removeValue();
    }

}
