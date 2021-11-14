package com.kimi.easyget.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kimi.easyget.R;
import com.kimi.easyget.cart.model.ProductTransaction;
import com.kimi.easyget.products.SingleProductFragment;
import com.kimi.easyget.products.adapter.AdapterProduct;
import com.kimi.easyget.products.models.Product;
import com.kimi.easyget.products.models.ProductTransactionViewModel;
import com.kimi.easyget.search.models.Search;
import com.kimi.easyget.search.models.SearchResult;
import com.kimi.easyget.user.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static android.content.ContentValues.TAG;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String QUERY = "QUERY";
    private static final String ARG_PARAM2 = "param2";
    private ProductTransactionViewModel productTransactionViewModel;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    // TODO: Rename and change types of parameters
    private String query;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(final String query) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            query = getArguments().getString(QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setContent(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setContent(final View view) {

        final TextView quantityResult = view.findViewById(R.id.quantity_result);
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_products_search);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        productTransactionViewModel = new ViewModelProvider(requireActivity()).get(ProductTransactionViewModel.class);
        db.collection("products")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("ERR", "Listen failed.", error);
                        return;
                    }

                    UUID uuid = UUID.randomUUID();

                    final List<Product> products = value.toObjects(Product.class);

                    final List<Product> filteredProduct = products.stream()
                            .filter(product -> product.getName().toLowerCase().contains(query.toLowerCase()))
                            .collect(toList());
                    quantityResult.setText(getString(R.string.results_search, String.valueOf(filteredProduct.size()), query));
                    final AdapterProduct adapterProduct = new AdapterProduct(filteredProduct, getContext(),
                            new AdapterProduct.OnItemClickListener() {
                                @Override
                                public void onItemClick(Product product) {
                                    final ProductTransaction productTransaction = getProductTransactionResource(product);
                                    productTransactionViewModel.selectProduct(productTransaction);
                                }

                                @Override
                                public void onItemClickSingleProduct(Product product) {
                                    registerSearch(filteredProduct, product, uuid);
                                    openSingleProductFragment(product);
                                }
                            });
                    recyclerView.setAdapter(adapterProduct);
                    adapterProduct.notifyDataSetChanged();
                    registerSearch(filteredProduct, null, uuid);
                });

    }

    private void registerSearch(final List<Product> filteredProduct, final Product product, final UUID uuid) {

        final String productId = isNull(product) ? null : product.getId();
        final String categoryId = isNull(product) ? null : product.getCategoryId();

        final User user = getCurrentUser();
        final List<SearchResult> searchResults = getSearchResultResource(filteredProduct);
        final Search search = Search.builder()
                .userId(user.getUid())
                .registration(FieldValue.serverTimestamp())
                .key(query)
                .results(searchResults)
                .select(productId)
                .categoryId(categoryId)
                .build();

        db.collection("searchModels")
                .document(String.valueOf(uuid))
                .set(search)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                });
    }

    private List<SearchResult> getSearchResultResource(final List<Product> filteredProduct) {

        final List<SearchResult> searchResults = new ArrayList<>();
        for (Product product : filteredProduct){

            SearchResult searchResult = SearchResult.builder()
                    .productId(product.getId())
                    .build();
            searchResults.add(searchResult);
        }
        return searchResults;
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