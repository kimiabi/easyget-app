package com.kimi.easyget.products;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kimi.easyget.R;
import com.kimi.easyget.cart.model.ProductTransaction;
import com.kimi.easyget.categories.models.CategoriesViewModel;
import com.kimi.easyget.categories.models.Category;
import com.kimi.easyget.offer.adapter.AdapterOffers;
import com.kimi.easyget.products.adapter.AdapterProduct;
import com.kimi.easyget.products.models.Product;
import com.kimi.easyget.products.models.ProductTransactionViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "category";

    private ProductTransactionViewModel productTransactionViewModel;
    private FirebaseFirestore db;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Category category;

    public ProductFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ProductFragment newInstance(final Category category) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            category = (Category) getArguments().getSerializable(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final RecyclerView recyclerProducts = view.findViewById(R.id.recycler_products);
        final TextView titleCategory = view.findViewById(R.id.title_category);

        setRecyclerProductsContent(recyclerProducts, titleCategory);

        super.onViewCreated(view, savedInstanceState);
    }

    private void setRecyclerProductsContent(final RecyclerView recyclerProducts, final TextView titleCategory) {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerProducts.setLayoutManager(gridLayoutManager);

        titleCategory.setText(category.getName());

        productTransactionViewModel = new ViewModelProvider(requireActivity()).get(ProductTransactionViewModel.class);
        db.collection("products")
                .whereEqualTo("categoryId", category.getId())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("ERR", "Listen failed.", error);
                        return;
                    }

                    final List<Product> products = value.toObjects(Product.class);
                    final AdapterProduct adapterProduct = new AdapterProduct(products, getContext(),
                            new AdapterProduct.OnItemClickListener() {
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
                    recyclerProducts.setAdapter(adapterProduct);
                    adapterProduct.notifyDataSetChanged();
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

}