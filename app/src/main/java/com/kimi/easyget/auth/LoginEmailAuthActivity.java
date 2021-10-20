package com.kimi.easyget.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.kimi.easyget.MainActivity;
import com.kimi.easyget.R;
import com.kimi.easyget.utils.AlertMessage;
import com.kimi.easyget.utils.CustomError;
import com.kimi.easyget.utils.Rules;

import java.util.Objects;

public class LoginEmailAuthActivity extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private FirebaseAuth firebaseAuth;
    private SwipeRefreshLayout swipeLoading;
    private AlertMessage alertMessage;
    private Rules rules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_email);

        firebaseAuth = FirebaseAuth.getInstance();
        alertMessage = AlertMessage.getInstance();
        rules = Rules.getInstance();

        setWidgets();
    }

    private void setWidgets() {
        final TextInputLayout loginEmailLayout = (TextInputLayout) findViewById(R.id.login_email_layout);
        final TextInputEditText loginEmail = (TextInputEditText) findViewById(R.id.login_email);
        final TextInputLayout loginPasswordLayout = findViewById(R.id.login_password_layout);
        final TextInputEditText loginPassword = (TextInputEditText) findViewById(R.id.login_password);
        final Button btnLogin = findViewById(R.id.btn_login);
        final Button btnNewAccount = findViewById(R.id.btn_new_account);
        final ImageView btnBack = findViewById(R.id.btn_back);
        swipeLoading = findViewById(R.id.swipe_email_auth);
        swipeLoading.setEnabled(false);
        swipeLoading.setColorSchemeColors(getColor(R.color.white), getColor(R.color.colorAccent));
        swipeLoading.setProgressBackgroundColorSchemeColor(getColor(R.color.colorPrimary));


        validateAndSubmitCredentials(loginEmail, loginPassword, loginEmailLayout, loginPasswordLayout, btnLogin);

        btnNewAccount.setOnClickListener(view -> goToRegisterUserActivity());

        btnBack.setOnClickListener(view -> onBackPressed());

    }

    private void validateAndSubmitCredentials(final TextInputEditText loginEmail,
                                              final TextInputEditText loginPassword,
                                              final TextInputLayout loginEmailLayout,
                                              final TextInputLayout loginPasswordLayout,
                                              final Button btnLogin) {

        btnLogin.setOnClickListener(view -> {
            final String email = loginEmail.getText().toString();
            final String password = loginPassword.getText().toString();

            final CustomError customErrorEmail = rules.emailValidation(this, email);
            final CustomError customErrorPassword = rules.passwordValidation(this, password);

            enabledErrorTextInputEditText(loginEmailLayout, loginPasswordLayout, false);

            if (!customErrorEmail.getError() && !customErrorPassword.getError()) {
                enabledErrorTextInputEditText(loginEmailLayout, loginPasswordLayout, false);
                switchLoading(true);
                authEmail(email, password);
            } else {
                enabledErrorTextInputEditText(loginEmailLayout, loginPasswordLayout, true);
                if (customErrorEmail.getError()) {
                    loginEmailLayout.setError(customErrorEmail.getMessage());
                }

                if (customErrorPassword.getError()) {
                    loginPasswordLayout.setError(customErrorPassword.getMessage());
                }
            }
        });
    }

    private void authEmail(final String email, final String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    switchLoading(false);
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        goToMainActivity();
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            alertMessage.show(this, getString(R.string.oh), e.getLocalizedMessage());
                        }
                    }
                });
    }

    private void goToMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        finishAffinity();
        startActivity(intent);
    }

    private void goToRegisterUserActivity() {
        final Intent intent = new Intent(this, RegisterUserActivity.class);
        startActivity(intent);
    }

    private void switchLoading(final Boolean flag) {
        swipeLoading.setEnabled(flag);
        swipeLoading.setRefreshing(flag);
    }

    private void enabledErrorTextInputEditText(final TextInputLayout loginEmailLayout,
                                               final TextInputLayout loginPasswordLayout,
                                               final Boolean flag) {
        loginEmailLayout.setErrorEnabled(flag);
        loginPasswordLayout.setErrorEnabled(flag);
    }

}