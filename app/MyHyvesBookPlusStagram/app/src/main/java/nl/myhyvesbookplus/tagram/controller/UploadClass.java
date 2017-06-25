package nl.myhyvesbookplus.tagram.controller;

import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

/**
 * Class that does all the photo uploading things.
 */
public abstract class UploadClass {

    private static final String TAG = "UploadClass";
    StorageReference mStorageRef;
    DatabaseReference mDataRef;
    FirebaseUser mUser;

    UploadClass() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDataRef = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    /// Helpers ///

    byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    String getUserUid() {
        if (mUser != null) {
            return mUser.getUid();
        }
        return "";
    }
}
