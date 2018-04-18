package com.example.oluwatobiloba.medmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.oluwatobiloba.medmanager.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1000;
    private static final String TAG = SplashScreen.class.getSimpleName();

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();

                if (isUserSignIn()) {
                    launchIntent(new Intent(SplashScreen.this, DashBoard.class));
                    finish();
                } else {

                    List<AuthUI.IdpConfig> providersList = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build());

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providersList)
                                    .setTheme(R.style.SignInTheme)
                                    .setLogo(R.drawable.ic_logo)
//                                    .setIsSmartLockEnabled(BuildConfig.DEBUG, true)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                FirebaseUserMetadata metadata = Objects.requireNonNull(user).getMetadata();
                assert metadata != null;
                if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                    Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    launchIntent(new Intent(SplashScreen.this, EditProfile.class));
                } else {
                    Toast.makeText(this, "Welcome back" + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    launchIntent(new Intent(SplashScreen.this, DashBoard.class));
                }
            } else {
                if (response == null) {
                    showToast(R.string.sign_in_cancelled);
                    return;
                }

                if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showToast(R.string.no_internet_connection);
                    return;
                }

                showToast(R.string.unknown_error);
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    private void showToast(int msg) {
        Toast.makeText(this, getResources().getString(msg), Toast.LENGTH_SHORT).show();
    }

    private boolean isUserSignIn() {
        return mFirebaseUser != null;
    }

    private void launchIntent(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
