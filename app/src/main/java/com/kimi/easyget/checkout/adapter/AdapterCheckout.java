package com.kimi.easyget.checkout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kimi.easyget.R;
import com.kimi.easyget.cart.model.ProductTransaction;

import java.util.List;

public class AdapterCheckout extends RecyclerView.Adapter<AdapterCheckout.ViewHolder> {
    private List<ProductTransaction> products;
    private Context context;


    public AdapterCheckout(final List<ProductTransaction> products, final Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_products_checkout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        final ProductTransaction product = products.get(position);

        viewHolder.productQuantity.setText(product.getTotalQuantity());
        viewHolder.productName.setText(product.getName());
        viewHolder.productPrice.setText(product.getPrice());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productQuantity, productName, productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productQuantity = itemView.findViewById(R.id.product_quantity_ck);
            productName = itemView.findViewById(R.id.product_name_ck);
            productPrice = itemView.findViewById(R.id.product_price_ck);
        }
    }
}
