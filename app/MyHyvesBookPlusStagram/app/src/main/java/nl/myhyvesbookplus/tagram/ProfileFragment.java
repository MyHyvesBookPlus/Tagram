package nl.myhyvesbookplus.tagram;

import android.app.Fragment;
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
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    final static private String TAG = "ProfileFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Uri downloadUrl;
    protected Button changePwdButton;
    protected ImageButton profilePicButton;

    /// Views ///
    protected StorageReference httpsReference;
    protected TextView profileName;
    protected ImageView profilePicture;
    protected FirebaseUser user;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    /// Required empty public constructor ///

    public ProfileFragment() {}


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhotoUrl().toString());
        }
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

        if (httpsReference != null) {
            Glide.with(this).using(new FirebaseImageLoader()).load(httpsReference).into(profilePicture);
        }

        if (user != null && user.getDisplayName() != null) {
            profileName.setText(user.getDisplayName());
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
//        Log.d(TAG, "profilePicOnClick: JE KAN NOG GEEN FOTO UPLOADEN");
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
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            UploadClass uploadClass = new UploadClass(getActivity());
            uploadClass.uploadProfilePicture(imageBitmap);
            profilePicture.invalidate();
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
                                    ? "An e-mail was sent, please follow its instructions."
                                    : "An error occurred, please check internet connection.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // TODO Add code here for when there is no currently active user.
        }
    }

    /**
     * Obligatory onAttach function included in fragments.
     * @param context provided context for the function to operate on.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Obligatory onDetach function included in fragments.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * See the Android Training lesson http://developer.android.com/training/basics/fragments/communicating.html
     * for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
