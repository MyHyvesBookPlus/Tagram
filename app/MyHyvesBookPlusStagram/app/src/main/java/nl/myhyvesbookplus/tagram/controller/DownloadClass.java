package nl.myhyvesbookplus.tagram.controller;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import nl.myhyvesbookplus.tagram.model.UriPost;

/**
 * Created by marijnjansen on 23/06/2017.
 */

public class DownloadClass {
    private static final String TAG = "DownloadClass";
    private DatabaseReference mDataRef;
    private ArrayList<UriPost> mList;
    private PostDownloadListener mListener;

    public DownloadClass(Context context) {
        if (context instanceof DownloadClass.PostDownloadListener) {
            mListener = (PostDownloadListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PostDownloadListener");
        }
        mDataRef = FirebaseDatabase.getInstance().getReference();
        mList = new ArrayList<>();
    }

    public void getPostsFromServer() {
        mDataRef.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    UriPost tempPost = data.getValue(UriPost.class);
                    tempPost.setDatabaseEntryName(data.getKey());
                    mList.add(tempPost);
                }
                Collections.reverse(mList);
                mListener.PostDownloaded();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getDetails() + databaseError.getMessage());
            }
        });
    }

    public ArrayList<UriPost> getmList() {
        return mList;
    }

    public ArrayList<UriPost> getOwnPosts() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<UriPost> posts = new ArrayList<UriPost>();

        for (UriPost post : mList) {
            if (post.getPoster().equals(currentUid)) {
                posts.add(post);
            }
        }
        return posts;
    }

    public interface PostDownloadListener {
        void PostDownloaded();
    }
}
