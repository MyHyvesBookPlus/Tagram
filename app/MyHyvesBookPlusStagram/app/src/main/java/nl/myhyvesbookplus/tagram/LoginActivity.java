package nl.myhyvesbookplus.tagram;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "Login";

    /// Views ///

    protected EditText emailField, usernameField, passwordField;
    protected TextView passwordConfirmField, passwordConfirmLabel, usernameLabel;
    protected Button registerButton, backToLoginButton, goToRegisterButton, logInButton;

    protected FirebaseAuth mAuth;

    /// Setup ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(nl.myhyvesbookplus.tagram.R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        findViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Assigns all views.
     */
    protected void findViews() {
        passwordConfirmLabel = (TextView) findViewById(R.id.confirm_password_label);
        usernameLabel = (TextView) findViewById(R.id.username_label);

        registerButton = (Button) findViewById(R.id.register_button);
        backToLoginButton = (Button) findViewById(R.id.back_to_login_button);
        goToRegisterButton = (Button) findViewById(R.id.go_to_register_button);
        logInButton = (Button) findViewById(R.id.login_button);

        passwordConfirmField = (EditText) findViewById(R.id.confirm_password_field);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);
        emailField = (EditText) findViewById(R.id.email);
    }

    /// OnClick ///

    /**
     * Performs the logon action.
     *
     * @param view
     */
    public void logInOnClick(View view) {
        String emailSting = emailField.getText().toString();
        String passwordSting = passwordField.getText().toString();

        logIn(emailSting, passwordSting);
    }

    /**
     * Performs the register action.
     * @param view
     */
    public void registerOnClick(View view) {
        Log.d(TAG, "registerOnClick: ");

        if (passwordField.getText().toString().equals(passwordConfirmField.getText().toString())) {
            registerUser(emailField.getText().toString(), passwordField.getText().toString());
        } else {
            Toast.makeText(LoginActivity.this, "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "registerOnClick: Passwords do not match");
        }

    }

    /// UI-changes ///

    /**
     * Changes the Activity for registering.
     * @param view
     */
    public void goToRegisterOnClick(View view) {
        passwordConfirmField.setVisibility(View.VISIBLE);
        passwordConfirmLabel.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.VISIBLE);
        backToLoginButton.setVisibility(View.VISIBLE);
        usernameField.setVisibility(View.VISIBLE);
        usernameLabel.setVisibility(View.VISIBLE);

        goToRegisterButton.setVisibility(View.GONE);
        logInButton.setVisibility(View.GONE);
    }

    /**
     * Changes the Activity for logging in.
     * @param view
     */
    public void backToLoginOnClick(View view) {
        passwordConfirmField.setVisibility(View.GONE);
        passwordConfirmLabel.setVisibility(View.GONE);
        registerButton.setVisibility(View.GONE);
        backToLoginButton.setVisibility(View.GONE);
        usernameField.setVisibility(View.GONE);
        usernameLabel.setVisibility(View.GONE);

        goToRegisterButton.setVisibility(View.VISIBLE);
        logInButton.setVisibility(View.VISIBLE);
    }

    /**
     * Performs intend to Main screen.
     */
    protected void goToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    /// FireBase ///

    /**
     * Performs the actual login action.
     * @param emailSting email address
     * @param passwordSting the entered password
     */
    protected void logIn(String emailSting, String passwordSting) {
        mAuth.signInWithEmailAndPassword(emailSting, passwordSting)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "onComplete: isVerified " + user.isEmailVerified());
                            user.isEmailVerified();
                            goToMainScreen();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Performs the actual register action
     * @param email Users email address
     * @param password the entered password
     */
    protected void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUserInfo(user);
                            goToMainScreen();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            if (task.getException() != null) {
                                Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    /**
     * Saves the Username to Firebase
     * @param user The User object that needs to be updated
     */
    protected void updateUserInfo(final FirebaseUser user) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(usernameField.getText().toString())
                .build();

        user.updateProfile(request)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                        sendConfirmEmail(user);
                    }
                });
    }

    /**
     * Sends a confirm email
     * @param user The User object which the mail needs to be send to
     */
    protected void sendConfirmEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }
}
