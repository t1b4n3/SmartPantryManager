package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditPantryActivity extends AppCompatActivity {

    private TextInputEditText etName, etQuantity, etUnit, etExpiry;
    private TextView tvFormTitle;
    private DatabaseHelper dbHelper;
    private PantryItem editingItem;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_pantry);

        dbHelper = new DatabaseHelper(this);

        tvFormTitle = findViewById(R.id.tvFormTitle);
        etName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        Button btnSave = findViewById(R.id.btnSavePantry);

        if (getIntent() != null && getIntent().hasExtra("PANTRY_ITEM")) {
            editingItem = (PantryItem) getIntent().getSerializableExtra("PANTRY_ITEM");
            if (editingItem != null) {
                isEditMode = true;
                tvFormTitle.setText("Edit Pantry Ingredient");
                etName.setText(editingItem.getName());
                etQuantity.setText(String.valueOf(editingItem.getQuantity()));
                etUnit.setText(editingItem.getUnit());
                etExpiry.setText(editingItem.getExpiryDate());
                btnSave.setText("Update Ingredient");
            }
        }

        btnSave.setOnClickListener(v -> savePantryItem());
    }

    private void savePantryItem() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String qtyStr = etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
        String unit = etUnit.getText() != null ? etUnit.getText().toString().trim() : "";
        String expiry = etExpiry.getText() != null ? etExpiry.getText().toString().trim() : "";

        // Input Validation
        if (name.isEmpty()) {
            etName.setError("Ingredient name is required");
            etName.requestFocus();
            return;
        }

        if (qtyStr.isEmpty()) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
            if (quantity <= 0) {
                etQuantity.setError("Quantity must be greater than zero");
                etQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Invalid quantity number");
            etQuantity.requestFocus();
            return;
        }

        if (unit.isEmpty()) {
            etUnit.setError("Unit is required (e.g. pcs, grams, cups)");
            etUnit.requestFocus();
            return;
        }

        if (isEditMode && editingItem != null) {
            editingItem.setName(name);
            editingItem.setQuantity(quantity);
            editingItem.setUnit(unit);
            editingItem.setExpiryDate(expiry);
            dbHelper.updatePantryItem(editingItem);
            Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            PantryItem newItem = new PantryItem(name, quantity, unit, expiry);
            dbHelper.addPantryItem(newItem);
            Toast.makeText(this, "Added to pantry!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
