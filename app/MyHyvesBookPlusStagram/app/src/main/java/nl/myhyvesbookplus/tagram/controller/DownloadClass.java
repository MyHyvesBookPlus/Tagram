package nl.myhyvesbookplus.tagram.controller;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import nl.myhyvesbookplus.tagram.model.UriPost;

/**
 * Created by marijnjansen on 23/06/2017.
 */

public class DownloadClass {
    private static final String TAG = "DownloadClass";
    //    private StorageReference mStorageRef;
    private DatabaseReference mDataRef;

    public DownloadClass() {
//        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDataRef = FirebaseDatabase.getInstance().getReference();
    }

    public UriPost[] getPosts() {
        UriPost[] posts = new UriPost[10];
        getPostsFromServer().toArray(posts);

        return posts;
    }

    private ArrayList<UriPost> getPostsFromServer() {
        Log.d(TAG, "getPostsFromServer: Begin of function");
        final ArrayList<UriPost> list = new ArrayList<>();
        mDataRef.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    list.add(data.getValue(UriPost.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getDetails() + databaseError.getMessage());
            }
        });
        return list;
    }
}
