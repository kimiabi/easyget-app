package com.kimi.easyget.categories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kimi.easyget.R;
import com.kimi.easyget.categories.models.Category;

import java.util.List;

public class AdapterCategories extends RecyclerView.Adapter<AdapterCategories.ViewHolder> {
    private List<Category> categories;
    private Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClik(final Category category);
    }

    public AdapterCategories(List<Category> categories, Context context, OnItemClickListener listener) {
        this.categories = categories;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_categories, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Category category = categories.get(position);

        viewHolder.categoryName.setText(category.getName());

        viewHolder.bin(category, position, listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.category_name);
        }

        public void bin(final Category category, final int position, final OnItemClickListener listener) {
            itemView.setOnClickListener(view -> listener.onItemClik(category));
        }
    }
}
