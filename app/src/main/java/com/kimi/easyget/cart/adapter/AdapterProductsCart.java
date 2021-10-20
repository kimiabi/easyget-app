package com.kimi.easyget.cart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kimi.easyget.R;
import com.kimi.easyget.cart.model.ProductTransaction;

import java.util.List;

public class AdapterProductsCart extends RecyclerView.Adapter<AdapterProductsCart.ViewHolder> {
    private List<ProductTransaction> productTransactions;
    private Context context;
    private final OnItemClickListener listener;

    public AdapterProductsCart(final List<ProductTransaction> productTransactions,
                               final Context context,
                               final OnItemClickListener listener) {
        this.productTransactions = productTransactions;
        this.context = context;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(final ProductTransaction productTransaction);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_cart_products, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
