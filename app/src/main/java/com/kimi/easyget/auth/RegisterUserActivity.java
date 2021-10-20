package com.kimi.easyget.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kimi.easyget.MainActivity;
import com.kimi.easyget.R;
import com.kimi.easyget.user.models.User;
import com.kimi.easyget.utils.AlertMessage;
import com.kimi.easyget.utils.CustomError;
import com.kimi.easyget.utils.Rules;

import java.util.Objects;

public class RegisterUserActivity extends AppCompatActivity {

    private static final String TAG = "Register Firebase";
    private static final String DELIMITER = " ";
    private static final String AUTH_PROVIDER = "EMAIL";
    private FirebaseAuth firebaseAuth;
    private SwipeRefreshLayout swipeLoading;
    private AlertMessage alertMessage;
    private FirebaseFirestore db;
    private Rules rules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        firebaseAuth = FirebaseAuth.getInstance();
        alertMessage = AlertMessage.getInstance();
        rules = Rules.getInstance();
        db = FirebaseFirestore.getInstance();
        setWidgets();
    }

    private void setWidgets() {
        final TextInputEditText firstNameInput = findViewById(R.id.first_name_register);
        final TextInputLayout firstNameInputLayout = findViewById(R.id.first_name_register_layout);
        final TextInputEditText lastNameInput = findViewById(R.id.last_name_register);
        final TextInputLayout lastNameInputLayout = findViewById(R.id.last_name_register_layout);
        final TextInputEditText emailInput = findViewById(R.id.email_register);
        final TextInputLayout emailInputLayout = findViewById(R.id.email_register_layout);
        final TextInputEditText passwordInput = findViewById(R.id.password_register);
        final TextInputLayout passwordInputLayout = findViewById(R.id.password_register_layout);
        final TextInputEditText passwordConfirmInput = findViewById(R.id.password_confirm_register);
        final TextInputLayout passwordConfirmInputLayout = findViewById(R.id.password_confirm_register_layout);
        final Button btnRegister = findViewById(R.id.btn_register);
        final ImageView btnBack = findViewById(R.id.btn_back);

        swipeLoading = findViewById(R.id.swipe_user_register);
        swipeLoading.setEnabled(false);
        swipeLoading.setColorSchemeColors(getColor(R.color.white), getColor(R.color.colorAccent));
        swipeLoading.setProgressBackgroundColorSchemeColor(getColor(R.color.colorPrimary));

        validateAndSubmitDataNewUser(firstNameInput,
                firstNameInputLayout,
                lastNameInput,
                lastNameInputLayout,
                emailInput,
                emailInputLayout,
                passwordInput,
                passwordInputLayout,
                passwordConfirmInput,
                passwordConfirmInputLayout,
                btnRegister);

        btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void validateAndSubmitDataNewUser(final TextInputEditText firstNameInput,
                                              final TextInputLayout firstNameInputLayout,
                                              final TextInputEditText lastNameInput,
                                              final TextInputLayout lastNameInputLayout,
                                              final TextInputEditText emailInput,
                                              final TextInputLayout emailInputLayout,
                                              final TextInputEditText passwordInput,
                                              final TextInputLayout passwordInputLayout,
                                              final TextInputEditText passwordConfirmInput,
                                              final TextInputLayout passwordConfirmInputLayout,
                                              final Button btnRegister) {

        btnRegister.setOnClickListener(view -> {
            final String firstName = firstNameInput.getText().toString().trim();
            final String lastName = lastNameInput.getText().toString().trim();
            final String email = emailInput.getText().toString().trim();
            final String password = passwordInput.getText().toString().trim();
            final String passwordConfirm = passwordConfirmInput.getText().toString().trim();

            final CustomError customErrorFirstName = rules.fieldValidation(this, firstName);
            final CustomError customErrorLastName = rules.fieldValidation(this, lastName);
            final CustomError customErrorEmail = rules.emailValidation(this, email);
            final CustomError customErrorPassword = rules.passwordValidation(this, password);
            final CustomError customErrorPasswordConfirm = rules.passwordValidationConfirm(this, password, passwordConfirm);

            enabledErrorTextInputEditText(firstNameInputLayout, lastNameInputLayout, emailInputLayout, passwordInputLayout, passwordConfirmInputLayout, false);

            if (!customErrorFirstName.getError() && !customErrorLastName.getError() && !customErrorEmail.getError() && !customErrorPassword.getError() && !customErrorPasswordConfirm.getError()) {
                switchLoading(true);
                createAccount(firstName, lastName, email, password);
            } else {
                enabledErrorTextInputEditText(firstNameInputLayout, lastNameInputLayout, emailInputLayout, passwordInputLayout, passwordConfirmInputLayout, true);
                if (customErrorFirstName.getError()) {
                    firstNameInputLayout.setError(customErrorFirstName.getMessage());
                }
                if (customErrorLastName.getError()) {
                    lastNameInputLayout.setError(customErrorLastName.getMessage());
                }
                if (customErrorEmail.getError()) {
                    emailInputLayout.setError(customErrorEmail.getMessage());
                }
                if (customErrorPassword.getError()) {
                    passwordInputLayout.setError(customErrorPassword.getMessage());
                }
                if (customErrorPasswordConfirm.getError()) {
                    passwordConfirmInputLayout.setError(customErrorPasswordConfirm.getMessage());
                }
            }


        });

    }

    private void enabledErrorTextInputEditText(final TextInputLayout firstNameInput,
                                               final TextInputLayout lastNameInput,
                                               final TextInputLayout emailInput,
                                               final TextInputLayout passwordInput,
                                               final TextInputLayout passwordConfirmInput,
                                               final Boolean flag) {
        firstNameInput.setErrorEnabled(flag);
        lastNameInput.setErrorEnabled(flag);
        emailInput.setErrorEnabled(flag);
        passwordInput.setErrorEnabled(flag);
        passwordConfirmInput.setErrorEnabled(flag);
    }

    private void createAccount(final String name, final String lastName, final String email, final String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        final FirebaseUser user = firebaseAuth.getCurrentUser();
                        createAndUpdateProfile(name, lastName, user);
                    } else {
                        // If sign in fails, display a message to the user.
                        switchLoading(false);
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterUserActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createAndUpdateProfile(final String name, final String lastName, final FirebaseUser user) {
        final String displayName = String.join(DELIMITER, name, lastName);
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        user.updateProfile(userProfileChangeRequest)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                        createProfile(name, lastName, displayName, user);
                    } else {
                        switchLoading(false);
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            alertMessage.show(this, getString(R.string.oh), e.getLocalizedMessage());
                        }
                    }
                });
    }

    private void createProfile(final String name, final String lastName, final String displayName, final FirebaseUser firebaseUser) {
        switchLoading(false);

        final User user = User.builder()
                .uid(firebaseUser.getUid())
                .firstName(name)
                .lastName(lastName)
                .displayName(displayName)
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
                    Log.w(TAG, "Error writing document", e);
                    alertMessage.show(RegisterUserActivity.this, getString(R.string.oh), e.getLocalizedMessage());

                });

    }

    private void goToMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        finishAffinity();
        startActivity(intent);
    }

    private void switchLoading(final Boolean flag) {
        swipeLoading.setEnabled(flag);
        swipeLoading.setRefreshing(flag);
    }
}