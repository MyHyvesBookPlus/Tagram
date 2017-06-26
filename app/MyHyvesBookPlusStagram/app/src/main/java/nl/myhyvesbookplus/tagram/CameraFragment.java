package nl.myhyvesbookplus.tagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import nl.myhyvesbookplus.tagram.controller.PostUploader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment implements PostUploader.PostUploadListener{
    // TODO: Rename parameter arguments, choose names that match
    private static final String TAG = "CameraFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Camera mCamera;
    private CameraPreview mPreview;
    private byte[] mPhotoRaw;
    private Bitmap mPhoto;
    private int facing = Camera.CameraInfo.CAMERA_FACING_BACK;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_camera, container, false);

        mCamera = getCameraInstance(facing);
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(0);
        mCamera.setParameters(params);

        mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera);
        final RelativeLayout pictureButtons = (RelativeLayout) view.findViewById(R.id.picture_taken_buttons);
        final RelativeLayout filterButtons = (RelativeLayout) view.findViewById(R.id.filter_buttons);
        final RelativeLayout mCameraLayout = (RelativeLayout) view.findViewById(R.id.camera_preview);
//        final RelativeLayout mImageTaken = (RelativeLayout) view.findViewById(R.id.picture_view);

        mCameraLayout.addView(mPreview);

        // Draw buttons over preview
        view.findViewById(R.id.picture_button).bringToFront();
        view.findViewById(R.id.switch_camera_button).bringToFront();
        pictureButtons.bringToFront();
        filterButtons.bringToFront();

        (view.findViewById(R.id.switch_camera_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFacing();

                mCameraLayout.removeView(mPreview);
                mCamera = getCameraInstance(facing);
                mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera);
                mCameraLayout.addView(mPreview);

                view.findViewById(R.id.picture_button).bringToFront();
                view.findViewById(R.id.switch_camera_button).bringToFront();
            }
        });

        (view.findViewById(R.id.picture_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, new PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bmp = rotate(BitmapFactory.decodeByteArray(data, 0, data.length, null), 90);
                        mPhoto = bmp;

                        PicturePreview mPicPreview = new PicturePreview(getActivity().getBaseContext(), mPhoto);
                        mPicPreview.setId(R.id.pic_preview);
                        Log.d(TAG, "onPictureTaken: PICTURE");

                        mCameraLayout.addView(mPicPreview);

                        filterButtons.setVisibility(View.VISIBLE);
                        filterButtons.bringToFront();

//                        mPicPreview.invalidate();
                        switchButtons(view);
                    }
                });
            }
        });

        (view.findViewById(R.id.upload_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPhoto.recycle();
                mPhoto = null;

                filterButtons.setVisibility(View.GONE);
                switchButtons(view);

                mCameraLayout.removeView(mPreview);

                mCamera = getCameraInstance(facing);
                Camera.Parameters params = mCamera.getParameters();
                params.setRotation(0);
                mCamera.setParameters(params);

                mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera);
                mCameraLayout.addView(mPreview);

                view.findViewById(R.id.picture_button).bringToFront();
                view.findViewById(R.id.switch_camera_button).bringToFront();

                mCameraLayout.removeView(view.findViewById(R.id.pic_preview));
            }
        });

        (view.findViewById(R.id.filter_left)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraLayout.removeView(view.findViewById(R.id.pic_preview));

                PicturePreview.filterPrev();

                PicturePreview mPicPreview = new PicturePreview(getActivity().getBaseContext(), mPhoto);
                mPicPreview.setId(R.id.pic_preview);

                mCameraLayout.addView(mPicPreview);

                view.findViewById(R.id.picture_taken_buttons).bringToFront();
                filterButtons.setVisibility(View.VISIBLE);
                filterButtons.bringToFront();
            }
        });

        (view.findViewById(R.id.filter_right)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraLayout.removeView(view.findViewById(R.id.pic_preview));

                PicturePreview.filterNext();

                PicturePreview mPicPreview = new PicturePreview(getActivity().getBaseContext(), mPhoto);
                mPicPreview.setId(R.id.pic_preview);

                mCameraLayout.addView(mPicPreview);

                view.findViewById(R.id.picture_taken_buttons).bringToFront();
                filterButtons.setVisibility(View.VISIBLE);
                filterButtons.bringToFront();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static Camera getCameraInstance(int facing) {
        Camera c = null;
        try {
            c = Camera.open(facing);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return c;
    }

    public void switchFacing() {
        if (facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
            facing = Camera.CameraInfo.CAMERA_FACING_BACK;
        else
            facing = Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    public void switchButtons(View view) {
        RelativeLayout pictureButtons = (RelativeLayout) view.findViewById(R.id.picture_taken_buttons);
        FloatingActionButton upload = (FloatingActionButton) view.findViewById(R.id.upload_button);
        FloatingActionButton save = (FloatingActionButton) view.findViewById(R.id.save_button);

        if (((Integer)upload.getVisibility()).equals(View.VISIBLE)) {
            upload.hide();
            save.hide();

            view.findViewById(R.id.picture_button).setVisibility(View.VISIBLE);
            view.findViewById(R.id.switch_camera_button).setVisibility(View.VISIBLE);
        } else {
            pictureButtons.bringToFront();
            upload.show();
            save.show();

            view.findViewById(R.id.picture_button).setVisibility(View.GONE);
            view.findViewById(R.id.switch_camera_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void PostUploadComplete(Boolean success) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
