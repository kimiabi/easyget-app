package com.kimi.easyget.offer.adapter;

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
import com.kimi.easyget.products.models.Product;

import java.util.List;

import static java.util.Objects.isNull;

public class AdapterOffers extends RecyclerView.Adapter<AdapterOffers.ViewHolder> {

    private List<Product> products;
    private Context context;
    private final OnItemClickListener listener;

    public AdapterOffers(final List<Product> offers,
                         final Context context,
                         final OnItemClickListener listener) {
        this.products = offers;
        this.context = context;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(final Product product);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_offer, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final Product product = products.get(i);
        viewHolder.offerName.setText(product.getName());

        ImageView imageView = ((ViewHolder) viewHolder).offerPhoto;

        if (!isNull(product.getPhoto_url()) && !product.getPhoto_url().isEmpty()) {
            Glide.with(context)
                    .load(product.getPhoto_url())
                    .into(imageView);
        }

        viewHolder.bin(product, i, listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView offerName;
        Button addOfferBtn;
        ImageView offerPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            offerName = itemView.findViewById(R.id.offer_name);
            addOfferBtn = itemView.findViewById(R.id.add_offer_btn);
            offerPhoto = itemView.findViewById(R.id.offer_photo);

        }

        public void bin(final Product product, final int i, final OnItemClickListener listener) {
            addOfferBtn.setOnClickListener(view -> listener.onItemClick(product));
        }
    }
}
