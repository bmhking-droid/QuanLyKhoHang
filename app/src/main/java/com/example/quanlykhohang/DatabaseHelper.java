package com.example.quanlykhohang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    // Table users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Table products
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_PRODUCT_ID = "id";
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRODUCT_PRICE = "price";
    public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
    public static final String COLUMN_PRODUCT_CATEGORY = "category";
    public static final String COLUMN_PRODUCT_ACTIVE = "active";
    public static final String COLUMN_PRODUCT_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_NAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL" +
                ");";
        db.execSQL(createUsersTable);

        // Create products table
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_CATEGORY + " TEXT NOT NULL, " +
                COLUMN_PRODUCT_ACTIVE + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_DESCRIPTION + " TEXT" +
                ");";
        db.execSQL(createProductsTable);

        // Insert seed data
        insertInitialData(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Insert sample admin user
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USER_NAME, "admin");
        userValues.put(COLUMN_USER_PASSWORD, "123456");
        db.insert(TABLE_USERS, null, userValues);

        // Insert sample products
        insertProductDirect(db, "Chuột không dây", 150000, 20, "Điện tử", 1, "Chuột không dây tiện lợi");
        insertProductDirect(db, "Bàn phím cơ", 450000, 3, "Điện tử", 1, "Bàn phím cơ gõ êm");
        insertProductDirect(db, "Sổ tay A5", 25000, 40, "Văn phòng phẩm", 1, "Sổ tay ghi chép A5 200 trang");
    }

    private void insertProductDirect(SQLiteDatabase db, String name, int price, int quantity, String category, int active, String description) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(COLUMN_PRODUCT_CATEGORY, category);
        values.put(COLUMN_PRODUCT_ACTIVE, active);
        values.put(COLUMN_PRODUCT_DESCRIPTION, description);
        db.insert(TABLE_PRODUCTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Check login
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_NAME + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    // Insert user
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    // Insert product
    public long insertProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, p.getName());
        values.put(COLUMN_PRODUCT_PRICE, p.getPrice());
        values.put(COLUMN_PRODUCT_QUANTITY, p.getQuantity());
        values.put(COLUMN_PRODUCT_CATEGORY, p.getCategory());
        values.put(COLUMN_PRODUCT_ACTIVE, p.getActive());
        values.put(COLUMN_PRODUCT_DESCRIPTION, p.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    // Get all products
    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COLUMN_PRODUCT_ID + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID);
                int nameIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME);
                int priceIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE);
                int quantityIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY);
                int categoryIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY);
                int activeIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ACTIVE);
                int descIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION);

                do {
                    Product p = new Product(
                            cursor.getInt(idIdx),
                            cursor.getString(nameIdx),
                            cursor.getInt(priceIdx),
                            cursor.getInt(quantityIdx),
                            cursor.getString(categoryIdx),
                            cursor.getInt(activeIdx),
                            cursor.getString(descIdx)
                    );
                    productList.add(p);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return productList;
    }

    // Get product by ID
    public Product getProductById(int id) {
        Product p = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID);
                int nameIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME);
                int priceIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE);
                int quantityIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY);
                int categoryIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY);
                int activeIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ACTIVE);
                int descIdx = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION);

                p = new Product(
                        cursor.getInt(idIdx),
                        cursor.getString(nameIdx),
                        cursor.getInt(priceIdx),
                        cursor.getInt(quantityIdx),
                        cursor.getString(categoryIdx),
                        cursor.getInt(activeIdx),
                        cursor.getString(descIdx)
                );
            }
            cursor.close();
        }
        return p;
    }

    // Update quantity
    public boolean updateQuantity(int id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_QUANTITY, quantity);
        int rows = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // Delete product
    public boolean deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // Get total warehouse value: SUM(price * quantity)
    public long getTotalValue() {
        long total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_PRODUCT_PRICE + " * " + COLUMN_PRODUCT_QUANTITY + ") FROM " + TABLE_PRODUCTS;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total = cursor.getLong(0);
            }
            cursor.close();
        }
        return total;
    }

    // Count low stock products: COUNT where quantity < 5
    public int countLowStock() {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_QUANTITY + " < 5";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }
}
