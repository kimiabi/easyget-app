package com.kimi.easyget.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kimi.easyget.R;
import com.kimi.easyget.auth.LoginActivity;
import com.kimi.easyget.auth.models.AuthenticationLog;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String AUTH_PROVIDER = "GOOGLE";
    private static final String DEVICE = "SAMSUNG SM-A205G";
    private static final String OS = "Android 10, API 29";
    private static final String DEVICE_IP = "192.168.1.15";
    private static final String TYPE_AUTH = "logout";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseFirestore db;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        final GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);

        setWidget(view, user);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setWidget(final View view, final FirebaseUser user) {
        final ImageView photoUser = view.findViewById(R.id.photo_user);
        final TextView nameUser = view.findViewById(R.id.name_user);
        final TextView emailUser = view.findViewById(R.id.email_user);
        final Button btnSignOut = view.findViewById(R.id.btn_signout);

        if (user != null) {
            Glide.with(this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(photoUser);
            nameUser.setText(user.getDisplayName());
            emailUser.setText(user.getEmail());
        }

        btnSignOut.setOnClickListener(v -> {
            firebaseAuth.signOut();
            googleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
                registerAuthLog(user);
                goToLogin();
            });
        });
    }

    private void registerAuthLog(final FirebaseUser user) {
        final AuthenticationLog authenticationLog = AuthenticationLog.builder()
                .device(DEVICE)
                .ip(DEVICE_IP)
                .os(OS)
                .provider(AUTH_PROVIDER)
                .registration(FieldValue.serverTimestamp())
                .type(TYPE_AUTH)
                .userId(user.getUid())
                .build();

        db.collection("authenticationLogsModels")
                .add(authenticationLog)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                });
    }

    private void goToLogin() {
        final Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }
}