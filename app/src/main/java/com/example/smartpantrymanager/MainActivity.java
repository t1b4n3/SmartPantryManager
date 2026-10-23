package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PantryAdapter.OnPantryClickListener {

    private DatabaseHelper dbHelper;
    private RecyclerView rvPantry;
    private TextView tvEmptyPantry;
    private PantryAdapter adapter;
    private List<PantryItem> pantryItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);

        rvPantry = findViewById(R.id.rvPantry);
        tvEmptyPantry = findViewById(R.id.tvEmptyPantry);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddPantry);
        Button btnViewSuggested = findViewById(R.id.btnViewSuggested);

        rvPantry.setLayoutManager(new LinearLayoutManager(this));
        loadPantryData();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditPantryActivity.class);
            startActivity(intent);
        });

        btnViewSuggested.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SuggestedRecipesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryData();
    }

    private void loadPantryData() {
        pantryItemList = dbHelper.getAllPantryItems();
        if (pantryItemList.isEmpty()) {
            tvEmptyPantry.setVisibility(View.VISIBLE);
            rvPantry.setVisibility(View.GONE);
        } else {
            tvEmptyPantry.setVisibility(View.GONE);
            rvPantry.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new PantryAdapter(pantryItemList, this);
                rvPantry.setAdapter(adapter);
            } else {
                adapter.updateList(pantryItemList);
            }
        }
    }

    @Override
    public void onEditClick(PantryItem item) {
        Intent intent = new Intent(MainActivity.this, AddEditPantryActivity.class);
        intent.putExtra("PANTRY_ITEM", item);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(PantryItem item) {
        dbHelper.deletePantryItem(item.getId());
        Toast.makeText(this, "Deleted " + item.getName(), Toast.LENGTH_SHORT).show();
        loadPantryData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
