package nl.myhyvesbookplus.tagram.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import nl.myhyvesbookplus.tagram.model.UriPost;

/**
 * Created by marijnjansen on 23/06/2017.
 */

public class DownloadClass {
    private static final String TAG = "DownloadClass";
    private StorageReference mStorageRef;
    private DatabaseReference mDataRef;

    public DownloadClass() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDataRef = FirebaseDatabase.getInstance().getReference();
    }

    public UriPost[] getPosts() {
        UriPost[] posts = new UriPost[10];
        return posts;
    }
}
