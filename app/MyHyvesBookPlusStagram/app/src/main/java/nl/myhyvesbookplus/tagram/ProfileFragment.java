package nl.myhyvesbookplus.tagram;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import nl.myhyvesbookplus.tagram.controller.UploadClass;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected Button changePwdButton;
    protected ImageButton profilePicButton;
    protected StorageReference httpsReference;
    protected TextView profileName;
    protected ImageView profilePicture;
    protected FirebaseUser user;
    ProgressDialog progressDialog;


    /// Required empty public constructor ///

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Assigns all views.
     */
    protected void findViews(View view) {
        profilePicButton = (ImageButton) view.findViewById(R.id.profile_pic_button);
        profilePicture = (ImageView) view.findViewById(R.id.imageView_profile_picture);
        profileName = (TextView) view.findViewById(R.id.profile_name);
        changePwdButton = (Button) view.findViewById(R.id.change_psw_button);
        bindOnClick();
    }

    protected void bindOnClick() {
        profilePicButton.setOnClickListener(this);
        changePwdButton.setOnClickListener(this);
    }

    /// Setup ///

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(view);

        if (user != null && user.getPhotoUrl() != null) {
            httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhotoUrl().toString());
        }

        if (httpsReference != null) {
            Glide.with(this).using(new FirebaseImageLoader()).load(httpsReference).into(profilePicture);
        }

        profilePicture.invalidate(); // To display the profile picture after it has been updated.

        if (user != null && user.getDisplayName() != null) {
            profileName.setText(user.getDisplayName());
        }

        return view;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_pic_button:
                profilePicOnClick();
                break;
            case R.id.change_psw_button:
                changePwdOnClick();
                break;
        }
    }

    // TODO Make the function actually do something.

    /**
     * Performs profile picture change action.
     */
    public void profilePicOnClick() {
        dispatchTakePictureIntent();
    }

    /**
     * Starts new intent for access to the built-in camera of device.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Grabs the image just taken by the built-in camera and pushes this image to the user account.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), "Uploading...", true, false);
            UploadClass uploadClass = new UploadClass(getActivity());
            uploadClass.uploadProfilePicture(imageBitmap);
            progressDialog.dismiss();
        }
    }

    // TODO Make this function into its own class for modularity.

    /**
     * Performs password reset action.
     */
    public void changePwdOnClick() {
        if (user != null && user.getEmail() != null) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), task.isSuccessful()
                                            ? getString(R.string.mail_successful)
                                            : getString(R.string.mail_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // TODO Add code here for when there is no currently active user.
        }
    }
}
