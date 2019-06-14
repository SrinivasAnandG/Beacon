package com.sriniavsprojectpool.beacon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sriniavsprojectpool.beacon.Cores.UserIdentifier;
import com.sriniavsprojectpool.beacon.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN_STUDENT = 997;
    private static final int RC_SIGN_IN_MANAGEMENT = 998;
    private static final int RC_SIGN_IN_GUEST = 999;

    private String studentSignInText="Student";
    private String managementSignInText="Management";
    private String guestSignInText = "Guest";


    GoogleSignInOptions gso;
    GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setSignInText(this);
        gso = new GoogleSignInOptions.Builder()
                .requestIdToken("541721568302-1t84s3ntmh0hj3uckigt0dbf8tav7sd7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();



    }

    public String getStudentSignInText() {
        return studentSignInText;
    }

    public String getManagementSignInText() {
        return managementSignInText;
    }

    public String getGuestSignInText() {
        return guestSignInText;
    }

    @BindingAdapter("android:text")
    public static void setSignInText(SignInButton signInButton , String name)
    {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
        View v = signInButton.getChildAt(i);

        if (v instanceof TextView) {
            TextView tv = (TextView) v;
            tv.setText(name);
            return;
        }
    }
    }


    public void signIn(View view ) {
        int id = view.getId();

        switch (id)
        {
            case R.id.signInStudent:
                Intent intent= googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN_STUDENT);
                break;

            case R.id.signInManagement:
                Intent intent1 = googleSignInClient.getSignInIntent();
                startActivityForResult(intent1, RC_SIGN_IN_MANAGEMENT);
                break;

            case R.id.signInGuest:
                Intent intent2= googleSignInClient.getSignInIntent();
                startActivityForResult(intent2, RC_SIGN_IN_GUEST);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(this, ""+requestCode, Toast.LENGTH_SHORT).show();
        switch (requestCode)
        {
            case RC_SIGN_IN_STUDENT:{
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    String email = account.getEmail();
                    googleSignInClient.signOut();
                    if(new UserIdentifier(email).getUserFlag().equals("Student"))
                        firebaseAuthWithGoogle(account);
                    else
                        Toast.makeText(this, "You must use college email id", Toast.LENGTH_SHORT).show();
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                    // ...
                }
                break;
            }

            case RC_SIGN_IN_MANAGEMENT:{
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    String email = account.getEmail();
                    googleSignInClient.signOut();
                    if(new UserIdentifier(email).getUserFlag().equals("Management"))
                        firebaseAuthWithGoogle(account);
                    else
                        Toast.makeText(this, "You must use college faculty email id", Toast.LENGTH_SHORT).show();
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                    // ...
                }
                break;
            }

            case RC_SIGN_IN_GUEST:{
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    String email = account.getEmail();
                    googleSignInClient.signOut();
                    if(new UserIdentifier(email).getUserFlag().equals("Guest"))
                        firebaseAuthWithGoogle(account);
                    else
                        Toast.makeText(this, "You must use personal Gmail", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                    // ...
                }
                break;
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Something went wrong, please try again...", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        updateUI(FirebaseAuth.getInstance().getCurrentUser());
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null)
        {
            Toast.makeText(this, "User Found"+currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        }
    }


}
