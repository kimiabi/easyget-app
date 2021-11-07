package com.kimi.easyget.products.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kimi.easyget.R;
import com.kimi.easyget.offer.adapter.AdapterOffers;
import com.kimi.easyget.products.models.Product;

import java.util.List;

import static java.util.Objects.isNull;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.ViewHolder> {
    public static final String CURRENCY = "Q ";
    private List<Product> products;
    private Context context;
    private final OnItemClickListener listener;

    public AdapterProduct(final List<Product> products,
                          final Context context,
                          final OnItemClickListener listener) {
        this.products = products;
        this.context = context;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(final Product product);
        void onItemClickSingleProduct(final Product product);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_product, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final Product product = products.get(i);
        viewHolder.productName.setText(product.getName());

        final String price = product.isOffer() ? product.getOfferPrice() : product.getPrice();
        viewHolder.productPrice.setText(CURRENCY + price);

        ImageView imageView = ((ViewHolder) viewHolder).productPhoto;

        if (!isNull(product.getPhoto_url()) && !product.getPhoto_url().isEmpty()) {
            Glide.with(context)
                    .load(product.getPhoto_url())
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.no_picture)
                    .into(imageView);
        }

        viewHolder.bin(product, i, listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        Button addProductBtn;
        ImageView productPhoto;
        TextView productPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            addProductBtn = itemView.findViewById(R.id.add_product_btn);
            productPhoto = itemView.findViewById(R.id.product_photo);
            productPrice = itemView.findViewById(R.id.product_price);
        }

        public void bin(final Product product, final int i, final OnItemClickListener listener) {

            itemView.setOnClickListener(view -> listener.onItemClickSingleProduct(product));
            addProductBtn.setOnClickListener(view -> listener.onItemClick(product));
        }
    }
}
