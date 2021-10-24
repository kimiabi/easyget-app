package com.kimi.easyget.cart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.kimi.easyget.R;
import com.kimi.easyget.cart.adapter.AdapterProductsCart;
import com.kimi.easyget.cart.model.ProductTransaction;
import com.kimi.easyget.cart.model.UserShoppingCart;
import com.kimi.easyget.user.models.User;

import java.util.List;
import java.util.stream.Collectors;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private TextView totalAmount;
    private List<ProductTransaction> productTransactions;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment cartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final RecyclerView recyclerViewProductsCart = view.findViewById(R.id.recycler_products_cart);

        totalAmount = view.findViewById(R.id.total_amount);

        setRecyclerProductCartContent(recyclerViewProductsCart);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setRecyclerProductCartContent(final RecyclerView recyclerViewProductsCart) {

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        recyclerViewProductsCart.setLayoutManager(linearLayoutManager);

        final User user = getCurrentUser();
        db.collection("userShoppingCarts")
                .document("cart-" + user.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("carrito compras failed", "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "carrito compras succ: " + snapshot.getData());
                            final UserShoppingCart userShoppingCarts =
                                    snapshot.toObject(UserShoppingCart.class);
                            productTransactions = userShoppingCarts.getProducts();
                            final AdapterProductsCart adapterProductsCart = new AdapterProductsCart(productTransactions, getContext(), new AdapterProductsCart.OnItemClickListener() {
                                @Override
                                public void onItemClick(final ProductTransaction productTransaction) {

                                }

                                @Override
                                public void onItemClickDelete(final ProductTransaction productTransaction) {
                                    removeproductTransaction(productTransaction);
                                }

                                @Override
                                public void updateTotalAmount(final int index, final ProductTransaction productTransaction) {
                                    productTransactions.set(index, productTransaction);
                                    setTotalAmount(totalAmount, productTransactions);
                                }
                            });
                            recyclerViewProductsCart.setAdapter(adapterProductsCart);
                            adapterProductsCart.notifyDataSetChanged();

                            setTotalAmount(totalAmount, productTransactions);


                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });


    }

    private void removeproductTransaction(final ProductTransaction productTransaction) {

        final User user = getCurrentUser();
        List<ProductTransaction> filteredProducts = getFilteredProducts(productTransaction);

        final UserShoppingCart userShoppingCart = UserShoppingCart.builder()
                .user(user)
                .products(filteredProducts)
                .build();

        db.collection("userShoppingCarts")
                .document("cart-" + user.getUid())
                .set(userShoppingCart)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                });
    }

    private List<ProductTransaction> getFilteredProducts(final ProductTransaction productTransaction) {
        return this.productTransactions.stream()
                .filter(product -> !product.getProductId().equals(productTransaction.getProductId()) )
                .collect(Collectors.toList());
    };

    private void setTotalAmount(final TextView totalAmount, final List<ProductTransaction> products) {
        double total = 0.0;

        for (ProductTransaction product : products) {
            total = total + Double.parseDouble(product.getTotalPrice());
        }

        totalAmount.setText(String.valueOf(total));
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
}