package com.kimi.easyget.categories;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kimi.easyget.R;
import com.kimi.easyget.cart.model.ProductTransaction;
import com.kimi.easyget.categories.models.Category;
import com.kimi.easyget.offer.adapter.AdapterOffers;
import com.kimi.easyget.products.models.Product;
import com.kimi.easyget.products.models.ProductTransactionViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {

    private FirebaseFirestore db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerCategories = view.findViewById(R.id.recycler_categories);
        setRecyclerCategoriesContent(recyclerCategories);
        
        super.onViewCreated(view, savedInstanceState);
    }

    private void setRecyclerCategoriesContent(final RecyclerView recyclerCategories) {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerCategories.setLayoutManager(gridLayoutManager);

        db.collection("categories")
                .whereEqualTo("enable", true)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w("ERR", "Listen failed.", e);
                        return;
                    }
                    final List<Category> categories = value.toObjects(Category.class);
//                    final AdapterOffers adapterOffers = new AdapterOffers(categories, getContext(),
//                            new AdapterOffers.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(Product product) {
//                                    final ProductTransaction productTransaction = getProductTransactionResource(product);
//                                    productTransactionViewModel.selectProduct(productTransaction);
//                                }
//                            });
//                    recyclerCategories.setAdapter(adapterOffers);
//                    adapterOffers.notifyDataSetChanged();
                });

    }
}