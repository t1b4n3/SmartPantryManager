package com.example.smartpantrymanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table Pantry
    public static final String TABLE_PANTRY = "pantry";
    public static final String COLUMN_PANTRY_ID = "id";
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QUANTITY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";
    public static final String COLUMN_PANTRY_EXPIRY = "expiry_date";

    // Table Recipes
    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_RECIPE_ID = "id";
    public static final String COLUMN_RECIPE_NAME = "name";
    public static final String COLUMN_RECIPE_INSTRUCTIONS = "instructions";

    // Table Recipe Ingredients
    public static final String TABLE_RECIPE_ING = "recipe_ingredients";
    public static final String COLUMN_RI_ID = "id";
    public static final String COLUMN_RI_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RI_NAME = "ingredient_name";
    public static final String COLUMN_RI_QUANTITY = "required_quantity";
    public static final String COLUMN_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COLUMN_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PANTRY_NAME + " TEXT NOT NULL, " +
                COLUMN_PANTRY_QUANTITY + " REAL NOT NULL, " +
                COLUMN_PANTRY_UNIT + " TEXT NOT NULL, " +
                COLUMN_PANTRY_EXPIRY + " TEXT)";
        db.execSQL(createPantryTable);

        String createRecipesTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                COLUMN_RECIPE_INSTRUCTIONS + " TEXT NOT NULL)";
        db.execSQL(createRecipesTable);

        String createRecipeIngTable = "CREATE TABLE " + TABLE_RECIPE_ING + " (" +
                COLUMN_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COLUMN_RI_NAME + " TEXT NOT NULL, " +
                COLUMN_RI_QUANTITY + " REAL NOT NULL, " +
                COLUMN_RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ") ON DELETE CASCADE)";
        db.execSQL(createRecipeIngTable);

        seedInitialRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_ING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // Pantry CRUD Operations
    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, item.getName().trim().toLowerCase());
        values.put(COLUMN_PANTRY_QUANTITY, item.getQuantity());
        values.put(COLUMN_PANTRY_UNIT, item.getUnit().trim().toLowerCase());
        values.put(COLUMN_PANTRY_EXPIRY, item.getExpiryDate());
        long id = db.insert(TABLE_PANTRY, null, values);
        db.close();
        return id;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, COLUMN_PANTRY_NAME + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT));
                String expiry = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_EXPIRY));
                list.add(new PantryItem(id, name, qty, unit, expiry));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, item.getName().trim().toLowerCase());
        values.put(COLUMN_PANTRY_QUANTITY, item.getQuantity());
        values.put(COLUMN_PANTRY_UNIT, item.getUnit().trim().toLowerCase());
        values.put(COLUMN_PANTRY_EXPIRY, item.getExpiryDate());
        int rows = db.update(TABLE_PANTRY, values, COLUMN_PANTRY_ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
        return rows;
    }

    public void deletePantryItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COLUMN_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Recipe Operations & Strict Matching
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INSTRUCTIONS));
                List<RecipeIngredient> ingredients = getIngredientsForRecipe(db, id);
                recipes.add(new Recipe(id, name, instructions, ingredients));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return recipes;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_RECIPE_ING, null, COLUMN_RI_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RI_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RI_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RI_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RI_UNIT));
                list.add(new RecipeIngredient(id, recipeId, name, qty, unit));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // Strict Matching Core Logic
    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> allRecipes = getAllRecipes();
        List<PantryItem> pantryItems = getAllPantryItems();
        List<Recipe> suggested = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            boolean canMake = true;
            for (RecipeIngredient req : recipe.getIngredients()) {
                boolean foundSufficient = false;
                for (PantryItem pantry : pantryItems) {
                    if (pantry.getName().equalsIgnoreCase(req.getIngredientName().trim())) {
                        // Check unit compatibility or quantity
                        // If units match or are compatible (e.g. pcs, grams, ml, cups), verify quantity
                        if (pantry.getQuantity() >= req.getRequiredQuantity()) {
                            foundSufficient = true;
                            break;
                        }
                    }
                }
                if (!foundSufficient) {
                    canMake = false;
                    break;
                }
            }
            if (canMake && !recipe.getIngredients().isEmpty()) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    // Seed 16 initial recipes
    private void seedInitialRecipes(SQLiteDatabase db) {
        addRecipeWithIngredients(db, "Scrambled Eggs on Toast",
                "1. Toast the bread slices until golden brown.\n2. Melt butter in a pan over medium heat.\n3. Whisk eggs and pour into the pan, stirring gently until soft curds form.\n4. Serve warm scrambled eggs over the buttered toast.",
                new String[]{"egg", "bread", "butter"}, new double[]{2.0, 2.0, 1.0}, new String[]{"pcs", "slices", "tbsp"});

        addRecipeWithIngredients(db, "Simple Pasta Marinara",
                "1. Boil pasta in salted water until al dente, then drain.\n2. Heat olive oil in a skillet and sauté minced garlic until fragrant.\n3. Add tomato sauce and simmer for 5 minutes.\n4. Toss the cooked pasta in the marinara sauce and serve hot.",
                new String[]{"pasta", "tomato sauce", "garlic", "olive oil"}, new double[]{200.0, 1.5, 2.0, 1.0}, new String[]{"grams", "cups", "cloves", "tbsp"});

        addRecipeWithIngredients(db, "Garlic Butter Rice",
                "1. Cook white rice with water until fluffy.\n2. In a separate pan, melt butter and sauté minced garlic.\n3. Stir the garlic butter thoroughly into the cooked rice and add a pinch of salt.",
                new String[]{"rice", "garlic", "butter", "salt"}, new double[]{2.0, 3.0, 2.0, 1.0}, new String[]{"cups", "cloves", "tbsp", "tsp"});

        addRecipeWithIngredients(db, "Classic Omelette",
                "1. Crack eggs into a bowl, add milk, salt, and beat well.\n2. Melt butter in a non-stick skillet over medium heat.\n3. Pour in the egg mixture, sprinkle cheese on top, fold in half when edges set.",
                new String[]{"egg", "milk", "cheese", "butter"}, new double[]{3.0, 2.0, 50.0, 1.0}, new String[]{"pcs", "tbsp", "grams", "tbsp"});

        addRecipeWithIngredients(db, "Tomato Soup",
                "1. Sauté chopped onion and garlic in butter until soft.\n2. Pour in tomato sauce and milk, bring to a gentle simmer.\n3. Season with salt and blend or stir until smooth. Serve warm.",
                new String[]{"tomato sauce", "onion", "garlic", "milk", "butter"}, new double[]{2.0, 1.0, 2.0, 1.0, 1.0}, new String[]{"cups", "pcs", "cloves", "cups", "tbsp"});

        addRecipeWithIngredients(db, "Cheesy Toast",
                "1. Preheat oven or toaster oven.\n2. Spread butter on bread slices and top generously with cheese.\n3. Toast until cheese is melted and bubbling.",
                new String[]{"bread", "cheese", "butter"}, new double[]{2.0, 60.0, 1.0}, new String[]{"slices", "grams", "tbsp"});

        addRecipeWithIngredients(db, "Pancakes",
                "1. Mix flour, sugar, milk, and eggs in a bowl until smooth.\n2. Melt butter in a hot griddle or pan.\n3. Pour batter to form pancakes, cook until bubbles appear, then flip until golden.",
                new String[]{"flour", "milk", "egg", "sugar", "butter"}, new double[]{1.5, 1.0, 1.0, 2.0, 2.0}, new String[]{"cups", "cups", "pcs", "tbsp", "tbsp"});

        addRecipeWithIngredients(db, "Mashed Potatoes",
                "1. Boil peeled potatoes in salted water until fork tender.\n2. Drain potatoes and mash with butter, milk, and salt until creamy.",
                new String[]{"potato", "milk", "butter", "salt"}, new double[]{4.0, 0.5, 2.0, 1.0}, new String[]{"pcs", "cups", "tbsp", "tsp"});

        addRecipeWithIngredients(db, "Vegetable Stir Fry",
                "1. Heat vegetable oil in a wok or large skillet over high heat.\n2. Add sliced carrots, cabbage, and onion, stir-frying briskly until tender-crisp.\n3. Pour in soy sauce and toss well before serving.",
                new String[]{"carrot", "cabbage", "onion", "soy sauce", "vegetable oil"}, new double[]{2.0, 2.0, 1.0, 2.0, 2.0}, new String[]{"pcs", "cups", "pcs", "tbsp", "tbsp"});

        addRecipeWithIngredients(db, "Boiled Eggs",
                "1. Place eggs in a saucepan and cover with water.\n2. Bring to a boil, then simmer for 8 minutes for hard-boiled.\n3. Cool in ice water, peel, and sprinkle with salt.",
                new String[]{"egg", "salt"}, new double[]{2.0, 1.0}, new String[]{"pcs", "tsp"});

        addRecipeWithIngredients(db, "Oatmeal Porridge",
                "1. Combine oats and milk in a saucepan.\n2. Cook over medium heat, stirring frequently, until thickened.\n3. Stir in sugar and serve warm.",
                new String[]{"oats", "milk", "sugar"}, new double[]{1.0, 1.5, 1.0}, new String[]{"cups", "cups", "tbsp"});

        addRecipeWithIngredients(db, "Garlic Bread",
                "1. Slice bread loaf horizontally.\n2. Mix softened butter with minced garlic and spread over bread.\n3. Bake in oven at 180°C for 10 minutes until golden.",
                new String[]{"bread", "garlic", "butter"}, new double[]{1.0, 3.0, 2.0}, new String[]{"loaf", "cloves", "tbsp"});

        addRecipeWithIngredients(db, "Simple Salad",
                "1. Wash and chop lettuce and tomatoes.\n2. Toss together in a bowl with olive oil and a pinch of salt.",
                new String[]{"lettuce", "tomato", "olive oil", "salt"}, new double[]{2.0, 2.0, 1.0, 1.0}, new String[]{"cups", "pcs", "tbsp", "tsp"});

        addRecipeWithIngredients(db, "Baked Potato",
                "1. Pierce potatoes with a fork and bake at 200°C for 45 minutes.\n2. Cut open and stuff with butter and cheese.",
                new String[]{"potato", "butter", "cheese"}, new double[]{2.0, 2.0, 50.0}, new String[]{"pcs", "tbsp", "grams"});

        addRecipeWithIngredients(db, "Fried Rice",
                "1. Heat vegetable oil in a pan, scramble an egg, and set aside.\n2. Sauté chopped onion and cooked rice in the pan.\n3. Add soy sauce and mixed egg, stir fry until hot.",
                new String[]{"rice", "egg", "onion", "soy sauce", "vegetable oil"}, new double[]{2.0, 1.0, 1.0, 2.0, 1.0}, new String[]{"cups", "pcs", "pcs", "tbsp", "tbsp"});

        addRecipeWithIngredients(db, "Pasta Aglio e Olio",
                "1. Boil pasta until al dente and drain.\n2. Heat olive oil and generously sauté sliced garlic until golden.\n3. Toss pasta in the garlic oil with salt.",
                new String[]{"pasta", "olive oil", "garlic", "salt"}, new double[]{200.0, 3.0, 4.0, 1.0}, new String[]{"grams", "tbsp", "cloves", "tsp"});
    }

    private void addRecipeWithIngredients(SQLiteDatabase db, String name, String instructions, String[] ingNames, double[] ingQtys, String[] ingUnits) {
        ContentValues rValues = new ContentValues();
        rValues.put(COLUMN_RECIPE_NAME, name);
        rValues.put(COLUMN_RECIPE_INSTRUCTIONS, instructions);
        long recipeId = db.insert(TABLE_RECIPES, null, rValues);

        for (int i = 0; i < ingNames.length; i++) {
            ContentValues riValues = new ContentValues();
            riValues.put(COLUMN_RI_RECIPE_ID, recipeId);
            riValues.put(COLUMN_RI_NAME, ingNames[i].trim().toLowerCase());
            riValues.put(COLUMN_RI_QUANTITY, ingQtys[i]);
            riValues.put(COLUMN_RI_UNIT, ingUnits[i].trim().toLowerCase());
            db.insert(TABLE_RECIPE_ING, null, riValues);
        }
    }
}
