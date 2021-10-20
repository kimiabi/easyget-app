package com.kimi.easyget.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kimi.easyget.R;
import com.kimi.easyget.cart.model.ProductTransaction;
import com.kimi.easyget.offer.adapter.AdapterOffers;
import com.kimi.easyget.populars.adapter.AdapterPopulars;
import com.kimi.easyget.products.models.Product;
import com.kimi.easyget.products.models.ProductTransactionViewModel;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private ProductTransactionViewModel productTransactionViewModel;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        db = FirebaseFirestore.getInstance();
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
        final RecyclerView recyclerOffers = view.findViewById(R.id.recycler_offers);
        final RecyclerView recyclerPopulars = view.findViewById(R.id.recycler_populars);

        setSlideContent(imageSlider);
        setRecyclerOffersContent(recyclerOffers);
        setReyclerPopullarsContent(recyclerPopulars);


        super.onViewCreated(view, savedInstanceState);
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
                                public void onItemClick(Product product) {
                                    final ProductTransaction productTransaction = getProductTransactionResource(product);
                                    productTransactionViewModel.selectProduct(productTransaction);
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
                .totalQuantity("1")
                .offer(true)
                .enabled(true)
                .build();
    }



    private void setSlideContent(final ImageSlider imageSlider) {
        final List<SlideModel> imageList = new ArrayList<>();

        imageList.add(new SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years.", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct.", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://bit.ly/3fLJf72", "And people do that.", ScaleTypes.FIT));
        imageSlider.setImageList(imageList);
    }
}