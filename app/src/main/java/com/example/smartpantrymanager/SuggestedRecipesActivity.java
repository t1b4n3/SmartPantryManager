package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.model.Recipe;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView rvSuggested;
    private TextView tvZeroMatch;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);
        rvSuggested = findViewById(R.id.rvSuggestedRecipes);
        tvZeroMatch = findViewById(R.id.tvZeroMatch);

        rvSuggested.setLayoutManager(new LinearLayoutManager(this));
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        List<Recipe> suggestedList = dbHelper.getSuggestedRecipes();
        if (suggestedList.isEmpty()) {
            tvZeroMatch.setVisibility(View.VISIBLE);
            rvSuggested.setVisibility(View.GONE);
        } else {
            tvZeroMatch.setVisibility(View.GONE);
            rvSuggested.setVisibility(View.VISIBLE);
            RecipeAdapter adapter = new RecipeAdapter(suggestedList, this);
            rvSuggested.setAdapter(adapter);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
        intent.putExtra("RECIPE", recipe);
        startActivity(intent);
    }
}
