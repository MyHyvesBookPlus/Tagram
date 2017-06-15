package nl.myhyvesbookplus.tagram;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(nl.myhyvesbookplus.tagram.R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
    }

    private void registerUser(String email, String password) {
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
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void logInOnClick(View view) {
        mAuth.signOut();
        EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        String emailSting = email.getText().toString();
        String passwordSting = password.getText().toString();

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

//                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
//                    updateUI(null);
                }

                // ...
            }
        });


    }

    public void registerOnClick(View view) {
        // goto Register page.
        Log.d(TAG, "registerOnClick: ");
        EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        EditText passwordConfirm = (EditText) findViewById(R.id.confirm_password_field);

        if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            if (password.getText().toString().equals(passwordConfirm.getText().toString())) {
                registerUser(email.getText().toString(), password.getText().toString());
            } else {
                Log.d(TAG, "registerOnClick: Passwords do not match");
            }
        } else {
            Log.d(TAG, "registerOnClick: Not An Email-address");
        }

    }

    public void goToRegisterOnClick(View view) {
        EditText passwordConfirm = (EditText) findViewById(R.id.confirm_password_field);
        TextView passwordConfirmLabel = (TextView) findViewById(R.id.confirm_password_label);
        Button register = (Button) findViewById(R.id.register_button);
        Button backToLogin = (Button) findViewById(R.id.back_to_login_button);
        EditText username = (EditText) findViewById(R.id.username);
        TextView usernameLabel = (TextView) findViewById(R.id.username_label);

        passwordConfirm.setVisibility(View.VISIBLE);
        passwordConfirmLabel.setVisibility(View.VISIBLE);
        register.setVisibility(View.VISIBLE);
        backToLogin.setVisibility(View.VISIBLE);
        username.setVisibility(View.VISIBLE);
        usernameLabel.setVisibility(View.VISIBLE);

        Button goToRegister = (Button) findViewById(R.id.go_to_register_button);
        Button logInButton = (Button) findViewById(R.id.login_button);

        goToRegister.setVisibility(View.GONE);
        logInButton.setVisibility(View.GONE);
    }

    public void backToLogin(View view) {
        EditText passwordConfirm = (EditText) findViewById(R.id.confirm_password_field);
        TextView passwordConfirmLabel = (TextView) findViewById(R.id.confirm_password_label);
        Button register = (Button) findViewById(R.id.register_button);
        Button backToLogin = (Button) findViewById(R.id.back_to_login_button);
        EditText username = (EditText) findViewById(R.id.username);
        TextView usernameLabel = (TextView) findViewById(R.id.username_label);

        passwordConfirm.setVisibility(View.GONE);
        passwordConfirmLabel.setVisibility(View.GONE);
        register.setVisibility(View.GONE);
        backToLogin.setVisibility(View.GONE);
        username.setVisibility(View.GONE);
        usernameLabel.setVisibility(View.GONE);

        Button goToRegister = (Button) findViewById(R.id.go_to_register_button);
        Button logInButton = (Button) findViewById(R.id.login_button);

        goToRegister.setVisibility(View.VISIBLE);
        logInButton.setVisibility(View.VISIBLE);
    }

    protected void sendConfirmEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
//                            goToMainScreen();
                        }
                    }
                });
    }

    protected void goToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    protected void updateUserInfo(final FirebaseUser user) {
        EditText username = (EditText) findViewById(R.id.username);
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(username.getText().toString())
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
}
