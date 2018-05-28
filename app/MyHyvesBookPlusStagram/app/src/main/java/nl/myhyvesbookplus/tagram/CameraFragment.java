package nl.myhyvesbookplus.tagram;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import nl.myhyvesbookplus.tagram.controller.PostUploader;
import nl.myhyvesbookplus.tagram.model.BitmapPost;

public class CameraFragment extends Fragment implements PostUploader.PostUploadListener{
    private static final String TAG = "CameraFragment";
    private Camera mCamera;
    private CameraPreview mPreview;
    private Bitmap mPhoto;
    private int facing = Camera.CameraInfo.CAMERA_FACING_BACK;

    /* Required empty public constructor */
    public CameraFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_camera, container, false);

        final RelativeLayout filterButtons = view.findViewById(R.id.filter_buttons);
        final RelativeLayout mCameraLayout = view.findViewById(R.id.camera_preview);
        final LinearLayout commentBox = view.findViewById(R.id.comment_box);
        final ImageButton pictureButton = view.findViewById(R.id.picture_button);
        final ImageButton switchButton = view.findViewById(R.id.switch_camera_button);

        // Hide the action bar
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        mCamera = getCameraInstance(facing);

        mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera);

        mCameraLayout.addView(mPreview);

        // Draw initial buttons over preview
        pictureButton.bringToFront();
        switchButton.bringToFront();
        filterButtons.bringToFront();

        /* Upon pressing the switch camera facing button: */
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFacing();

                mCameraLayout.removeView(mPreview);
                mCamera = getCameraInstance(facing);

                mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera);
                mCameraLayout.addView(mPreview);

                pictureButton.bringToFront();
                switchButton.bringToFront();
            }
        });

         /* Upon pressing the take photo button: */
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, new PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        mPhoto = BitmapFactory.decodeByteArray(data, 0, data.length, null);

                        PicturePreview mPicPreview = new PicturePreview(getActivity().getBaseContext(), mPhoto, facing);
                        mPicPreview.setId(R.id.pic_preview);

                        mCameraLayout.addView(mPicPreview);

                        filterButtons.setVisibility(View.VISIBLE);
                        filterButtons.bringToFront();

                        switchButtons(view);
                    }
                });
            }
        });

        /* Upon pressing the upload button: */
        (view.findViewById(R.id.upload_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentBox.setClickable(true);
                commentBox.setVisibility(View.VISIBLE);
                commentBox.bringToFront();
                filterButtons.setVisibility(View.GONE);
                ((FloatingActionButton)view.findViewById(R.id.upload_button)).hide();
            }
        });

        /* Upon pressing the enter button on the virtual keyboard: */
        (view.findViewById(R.id.comment_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mComment = view.findViewById(R.id.comment_text);

                String comment = mComment.getText().toString();
                mComment.setText("");

                PostUploader upload = new PostUploader(getActivity());
                upload.uploadPicture(new BitmapPost(((PicturePreview)view.findViewById(R.id.pic_preview)).getPicture(), comment));

                mPhoto.recycle();

                filterButtons.setVisibility(View.GONE);
                switchButtons(view);

                mCameraLayout.removeView(mPreview);

                mCamera = getCameraInstance(facing);

                mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera);
                mCameraLayout.addView(mPreview);

                pictureButton.bringToFront();
                switchButton.bringToFront();

                mCameraLayout.removeView(view.findViewById(R.id.pic_preview));
                hideKeyboard();
            }
        });

        /* Upon pressing the cancel button: */
        (view.findViewById(R.id.comment_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) view.findViewById(R.id.comment_text)).setText("");

                mPhoto.recycle();

                filterButtons.setVisibility(View.GONE);
                switchButtons(view);

                mCameraLayout.removeView(mPreview);

                mCamera = getCameraInstance(facing);

                mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera);
                mCameraLayout.addView(mPreview);

                pictureButton.bringToFront();
                switchButton.bringToFront();

                mCameraLayout.removeView(view.findViewById(R.id.pic_preview));
                hideKeyboard();
            }
        });

        /* Upon pressing the left arrow filter change button: */
        (view.findViewById(R.id.filter_left)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraLayout.removeView(view.findViewById(R.id.pic_preview));

                PicturePreview.filterPrev();

                PicturePreview mPicPreview = new PicturePreview(getActivity().getBaseContext(), mPhoto, facing);
                mPicPreview.setId(R.id.pic_preview);

                mCameraLayout.addView(mPicPreview);

                view.findViewById(R.id.upload_button).bringToFront();
                filterButtons.setVisibility(View.VISIBLE);
                filterButtons.bringToFront();
            }
        });

        /* Upon pressing the right arrow filter change button: */
        (view.findViewById(R.id.filter_right)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraLayout.removeView(view.findViewById(R.id.pic_preview));

                PicturePreview.filterNext();

                PicturePreview mPicPreview = new PicturePreview(getActivity().getBaseContext(), mPhoto, facing);
                mPicPreview.setId(R.id.pic_preview);

                mCameraLayout.addView(mPicPreview);

                view.findViewById(R.id.upload_button).bringToFront();
                filterButtons.setVisibility(View.VISIBLE);
                filterButtons.bringToFront();
            }
        });

        return view;
    }

    /**
     * Hides keyboard after submit, upload or cancel button gets pressed.
     */
    public void hideKeyboard() {
        ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    /**
     * Restores the action bar when exiting the fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    /**
     * Start the camera.
     * @param facing The direction in which the camera should be initialized (back by default).
     * @return the result of the opened camera, if successful.
     */
    public static Camera getCameraInstance(int facing) {
        Camera c = null;
        try {
            c = Camera.open(facing);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return c;
    }


    /**
     * Switch between front facing camera and the back camera.
     */
    public void switchFacing() {
       facing = facing == Camera.CameraInfo.CAMERA_FACING_FRONT ?
       Camera.CameraInfo.CAMERA_FACING_BACK :
       Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    /**
     * Change which buttons are visible during the different stages on the camera fragment.
     *
     * @param view The current view upon which the buttons need to be placed or removed.
     */
    public void switchButtons(View view) {
        FloatingActionButton upload = view.findViewById(R.id.upload_button);
        ImageButton picButton = view.findViewById(R.id.picture_button);
        ImageButton switchButton = view.findViewById(R.id.switch_camera_button);

        if (((Integer)picButton.getVisibility()).equals(View.GONE)) {
            Log.d(TAG, "switchButtons: GONE");
            upload.hide();

            picButton.setVisibility(View.VISIBLE);
            switchButton.setVisibility(View.VISIBLE);

            picButton.bringToFront();
            switchButton.bringToFront();
        } else {
            Log.d(TAG, "switchButtons: VISIBLE");
            upload.bringToFront();
            upload.show();

            picButton.setVisibility(View.GONE);
            switchButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void PostUploadComplete(Boolean success) {

    }
}
