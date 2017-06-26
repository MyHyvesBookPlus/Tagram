package nl.myhyvesbookplus.tagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "Login";

    /// Views ///

    protected EditText emailField, usernameField, passwordField;
    protected TextView passwordConfirmField, passwordConfirmLabel, usernameLabel;
    protected Button registerButton, backToLoginButton, goToRegisterButton, logInButton;

    protected FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    /// Setup ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(nl.myhyvesbookplus.tagram.R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        findViews();
        bindOnClick();
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

    protected void bindOnClick() {
        registerButton.setOnClickListener(this);
        backToLoginButton.setOnClickListener(this);
        goToRegisterButton.setOnClickListener(this);
        logInButton.setOnClickListener(this);
    }

    /// OnClick ///

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                registerOnClick();
                break;
            case R.id.go_to_register_button:
                goToRegisterOnClick();
                break;
            case R.id.login_button:
                logInOnClick();
                break;
            case R.id.back_to_login_button:
                backToLoginOnClick();
                break;
        }
    }

    /**
     * Performs the logon action.
     */
    public void logInOnClick() {
        String emailString = emailField.getText().toString();
        String passwordString = passwordField.getText().toString();

        if (!emailString.isEmpty() && !passwordString.isEmpty()) {
            logIn(emailString, passwordString);
        } else {
            Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Performs the register action.
     */
    public void registerOnClick() {
        String emailString = emailField.getText().toString();
        String usernameString = usernameField.getText().toString();
        String passwordString = passwordField.getText().toString();
        String passwordConfirmString = passwordConfirmField.getText().toString();

        if (!emailString.isEmpty() && !usernameString.isEmpty()
                && !passwordString.isEmpty() && !passwordConfirmString.isEmpty()) {
            if (passwordField.getText().toString().equals(passwordConfirmField.getText().toString())) {
                registerUser(emailField.getText().toString(), passwordField.getText().toString());
            } else {
                Toast.makeText(LoginActivity.this, R.string.password_match_error,
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "registerOnClick: Passwords do not match");
            }
        } else {
            Toast.makeText(LoginActivity.this, R.string.register_error, Toast.LENGTH_SHORT).show();
        }
    }

    /// UI-changes ///

    /**
     * Changes the Activity for registering.
     */
    public void goToRegisterOnClick() {
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
     */
    public void backToLoginOnClick() {
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
        progressDialog.dismiss();
        this.finish();
    }

    /// FireBase ///

    /**
     * Performs the actual login action.
     * @param emailString email address
     * @param passwordString the entered password
     */
    protected void logIn(String emailString, String passwordString) {
        progressDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.please_wait), "Logging in", true, false);

        mAuth.signInWithEmailAndPassword(emailString, passwordString)
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
                            progressDialog.dismiss();
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
        this.progressDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.please_wait), "Registering", true, false);
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
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    //TODO Make modular for use with Profile fragment.
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
