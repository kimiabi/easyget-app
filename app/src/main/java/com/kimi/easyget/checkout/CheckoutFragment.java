package com.kimi.easyget.checkout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kimi.easyget.R;
import com.kimi.easyget.cart.model.ProductTransaction;
import com.kimi.easyget.cart.model.UserShoppingCart;
import com.kimi.easyget.checkout.adapter.AdapterCheckout;
import com.kimi.easyget.checkout.models.Checkout;
import com.kimi.easyget.home.HomeFragment;
import com.kimi.easyget.user.models.User;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckoutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CHECKOUT = "checkout";
    private static final String CASH = "CASH";

    // TODO: Rename and change types of parameters
    private Checkout checkout;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private Dialog dialog;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CheckoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckoutFragment newInstance(final Checkout checkout) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putSerializable(CHECKOUT, checkout);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            checkout = (Checkout) getArguments().getSerializable(CHECKOUT);
        }

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerViewProduct = view.findViewById(R.id.recycler_products_ck);
        final Button btnEdit = view.findViewById(R.id.btn_edit);
        final Button btnDone = view.findViewById(R.id.btn_done);

        setRecyclerProductContent(recyclerViewProduct);
        setAmounts(view);

        btnEdit.setOnClickListener(view1 -> {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack();
        });

        btnDone.setOnClickListener( v -> {
            saveCheckout(view);
        });
    }

    private void saveCheckout(final View view) {

        final TextInputEditText addressInput = view.findViewById(R.id.address_ck);
        final String address = addressInput.getText().toString();

        final User user = getCurrentUser();

        final Checkout checkoutSaved = Checkout.builder()
                .totalAmount(checkout.getTotalAmount())
                .subTotalAmount(checkout.getTotalAmount())
                .saving(0.0)
                .userId(user.getUid())
                .deliveryAddress(address)
                .paymentType(CASH)
                .createdAt(FieldValue.serverTimestamp())
                .products(checkout.getProducts())
                .shoppingCartId("cart-" + user.getUid())
                .build();

        final CollectionReference collection = db.collection("checkoutModels");
        final String id = collection.document().getId();

        collection.document(id)
                .set(checkoutSaved)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "checkoutModels successfully written!");
                    clearShoppingCart(user.getUid());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                });
    }

    private void clearShoppingCart(final String uid) {

        final UserShoppingCart cart = UserShoppingCart.builder()
                .products(new ArrayList<>())
                .build();

        final String id = "cart-" + uid;

        db.collection("userShoppingCarts")
                .document(id)
                .set(cart)
                .addOnSuccessListener(aVoid -> {
                    showDoneMessage("Listo!", "Tu pedido fue creado con exito, pronto llegara");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                });
    }

    private void setAmounts(final View view) {
        final TextView subtotal = view.findViewById(R.id.subtotal_ck);
        final TextView total = view.findViewById(R.id.total_ck);

        subtotal.setText(String.valueOf(checkout.getTotalAmount()));
        total.setText(String.valueOf(checkout.getTotalAmount()));
    }

    private void setRecyclerProductContent(final RecyclerView recyclerViewProduct) {

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerViewProduct.setLayoutManager(linearLayoutManager);

        final List<ProductTransaction> products = checkout.getProducts();

        final AdapterCheckout adapterCheckout = new AdapterCheckout(products, getContext());
        recyclerViewProduct.setAdapter(adapterCheckout);
        adapterCheckout.notifyDataSetChanged();
    }

    private User getCurrentUser() {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        return User.builder()
                .displayName(firebaseUser.getDisplayName())
                .email(firebaseUser.getEmail())
                .uid(firebaseUser.getUid())
                .build();
    }

    public void showDoneMessage(String title, String message) {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_dialog);
        TextView messageTitle = dialog.findViewById(R.id.message_title);
        TextView messageBody = dialog.findViewById(R.id.body_message);
        Button btnClose = dialog.findViewById(R.id.btn_close);
        ImageView imageView = dialog.findViewById(R.id.icon_message);

        messageTitle.setText(title);
        messageBody.setText(message);
        imageView.setImageDrawable(getContext().getDrawable(R.drawable.check));

        btnClose.setOnClickListener(view -> {
            dialog.dismiss();
            addFragment(new HomeFragment());

        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void addFragment(final Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

}