package nl.myhyvesbookplus.tagram;

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

/**
 * Created by marijnjansen on 20/06/2017.
 */

public class UploadClass {

    private StorageReference mStorageRef;
    private DatabaseReference mDataRef;

    private static final String TAG = "UploadClass";
    private static Uri downloadUrl;

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

    /// Post Uploads ///

    public void uploadPicture(final BitmapPost post) {


        UploadTask uploadTask = mStorageRef.child("posts").child("UniquePostName").putBytes(bitmapToBytes(post.getBitmap()));
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
//                        putPostInDatabase(post.getUriPost(downloadUrl));
                    }
                });
    }

    private void putPostInDatabase(UriPost post) {
        DatabaseReference ref = mDataRef.child("posts").child("UniquePostName" + ".jpg"); // TODO: Naam voor post.
        ref.setValue(post) // FIXME: Grote boos veroorzaker
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Added post to database");
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException().getLocalizedMessage());
                        }
                        downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    }
                });
    }

    public Uri getDownloadUrl() {
        return downloadUrl;
    }

    private String getUserUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return "";
    }

    /// Profile picture ///

    protected void uploadProfilePicture(Bitmap picture) {
        byte[] uploadPhoto = bitmapToBytes(picture);
        UploadTask photoUpload = mStorageRef.child("profile").child(getUserUid()).putBytes(uploadPhoto);
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
