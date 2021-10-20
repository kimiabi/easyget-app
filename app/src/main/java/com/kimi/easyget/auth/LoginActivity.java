package com.kimi.easyget.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kimi.easyget.MainActivity;
import com.kimi.easyget.R;
import com.kimi.easyget.user.models.User;
import com.kimi.easyget.utils.AlertMessage;

import java.util.Objects;

import static java.util.Objects.isNull;

public class LoginActivity extends AppCompatActivity {

    private static final String AUTH_PROVIDER = "GOOGLE";
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final String SPACE_DELIMITER = " ";
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeLoading;
    private AlertMessage alertMessage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        firebaseAuth = FirebaseAuth.getInstance();
        alertMessage = AlertMessage.getInstance();
        db = FirebaseFirestore.getInstance();

        setWidgets();
    }//onCreate

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            goToMainActivity();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            final Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                final GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + googleSignInAccount.getId());
                firebaseAuthWithGoogle(googleSignInAccount.getIdToken());
            } catch (ApiException e) {
                switchLoading(false);
                alertMessage.show(this, getString(R.string.oh), String.join(SPACE_DELIMITER, getString(R.string.bad_message), e.getMessage()));
            }
        }
    }

    private void setWidgets() {
        final Button btnGoogle = findViewById(R.id.btn_google_auth);
        final Button btnEmailAuth = findViewById(R.id.btn_email_auth);
        swipeLoading = findViewById(R.id.swipe_social_login);
        swipeLoading.setEnabled(false);
        swipeLoading.setColorSchemeColors(getColor(R.color.white), getColor(R.color.colorAccent));
        swipeLoading.setProgressBackgroundColorSchemeColor(getColor(R.color.colorPrimary));
        btnGoogle.setOnClickListener(view -> {
            switchLoading(true);
            signIn();
        });
        btnEmailAuth.setOnClickListener(view -> goToEmailAuth());
    }

    private void signIn() {
        final Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void goToEmailAuth() {
        final Intent intent = new Intent(this, LoginEmailAuthActivity.class);
        startActivity(intent);
    }

    private void firebaseAuthWithGoogle(final String idToken) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        assert user != null;
                        createProfile(user);
                    } else {
                        switchLoading(false);
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch(Exception e) {
                            alertMessage.show(this, getString(R.string.oh), e.getLocalizedMessage());
                        }
                    }
                });
    }

    private void createProfile(final FirebaseUser firebaseUser) {

        final User user = User.builder()
                .uid(firebaseUser.getUid())
                .displayName(firebaseUser.getDisplayName())
                .email(firebaseUser.getEmail())
                .authProvider(AUTH_PROVIDER)
                .enabled(true)
                .createdAt(FieldValue.serverTimestamp())
                .createdBy("ANDROID_DEVICE")
                .build();

        db.collection("users")
                .document(firebaseUser.getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    goToMainActivity();
                })
                .addOnFailureListener(e -> {
                    switchLoading(false);
                    Log.w(TAG, "Error writing document", e);
                    alertMessage.show(LoginActivity.this, getString(R.string.oh), e.getLocalizedMessage());

                });
    }

    private void goToMainActivity() {
        switchLoading(false);
        final Intent intent = new Intent(this, MainActivity.class);
        finishAffinity();
        startActivity(intent);
    }

    private void switchLoading(final Boolean flag) {
        swipeLoading.setEnabled(flag);
        swipeLoading.setRefreshing(flag);
    }
}