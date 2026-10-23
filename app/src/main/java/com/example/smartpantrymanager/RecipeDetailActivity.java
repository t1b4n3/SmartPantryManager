package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.RecipeIngredientAdapter;
import com.example.smartpantrymanager.model.Recipe;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvInstructions = findViewById(R.id.tvDetailInstructions);
        RecyclerView rvIngredients = findViewById(R.id.rvRecipeIngredients);

        if (getIntent() != null && getIntent().hasExtra("RECIPE")) {
            Recipe recipe = (Recipe) getIntent().getSerializableExtra("RECIPE");
            if (recipe != null) {
                tvTitle.setText(recipe.getName());
                tvInstructions.setText(recipe.getInstructions());

                rvIngredients.setLayoutManager(new LinearLayoutManager(this));
                RecipeIngredientAdapter adapter = new RecipeIngredientAdapter(recipe.getIngredients());
                rvIngredients.setAdapter(adapter);
            }
        }
    }
}
