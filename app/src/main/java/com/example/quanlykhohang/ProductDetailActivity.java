package com.example.quanlykhohang;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvDetailName;
    private TextView tvDetailLowStockWarning;
    private TextView tvDetailCategory;
    private TextView tvDetailStatus;
    private TextView tvDetailPrice;
    private TextView tvDetailQuantity;
    private TextView tvDetailStockValue;
    private TextView tvDetailDescription;

    private Button btnAddOne;
    private Button btnSellOne;
    private Button btnBack;
    private Button btnDeleteProduct;

    private DatabaseHelper dbHelper;
    private int productId = -1;
    private Product currentProduct;
    private NumberFormat currencyFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);
        currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailLowStockWarning = findViewById(R.id.tvDetailLowStockWarning);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity);
        tvDetailStockValue = findViewById(R.id.tvDetailStockValue);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        btnAddOne = findViewById(R.id.btnAddOne);
        btnSellOne = findViewById(R.id.btnSellOne);
        btnBack = findViewById(R.id.btnBack);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);

        btnAddOne.setOnClickListener(v -> addOneQuantity());
        btnSellOne.setOnClickListener(v -> sellOneQuantity());
        btnBack.setOnClickListener(v -> finish());
        btnDeleteProduct.setOnClickListener(v -> confirmDeleteProduct());

        loadProductDetails();
    }

    private void loadProductDetails() {
        currentProduct = dbHelper.getProductById(productId);
        if (currentProduct == null) {
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailName.setText(currentProduct.getName());
        tvDetailCategory.setText("Loại: " + currentProduct.getCategory());

        if (currentProduct.getActive() == 1) {
            tvDetailStatus.setText(R.string.status_active);
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, R.color.green_success));
        } else {
            tvDetailStatus.setText(R.string.status_inactive);
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, R.color.red_warning));
        }

        tvDetailPrice.setText(currencyFormatter.format(currentProduct.getPrice()) + " đ");
        tvDetailQuantity.setText(String.valueOf(currentProduct.getQuantity()));

        long stockValue = (long) currentProduct.getPrice() * currentProduct.getQuantity();
        tvDetailStockValue.setText(currencyFormatter.format(stockValue) + " đ");

        String desc = currentProduct.getDescription();
        if (TextUtils.isEmpty(desc)) {
            tvDetailDescription.setText("(Không có mô tả)");
        } else {
            tvDetailDescription.setText(desc);
        }

        if (currentProduct.getQuantity() < 5) {
            tvDetailLowStockWarning.setVisibility(View.VISIBLE);
            tvDetailQuantity.setTextColor(ContextCompat.getColor(this, R.color.red_warning));
        } else {
            tvDetailLowStockWarning.setVisibility(View.GONE);
            tvDetailQuantity.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    private void addOneQuantity() {
        if (currentProduct == null) return;

        int newQty = currentProduct.getQuantity() + 1;
        boolean success = dbHelper.updateQuantity(productId, newQty);
        if (success) {
            Toast.makeText(this, "Đã nhập thêm 1 sản phẩm", Toast.LENGTH_SHORT).show();
            loadProductDetails();
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void sellOneQuantity() {
        if (currentProduct == null) return;

        if (currentProduct.getQuantity() == 0) {
            Toast.makeText(this, "Hết hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        int newQty = currentProduct.getQuantity() - 1;
        boolean success = dbHelper.updateQuantity(productId, newQty);
        if (success) {
            Toast.makeText(this, "Đã bán 1 sản phẩm", Toast.LENGTH_SHORT).show();
            loadProductDetails();
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteProduct() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean success = dbHelper.deleteProduct(productId);
                    if (success) {
                        Toast.makeText(ProductDetailActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
