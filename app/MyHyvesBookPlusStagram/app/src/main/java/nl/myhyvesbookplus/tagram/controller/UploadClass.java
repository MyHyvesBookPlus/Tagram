package nl.myhyvesbookplus.tagram.controller;

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

    public UploadClass() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDataRef = FirebaseDatabase.getInstance().getReference();
    }

    /// Helpers ///

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private String getUserUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
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

    public void uploadProfilePicture(Bitmap picture) {
        byte[] uploadPhoto = bitmapToBytes(picture);
        UploadTask photoUpload = mStorageRef.child("profile").child(getUserUid() + "_" + currentTimeMillis()).putBytes(uploadPhoto);
        photoUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                updateProfilePictureInUser(downloadUrl);
            }
        });
    }

    private void updateProfilePictureInUser(Uri url) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(url)
                .build();
        user.updateProfile(request)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: Updated profile picture");
                    }
                });
    }
}
