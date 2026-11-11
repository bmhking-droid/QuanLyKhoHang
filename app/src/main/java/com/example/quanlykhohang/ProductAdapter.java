package com.example.quanlykhohang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private final Context context;
    private ArrayList<Product> productList;
    private final OnItemClickListener listener;
    private final NumberFormat currencyFormatter;

    public ProductAdapter(Context context, ArrayList<Product> productList, OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        this.currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    public void updateList(ArrayList<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvCategory.setText("Loại: " + product.getCategory());
        holder.tvPrice.setText(currencyFormatter.format(product.getPrice()) + " đ");
        holder.tvQuantity.setText("SL: " + product.getQuantity());

        if (product.getQuantity() < 5) {
            holder.tvLowStockBadge.setVisibility(View.VISIBLE);
            holder.tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.red_warning));
        } else {
            holder.tvLowStockBadge.setVisibility(View.GONE);
            holder.tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvLowStockBadge, tvCategory, tvPrice, tvQuantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvLowStockBadge = itemView.findViewById(R.id.tvLowStockBadge);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
