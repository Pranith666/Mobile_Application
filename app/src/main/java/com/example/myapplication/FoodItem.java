package com.example.myapplication;

public class FoodItem {
    private String foodName;
    private int calories;

    public FoodItem() {
        // Default constructor required for Firebase
    }

    public FoodItem(String foodName, int calories) {
        this.foodName = foodName;
        this.calories = calories;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}

