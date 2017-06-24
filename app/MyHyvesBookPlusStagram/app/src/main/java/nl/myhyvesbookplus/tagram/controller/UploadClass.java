package nl.myhyvesbookplus.tagram.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import nl.myhyvesbookplus.tagram.model.BitmapPost;
import nl.myhyvesbookplus.tagram.model.UriPost;

import static java.lang.System.currentTimeMillis;

/**
 * Class that does all the photo uploading things.
 */
public class UploadClass {

    private static final String TAG = "UploadClass";
    private StorageReference mStorageRef;
    private DatabaseReference mDataRef;
    private ProfilePictureUpdatedListener mListener;
    private FirebaseUser mUser;
    private Uri oldPicture;

    public UploadClass(Context context) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDataRef = FirebaseDatabase.getInstance().getReference();
        mListener = (ProfilePictureUpdatedListener) context;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    /// Helpers ///

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private String getUserUid() {
        if (mUser != null) {
            return mUser.getUid();
        }
        return "";
    }

    /// Post Uploads ///

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

    /// Profile picture ///

    public void uploadProfilePicture(File picture) {
        try {
            FileInputStream stream = new FileInputStream(picture);
//        Uri uri = Uri.fromFile(picture);
            Log.d(TAG, "uploadProfilePicture: This is the uri:" + stream.toString());
            oldPicture = mUser.getPhotoUrl();

            UploadTask photoUpload = mStorageRef.child("profile").child(getUserUid() + "_" + currentTimeMillis()).putStream(stream);
            photoUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    updateProfilePictureInUser(downloadUrl);
                }
            });
        } catch (FileNotFoundException fnfe) {
            Log.d(TAG, "uploadProfilePicture: FIle niet gevonden");
        }
    }

    private void updateProfilePictureInUser(Uri url) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(url)
                .build();
        mUser.updateProfile(request)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: Updated profile picture");
                        mListener.ProfilePictureUpdated(true);
                        removeOldPicture();
                    }
                });
    }

    private void removeOldPicture() {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(oldPicture.toString());
        ref.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Delete successfull");
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    public interface ProfilePictureUpdatedListener {
        void ProfilePictureUpdated(Boolean success);
    }
}
