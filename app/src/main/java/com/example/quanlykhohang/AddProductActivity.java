package com.example.quanlykhohang;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

public class AddProductActivity extends AppCompatActivity {

    private EditText etProductName;
    private EditText etProductPrice;
    private EditText etProductQuantity;
    private Spinner spCategory;
    private MaterialSwitch swActive;
    private EditText etProductDescription;
    private Button btnSaveProduct;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);

        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductQuantity = findViewById(R.id.etProductQuantity);
        spCategory = findViewById(R.id.spCategory);
        swActive = findViewById(R.id.swActive);
        etProductDescription = findViewById(R.id.etProductDescription);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = etProductName.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String quantityStr = etProductQuantity.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etProductName.setError("Tên sản phẩm không được để trống");
            etProductName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etProductPrice.setError("Giá bán không được để trống");
            etProductPrice.requestFocus();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
            if (price <= 0) {
                etProductPrice.setError("Giá bán phải lớn hơn 0");
                etProductPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etProductPrice.setError("Giá bán không hợp lệ");
            etProductPrice.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(quantityStr)) {
            etProductQuantity.setError("Số lượng không được để trống");
            etProductQuantity.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                etProductQuantity.setError("Số lượng không được âm");
                etProductQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etProductQuantity.setError("Số lượng không hợp lệ");
            etProductQuantity.requestFocus();
            return;
        }

        String category = spCategory.getSelectedItem().toString();
        int active = swActive.isChecked() ? 1 : 0;

        Product product = new Product(name, price, quantity, category, active, description);
        long result = dbHelper.insertProduct(product);

        if (result != -1) {
            Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
