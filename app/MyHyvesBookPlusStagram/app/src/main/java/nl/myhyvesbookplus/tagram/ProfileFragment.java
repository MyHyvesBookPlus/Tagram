package nl.myhyvesbookplus.tagram;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.io.File;
import java.io.IOException;

import nl.myhyvesbookplus.tagram.controller.DownloadClass;
import nl.myhyvesbookplus.tagram.controller.ProfilePictureUploader;

import static android.app.Activity.RESULT_OK;

/**
 * Profilefragment which holds the personal info of the user.
 * Makes use of ProfileAdapter in order to load in the items for ListView.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {
    static final int REQUEST_TAKE_PHOTO = 1;
    ProgressDialog progressDialog;

    /* Views, buttons and other protected and private inits */
    protected Button changePwdButton;
    protected ImageButton profilePicButton;
    protected StorageReference httpsReference;
    protected TextView profileName;
    protected ImageView profilePicture;
    protected FirebaseUser user;
    protected File photoFile;

    private ListView listView;
    private DownloadClass downloadClass;
    private View headerInflater;
    private View timeLineInflater;
    private ProgressBar progressBar;

    /* Required empty public constructor */
    public ProfileFragment() {}

    /**
     * Overridden onCreate which initializes a user and sets the default photoFile to null.
     * @param savedInstanceState The standard return of the onCreate method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        photoFile = null;
     }

    /**
     * Assigns all views and buttons for the header.
     */
    protected void findHeaderViews() {
        profilePicButton = headerInflater.findViewById(R.id.profile_pic_button);
        profilePicture = headerInflater.findViewById(R.id.imageView_profile_picture);
        profileName = headerInflater.findViewById(R.id.profile_name);
        changePwdButton = headerInflater.findViewById(R.id.change_psw_button);
        bindOnClick();
    }

    /**
     * Assign the ListView and add the header to it.
     */
    protected void findTimelineViews() {
        listView = timeLineInflater.findViewById(R.id.list);
        listView.addHeaderView(headerInflater);
    }

    /**
     * Bind the buttons to their listeners.
     */
    protected void bindOnClick() {
        profilePicButton.setOnClickListener(this);
        changePwdButton.setOnClickListener(this);
    }

    /**
     * Overridden onCreateView which serves as a fragment content creator.
     * Checks for user data to be displayed.
     *
     * @param inflater The inflater used for the fragment.
     * @param container The container which holds this fragment.
     * @param savedInstanceState The state which was provided by onCreate.
     * @return the timeLineInflater View which is required for the ListView to be updated.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        timeLineInflater = inflater.inflate(R.layout.fragment_profile_timeline, container, false);
        headerInflater = inflater.inflate(R.layout.fragment_profile_header, listView, false);
        progressBar = timeLineInflater.findViewById(R.id.progressbar_timeline);
        progressBar.setVisibility(View.VISIBLE);
        findHeaderViews();
        findTimelineViews();

        profilePicture.invalidate();

        if (user != null) {
            if(user.getPhotoUrl() != null) {
                httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhotoUrl().toString());
            }

            if (user.getDisplayName() != null) {
                profileName.setText(user.getDisplayName());
            }
        }

        if (httpsReference != null) {
            GlideApp.with(this)
                    .load(httpsReference)
                    .into(profilePicture);
        }

        downloadClass = new DownloadClass(getActivity());
        downloadClass.getPostsFromServer();
        return timeLineInflater;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_pic_button:
                profilePicOnClick();
                break;
            case R.id.change_psw_button:
                changePwdOnClick();
                break;
        }
    }

    /**
     * Starts new intent for access to the built-in camera of device.
     */
    private void profilePicOnClick() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getActivity(), getString(R.string.image_save_error),
                                Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "nl.myhyvesbookplus.tagram.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Start display of the list; uses an adapter and listener in the main activity.
     */
    public void startList() {
        ProfileAdapter adapter = new ProfileAdapter(getActivity(), downloadClass.getOwnPosts());
        listView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
     }

    /**
     * Grabs the image just taken by the built-in camera and pushes this image to the user account.
     *
     * @param requestCode The code which corresponds to REQUEST_TAKE_PHOTO. Used as indicator.
     * @param resultCode Code should be RESULT_OK to allow camera to proceed.
     * @param data The image data from the camera.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), getString(R.string.upload_profile_pic), false, false);
            ProfilePictureUploader profilePictureUploader = new ProfilePictureUploader(getActivity());
            profilePictureUploader.uploadProfilePicture(photoFile);
        }
    }

    /**
     * Create the file which the camera requires to save a proper quality picture to.
     *
     * @return The new file.
     * @throws IOException when insufficient permission or storage available.
     */
    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + user.getUid();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        }

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
        }
    }
}


