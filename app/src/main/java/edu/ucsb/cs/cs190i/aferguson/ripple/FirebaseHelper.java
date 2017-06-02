package edu.ucsb.cs.cs190i.aferguson.ripple;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by shadeebarzin on 6/2/17.
 */

class FirebaseHelper {

    private static FirebaseHelper firebase;
    private DatabaseReference users;
    private DatabaseReference broadcasters;
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
        users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        broadcasters = firebase.getReference("broadcasters");
//        broadcasters.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        listeners = firebase.getReference("listeners");
//        listeners.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    void addNewUser(String userId, String name, String photo_url) {
        users.child(userId).child("name").setValue(name);
        users.child(userId).child("photo").setValue(photo_url);
    }

    void addNewBroadcaster(String userId) {

    }

    void addNewListener(String userId) {

    }

}
