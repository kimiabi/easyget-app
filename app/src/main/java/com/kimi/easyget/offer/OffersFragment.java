package com.kimi.easyget.offer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kimi.easyget.R;
import com.kimi.easyget.cart.model.ProductTransaction;
import com.kimi.easyget.offer.adapter.AdapterOffers;
import com.kimi.easyget.products.SingleProductFragment;
import com.kimi.easyget.products.models.Product;
import com.kimi.easyget.products.models.ProductTransactionViewModel;
import com.kimi.easyget.products.models.ViewProductLog;
import com.kimi.easyget.user.models.User;

import java.util.List;

import static android.content.ContentValues.TAG;
import static com.kimi.easyget.MainActivity.DEVICE;
import static com.kimi.easyget.MainActivity.OS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OffersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OffersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ProductTransactionViewModel productTransactionViewModel;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private User user;

    public OffersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OfertsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OffersFragment newInstance(String param1, String param2) {
        OffersFragment fragment = new OffersFragment();
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
        user = getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_oferts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final RecyclerView recyclerOffers = view.findViewById(R.id.recycler_offers_fragment);
        setRecyclerOffersContent(recyclerOffers);


        super.onViewCreated(view, savedInstanceState);
    }

    private void setRecyclerOffersContent(final RecyclerView recyclerOffers) {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerOffers.setLayoutManager(gridLayoutManager);

        productTransactionViewModel = new ViewModelProvider(requireActivity()).get(ProductTransactionViewModel.class);
        db.collection("products")
                .whereEqualTo("offer", true)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w("ERR", "Listen failed.", e);
                        return;
                    }
                    final List<Product> products = value.toObjects(Product.class);
                    final AdapterOffers adapterOffers = new AdapterOffers(products, getContext(),
                            new AdapterOffers.OnItemClickListener() {
                                @Override
                                public void onItemClick(final Product product) {
                                    final ProductTransaction productTransaction = getProductTransactionResource(product);
                                    productTransactionViewModel.selectProduct(productTransaction);
                                }

                                @Override
                                public void onItemClickSingleProduct(final Product product) {
                                    registerProductLog(product);
                                    openSingleProductFragment(product);
                                }
                            });
                    recyclerOffers.setAdapter(adapterOffers);
                    adapterOffers.notifyDataSetChanged();

                });

    }

    private void registerProductLog(final Product product) {
        final ViewProductLog viewProductLog = ViewProductLog.builder()
                .productId(product.getId())
                .categoryId(product.getCategoryId())
                .userId(user.getUid())
                .os(OS)
                .device(DEVICE)
                .createdAt(FieldValue.serverTimestamp())
                .build();

        db.collection("viewProductsModels")
                .add(viewProductLog)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                });
    }

    private void openSingleProductFragment(final Product product) {
        SingleProductFragment fragment = SingleProductFragment.newInstance(product);
        addFragment(fragment);
    }

    private void addFragment(final Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }

    private ProductTransaction getProductTransactionResource(final Product product) {
        return ProductTransaction.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .photoUrl(product.getPhoto_url())
                .price(product.getPrice())
                .totalPrice(product.getPrice())
                .totalQuantity("1")
                .offer(product.isOffer())
                .enabled(product.isEnabled())
                .build();
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