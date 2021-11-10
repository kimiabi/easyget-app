package com.kimi.easyget.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kimi.easyget.R;
import com.kimi.easyget.cart.model.ProductTransaction;
import com.kimi.easyget.offer.adapter.AdapterOffers;
import com.kimi.easyget.populars.adapter.AdapterPopulars;
import com.kimi.easyget.products.SingleProductFragment;
import com.kimi.easyget.products.models.Product;
import com.kimi.easyget.products.models.ProductTransactionViewModel;
import com.kimi.easyget.user.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    private ProductTransactionViewModel productTransactionViewModel;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        final RecyclerView recyclerSuggestions = view.findViewById(R.id.recycler_suggestions);
        final RecyclerView recyclerOffers = view.findViewById(R.id.recycler_offers);
        final RecyclerView recyclerPopulars = view.findViewById(R.id.recycler_populars);

        setSlideContent(imageSlider);
        seRecyclerSuggestionsContent(recyclerSuggestions, view);
        setRecyclerOffersContent(recyclerOffers);
        setReyclerPopullarsContent(recyclerPopulars);


        super.onViewCreated(view, savedInstanceState);
    }

    private void seRecyclerSuggestionsContent(final RecyclerView recyclerSuggestion,
                                              final View view){
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerSuggestion.setLayoutManager(linearLayoutManager);

        final LinearLayout linearLayout = view.findViewById(R.id.label_suggestion_layout);

        final User user = getCurrentUser();

        productTransactionViewModel = new ViewModelProvider(requireActivity()).get(ProductTransactionViewModel.class);
        db.collection("productsSuggestionsModels")
                .whereEqualTo("userId", user.getUid())
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w("ERR", "Listen failed.", e);
                        return;
                    }
                    final List<Product> products = value.toObjects(Product.class);
                    if (!products.isEmpty()) {
                        linearLayout.setVisibility(View.VISIBLE);
                    }else {
                        linearLayout.setVisibility(View.GONE);
                    }
                    Collections.reverse(products);
                    final AdapterOffers adapterOffers = new AdapterOffers(products, getContext(),
                            new AdapterOffers.OnItemClickListener() {
                                @Override
                                public void onItemClick(final Product product) {
                                    final ProductTransaction productTransaction = getProductSuggestionByTransactionResource(product);
                                    productTransactionViewModel.selectProduct(productTransaction);
                                }

                                @Override
                                public void onItemClickSingleProduct(final Product product) {
                                    openSingleProductFragment(product);
                                }
                            });
                    recyclerSuggestion.setAdapter(adapterOffers);
                    adapterOffers.notifyDataSetChanged();
                });
    }

    private void setRecyclerOffersContent(final RecyclerView recyclerOffers) {

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerOffers.setLayoutManager(linearLayoutManager);

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
                                    openSingleProductFragment(product);
                                }
                            });
                    recyclerOffers.setAdapter(adapterOffers);
                    adapterOffers.notifyDataSetChanged();
                });
    }

    private void setReyclerPopullarsContent(final RecyclerView recyclerPopulars) {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerPopulars.setLayoutManager(linearLayoutManager);

        productTransactionViewModel = new ViewModelProvider(requireActivity()).get(ProductTransactionViewModel.class);
        db.collection("products")
                .whereEqualTo("popular", true)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w("ERR", "Listen failed.", e);
                        return;
                    }

                    final List<Product> products1 = value.toObjects(Product.class);
                    final AdapterPopulars adapterPopulars = new AdapterPopulars(products1, getContext(),
                            new AdapterPopulars.OnItemClickListener() {
                                @Override
                                public void onItemClick(Product product) {
                                    final ProductTransaction productTransaction = getProductTransactionResource(product);
                                    productTransactionViewModel.selectProduct(productTransaction);
                                }

                                @Override
                                public void onItemClickSingleProduct(Product product) {
                                    openSingleProductFragment(product);
                                }
                            });
                    recyclerPopulars.setAdapter(adapterPopulars);
                    adapterPopulars.notifyDataSetChanged();
                });
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

    private ProductTransaction getProductSuggestionByTransactionResource(final Product product) {
        return ProductTransaction.builder()
                .productId(product.getProductId())
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

    private void setSlideContent(final ImageSlider imageSlider) {
        final List<SlideModel> imageList = new ArrayList<>();
        String url1 = "https://firebasestorage.googleapis.com/v0/b/easyget-km.appspot.com/o/products%2F2.jpg?alt=media&token=672e654b-c769-48de-a092-fab1826ad4d8";
        String url2 = "https://firebasestorage.googleapis.com/v0/b/easyget-km.appspot.com/o/products%2F1.jpg?alt=media&token=88e340c1-6ced-49e3-a4eb-88add9e58566";
        String url3 = "https://firebasestorage.googleapis.com/v0/b/easyget-km.appspot.com/o/products%2F3.jpg?alt=media&token=b3976230-328c-4df0-a994-5f3e1e3cfaff";


        imageList.add(new SlideModel(url1, "Los mejores frijoles a un mejor precio", ScaleTypes.FIT));
        imageList.add(new SlideModel(url2, "Leche Anchor siempre contigo, espera las promociones...", ScaleTypes.FIT));
        imageList.add(new SlideModel(url3, "Tienes que estar atento a las ofertas que pronto publicaremos", ScaleTypes.FIT));
        imageSlider.setImageList(imageList);
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