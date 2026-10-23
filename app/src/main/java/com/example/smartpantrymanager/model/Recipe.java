package com.example.smartpantrymanager.model;

import java.io.Serializable;
import java.util.List;

public class Recipe implements Serializable {
    private long id;
    private String name;
    private String instructions;
    private List<RecipeIngredient> ingredients;

    public Recipe(long id, String name, String instructions, List<RecipeIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }
}
