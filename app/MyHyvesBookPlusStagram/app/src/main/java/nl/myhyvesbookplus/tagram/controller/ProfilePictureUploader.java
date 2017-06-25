package nl.myhyvesbookplus.tagram.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static java.lang.System.currentTimeMillis;

/**
 * Created by marijnjansen on 25/06/2017.
 */

public class ProfilePictureUploader extends UploadClass {

    final static private String TAG = "PPUploader";

    private Uri oldPicture;
    private ProfilePictureUpdatedListener mListener;

    public ProfilePictureUploader(Context context) {
        super();

        if (context instanceof ProfilePictureUpdatedListener) {
            mListener = (ProfilePictureUpdatedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ProfilePictureUpdatedListener");
        }
    }

    public void uploadProfilePicture(Bitmap picture) {
        byte[] uploadPhoto = bitmapToBytes(picture);
        oldPicture = mUser.getPhotoUrl();
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
