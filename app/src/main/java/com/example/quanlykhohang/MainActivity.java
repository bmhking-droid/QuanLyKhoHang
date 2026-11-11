package com.example.quanlykhohang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalProducts;
    private TextView tvTotalValue;
    private TextView tvLowStockCount;
    private TextView tvEmptyList;
    private RecyclerView rvProducts;
    private ExtendedFloatingActionButton btnAddProduct;

    private DatabaseHelper dbHelper;
    private ProductAdapter adapter;
    private ArrayList<Product> productList;
    private NumberFormat currencyFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        tvEmptyList = findViewById(R.id.tvEmptyList);
        rvProducts = findViewById(R.id.rvProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList, product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            startActivity(intent);
        });
        rvProducts.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        productList = dbHelper.getAllProducts();
        adapter.updateList(productList);

        if (productList.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
        }

        int totalCount = productList.size();
        long totalValue = dbHelper.getTotalValue();
        int lowStock = dbHelper.countLowStock();

        tvTotalProducts.setText(getString(R.string.total_products, totalCount));
        tvTotalValue.setText(currencyFormatter.format(totalValue) + " đ");
        tvLowStockCount.setText(getString(R.string.low_stock_count, lowStock));
    }
}
