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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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

public class ProfileFragment extends Fragment implements View.OnClickListener {
    static final int REQUEST_TAKE_PHOTO = 1;

    /// Views, buttons and other protected declarations ///
    protected Button changePwdButton;
    protected ImageButton profilePicButton;
    protected StorageReference httpsReference;
    protected TextView profileName;
    protected ImageView profilePicture;
    protected FirebaseUser user;
    protected File photoFile = null;
    private ListView listView;
    private DownloadClass downloadClass;

    ProgressDialog progressDialog;

    /// Required empty public constructor ///

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        loadPersonalPosts();
    }

    /**
     * Assigns all views and buttons.
     */
    protected void findViews(View view) {
        profilePicButton = (ImageButton) view.findViewById(R.id.profile_pic_button);
        profilePicture = (ImageView) view.findViewById(R.id.imageView_profile_picture);
        profileName = (TextView) view.findViewById(R.id.profile_name);
        changePwdButton = (Button) view.findViewById(R.id.change_psw_button);
        bindOnClick();
    }

    /**
     * Bind the buttons to their listeners.
     */
    protected void bindOnClick() {
        profilePicButton.setOnClickListener(this);
        changePwdButton.setOnClickListener(this);
    }

    /// Page setup ///

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewTimeline = inflater.inflate(R.layout.fragment_profile_timeline, container, false);



        listView = (ListView) viewTimeline.findViewById(R.id.listview_profile);
        View viewHeader = inflater.inflate(R.layout.fragment_profile_header, listView, false);
        findViews(viewHeader);
        listView.addHeaderView(viewHeader);

        if (user != null) {
            if(user.getPhotoUrl() != null) {
                httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhotoUrl().toString());
            }

            if (user.getDisplayName() != null) {
                profileName.setText(user.getDisplayName());
            }
        }

        if (httpsReference != null) {
            Glide.with(this).using(new FirebaseImageLoader()).load(httpsReference).into(profilePicture);
        }

        profilePicture.invalidate();

        downloadClass = new DownloadClass(getActivity());
        downloadClass.getPostsFromServer();

        return viewTimeline;
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

    /**
     * Starts new intent for access to the built-in camera of device.
     */
    private void profilePicOnClick() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /* Ensure that there's a camera activity to handle the intent */
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            /* Create the File where the photo should go */
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getActivity(), getString(R.string.image_save_error), Toast.LENGTH_LONG).show();
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

    public void startList() {
        ProfileAdapter adapter = new ProfileAdapter(getActivity(), downloadClass.getmList());
        if (listView != null) {
            listView.setAdapter(adapter);
        } else {
            Log.d("Jemoeder", "startList: Halloooooooo");
        }
            
//        listView.addHeaderView(adapter);
    }

    /**
     * Grabs the image just taken by the built-in camera and pushes this image to the user account.
     * @param requestCode The code which corresponds to REQUEST_TAKE_PHOTO. Used as indicator.
     * @param resultCode Code should be RESULT_OK to allow camera to proceed.
     * @param data The image data from the camera.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), getString(R.string.upload_profile_pic), false, false);
            ProfilePictureUploader profilePictureUploader = new ProfilePictureUploader(getActivity());
            profilePictureUploader.uploadProfilePicture(photoFile.getAbsoluteFile());
        }
    }

    public void loadPersonalPosts() {

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + user.getUid();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
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
