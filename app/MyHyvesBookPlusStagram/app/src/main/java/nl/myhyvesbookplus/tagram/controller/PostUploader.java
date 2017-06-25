package nl.myhyvesbookplus.tagram.controller;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;

import nl.myhyvesbookplus.tagram.model.BitmapPost;
import nl.myhyvesbookplus.tagram.model.UriPost;

import static java.lang.System.currentTimeMillis;

/**
 * Created by marijnjansen on 25/06/2017.
 */

public class PostUploader extends UploadClass {

    final private static String TAG = "PostUploader";

    private PostUploadListener mListener;

    public PostUploader(Context context) {
        super();
        if (context instanceof PostUploadListener) {
            mListener = (PostUploadListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PostUploadListener");
        }
    }

    public void uploadPicture(final BitmapPost post) {
        final String name = getUserUid() + currentTimeMillis();

        UploadTask uploadTask = mStorageRef.child("posts").child(name + ".jpg").putBytes(bitmapToBytes(post.getBitmap()));
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Upload Failed");
            }
        })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads on complete
                        Log.d(TAG, "onSuccess: Upload Success!");
                        Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                        putPostInDatabase(post.getUriPost(downloadUrl), name);
                    }
                });
    }

    private void putPostInDatabase(UriPost post, String name) {
        DatabaseReference ref = mDataRef.child("posts").child(name);
        ref.setValue(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Added post to database");
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    public interface PostUploadListener {
        void PostUploadComplete(Boolean success);
    }
}
