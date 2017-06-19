package nl.myhyvesbookplus.tagram;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements CameraFragment.OnFragmentInteractionListener {
    final static private String TAG = "MainScreen";

    FirebaseAuth mAuth;
    CameraFragment cameraFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case nl.myhyvesbookplus.tagram.R.id.navigation_timeline:
                    return true;
                case nl.myhyvesbookplus.tagram.R.id.navigation_camera:
                    return true;
                case nl.myhyvesbookplus.tagram.R.id.navigation_profile:
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

        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            goToLogin();
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void logOutOnClick(View view) {
        FirebaseAuth.getInstance().signOut();
        goToLogin();
        this.finish();
    }

    protected void goToLogin() {
        Intent goToLogIn = new Intent(this, LoginActivity.class);
        startActivity(goToLogIn);
    }
}
