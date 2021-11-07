package com.kimi.easyget.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kimi.easyget.R;
import com.kimi.easyget.cart.model.ProductTransaction;
import com.kimi.easyget.products.SingleProductFragment;
import com.kimi.easyget.products.adapter.AdapterProduct;
import com.kimi.easyget.products.models.Product;
import com.kimi.easyget.products.models.ProductTransactionViewModel;

import java.util.List;

public class SearchableActivity extends AppCompatActivity {

    private ProductTransactionViewModel productTransactionViewModel;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchProduct(query);
        }
    }

    private void searchProduct(final String query) {
        Log.d("busqueda", query);

        loadProducts();
    }

    private void loadProducts() {

        final RecyclerView recyclerView = findViewById(R.id.recycler_products_search);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        productTransactionViewModel = new ViewModelProvider(this).get(ProductTransactionViewModel.class);
        db.collection("products")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("ERR", "Listen failed.", error);
                        return;
                    }

                    final List<Product> products = value.toObjects(Product.class);
                    final AdapterProduct adapterProduct = new AdapterProduct(products, this,
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
                    recyclerView.setAdapter(adapterProduct);
                    adapterProduct.notifyDataSetChanged();
                });
    }

    private void openSingleProductFragment(final Product product) {
        SingleProductFragment fragment = SingleProductFragment.newInstance(product);
        addFragment(fragment);
    }

    private void addFragment(final Fragment fragment) {


//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.container, fragment)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .addToBackStack(null)
//                .commit();

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