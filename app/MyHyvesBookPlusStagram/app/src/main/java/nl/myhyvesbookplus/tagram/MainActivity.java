package nl.myhyvesbookplus.tagram;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import nl.myhyvesbookplus.tagram.controller.UploadClass;
import nl.myhyvesbookplus.tagram.model.BitmapPost;

public class MainActivity extends AppCompatActivity implements CameraFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, TimelineFragment.OnFragmentInteractionListener, UploadClass.ProfilePictureUpdatedListener {
    final static private String TAG = "MainScreen";

    FirebaseAuth mAuth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case nl.myhyvesbookplus.tagram.R.id.navigation_timeline:
                    Log.d(TAG, "onNavigationItemSelected: Timeline");
                    TimelineFragment timeline = new TimelineFragment();
                    transaction.replace(R.id.content, timeline)
                            .addToBackStack(null)
                            .commit();
                    return true;

                case nl.myhyvesbookplus.tagram.R.id.navigation_camera:
                    Log.d(TAG, "onNavigationItemSelected: Camera");
                    CameraFragment camera = new CameraFragment();
                    transaction.replace(R.id.content, camera)
                            .addToBackStack(null)
                            .commit();
                    return true;

                case nl.myhyvesbookplus.tagram.R.id.navigation_profile:
                    Log.d(TAG, "onNavigationItemSelected: Profile");
                    ProfileFragment profile = new ProfileFragment();
                    transaction.replace(R.id.content, profile)
                            .addToBackStack(null)
                            .commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(nl.myhyvesbookplus.tagram.R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(nl.myhyvesbookplus.tagram.R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            goToLogin();
        }

        TimelineFragment fragment = new TimelineFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void logOutOnClick(View view) {
        FirebaseAuth.getInstance().signOut();
        goToLogin();
    }

    protected void goToLogin() {
        Intent goToLogIn = new Intent(this, LoginActivity.class);
        startActivity(goToLogIn);
        this.finish();
    }

    public void testCreatePost(View view) {
        UploadClass uploadClass = new UploadClass(this);
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ALPHA_8);
        BitmapPost bitmapPost = new BitmapPost(bitmap, "Dit is een Test!");
        uploadClass.uploadPicture(bitmapPost);
    }

    @Override
    public void ProfilePictureUpdated(Boolean success) {
        Log.d(TAG, "ProfilePictureUpdated: Ja ik luister naar je!");
        FragmentManager man = getFragmentManager();
        ProfileFragment frag = (ProfileFragment) man.findFragmentById(R.id.content);
        FragmentTransaction transaction = man.beginTransaction();
        transaction.detach(frag)
                .attach(frag)
                .commit();
        Log.d(TAG, "ProfilePictureUpdated: Done reloading fragment");
    }

}
