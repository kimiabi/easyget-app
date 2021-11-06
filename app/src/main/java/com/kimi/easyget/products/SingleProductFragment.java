package com.kimi.easyget.products;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alespero.expandablecardview.ExpandableCardView;
import com.bumptech.glide.Glide;
import com.kimi.easyget.R;
import com.kimi.easyget.cart.model.ProductTransaction;
import com.kimi.easyget.offer.adapter.AdapterOffers;
import com.kimi.easyget.products.models.Product;
import com.kimi.easyget.products.models.ProductTransactionViewModel;

import static java.util.Objects.isNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String PRODUCTO = "PRODUCTO";
    private static final int DEFAULT_QUANTITY = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private Product product;

    public SingleProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment SingleProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleProductFragment newInstance(final Product product) {
        SingleProductFragment fragment = new SingleProductFragment();
        Bundle args = new Bundle();
        args.putSerializable(PRODUCTO, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable(PRODUCTO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final ExpandableCardView featuresProductCard = view.findViewById(R.id.features_product);

        setContent(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setContent(final View view) {
        final TextView productName = view.findViewById(R.id.product_name);
        final ImageView productPhoto = view.findViewById(R.id.product_photo);
        final TextView productPrice = view.findViewById(R.id.product_price);
        final Button btnMinusProduct = view.findViewById(R.id.btn_minus_product);
        final Button btnPlusProduct = view.findViewById(R.id.btn_plus_product);
        final Button btnAddCart = view.findViewById(R.id.add_cart);
        final TextView productQuantity = view.findViewById(R.id.product_quantity);
        final TextView productDescription = view.findViewById(R.id.product_description);

        if (!isNull(product)){
            final ProductTransaction productTransaction = getProductTransactionResource(product);

            productName.setText(productTransaction.getName());
            productPrice.setText(productTransaction.getTotalPrice());
            productQuantity.setText(productTransaction.getTotalQuantity());
            productDescription.setText(productTransaction.getDescription());

            if (!isNull(productTransaction.getPhotoUrl()) && !productTransaction.getPhotoUrl().isEmpty()) {
                Glide.with(getContext())
                        .load(productTransaction.getPhotoUrl())
                        .into(productPhoto);
            } else {
                Glide.with(getContext())
                        .load(R.drawable.no_picture)
                        .into(productPhoto);
            }

            btnPlusProduct.setOnClickListener(view1 -> {
                int totalQuantity = Integer.parseInt(productTransaction.getTotalQuantity());
                totalQuantity ++;
                Double totalAmount = Double.parseDouble(productTransaction.getPrice()) * totalQuantity;

                productQuantity.setText(String.valueOf(totalQuantity));
                productPrice.setText(String.valueOf(totalAmount));

                productTransaction.setTotalQuantity(String.valueOf(totalQuantity));
                productTransaction.setTotalPrice(String.valueOf(totalAmount));
            });

            btnMinusProduct.setOnClickListener(view2 -> {
                int totalQuantity = Integer.parseInt(productTransaction.getTotalQuantity());

                if (totalQuantity > 1) {
                    totalQuantity --;

                    Double totalAmount = Double.parseDouble(productTransaction.getPrice()) * totalQuantity;

                    productQuantity.setText(String.valueOf(totalQuantity));
                    productPrice.setText(String.valueOf(totalAmount));

                    productTransaction.setTotalQuantity(String.valueOf(totalQuantity));
                    productTransaction.setTotalPrice(String.valueOf(totalAmount));
                }
            });

            ProductTransactionViewModel productTransactionViewModel = new ViewModelProvider(requireActivity()).get(ProductTransactionViewModel.class);
            btnAddCart.setOnClickListener(view1 -> {
                productTransactionViewModel.selectProduct(productTransaction);
            });

        }

    }

    private ProductTransaction getProductTransactionResource(final Product product) {
        return ProductTransaction.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .photoUrl(product.getPhoto_url())
                .price(product.getPrice())
                .totalPrice(product.getPrice())
                .totalQuantity(String.valueOf(DEFAULT_QUANTITY))
                .offer(product.isOffer())
                .enabled(product.isEnabled())
                .build();
    }
}