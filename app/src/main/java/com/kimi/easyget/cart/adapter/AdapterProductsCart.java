package com.kimi.easyget.cart.adapter;

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
import com.kimi.easyget.cart.model.ProductTransaction;

import java.util.List;

import static java.util.Objects.isNull;

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
        void onItemClickDelete(final ProductTransaction productTransaction);
        void updateTotalAmount(final int index, final ProductTransaction productTransaction);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_cart_products, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final ProductTransaction product = productTransactions.get(i);

        ImageView imageView = ((ViewHolder) viewHolder).productPhoto;

        if (!isNull(product.getPhotoUrl()) && !product.getPhotoUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getPhotoUrl())
                    .into(imageView);
        }

        viewHolder.productName.setText(product.getName());
        viewHolder.productPrice.setText(product.getTotalPrice());
        viewHolder.productQuantity.setText(product.getTotalQuantity());

        viewHolder.bin(product, i, listener);

    }

    @Override
    public int getItemCount() {
        return productTransactions.size();
    }

    public void updateItem(int index, ProductTransaction productTransaction){
        productTransactions.set(index, productTransaction);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productPhoto, productDelete;
        TextView productName, productPrice, productQuantity;
        Button btnMinus, btnPlus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productPhoto = itemView.findViewById(R.id.product_photo_cart);
            productDelete = itemView.findViewById(R.id.product_delete_cart);
            productName = itemView.findViewById(R.id.product_name_cart);
            productPrice = itemView.findViewById(R.id.product_price_cart);
            productQuantity = itemView.findViewById(R.id.product_quantity_cart);
            btnMinus = itemView.findViewById(R.id.btn_minus_cart);
            btnPlus = itemView.findViewById(R.id.btn_plus_cart);
        }

        public void bin(final ProductTransaction product, final int index, final OnItemClickListener listener) {
            productDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClickDelete(product);
                }
            });

            btnPlus.setOnClickListener(view -> {
                int totalQuantity = Integer.parseInt(product.getTotalQuantity());
                totalQuantity ++;

                Double totalAmount = Double.parseDouble(product.getPrice()) * totalQuantity;

                product.setTotalQuantity(String.valueOf(totalQuantity));
                product.setTotalPrice(String.valueOf(totalAmount));
                updateItem(index, product);
                listener.updateTotalAmount(index, product);

            });

            btnMinus.setOnClickListener(view -> {


                int totalQuantity = Integer.parseInt(product.getTotalQuantity());

                if (totalQuantity > 1) {
                    totalQuantity --;

                    Double totalAmount = Double.parseDouble(product.getPrice()) * totalQuantity;

                    product.setTotalQuantity(String.valueOf(totalQuantity));
                    product.setTotalPrice(String.valueOf(totalAmount));
                    updateItem(index, product);
                    listener.updateTotalAmount(index, product);
                }

            });
        }
    }
}
