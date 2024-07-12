package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddFoodActivity1 extends AppCompatActivity {

    EditText editTextFoodName, editTextCalories;
    Button buttonAdd;
    ListView listViewFoodItems;
    List<FoodItem> foodItemList;
    FoodItemAdapter adapter;
    TextView textViewTotalCalories, email;

    FirebaseAuth mAuth;
    DatabaseReference db;
    int currentTotalCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food1);

        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextCalories = findViewById(R.id.editTextCalories);
        buttonAdd = findViewById(R.id.buttonAdd);
        listViewFoodItems = findViewById(R.id.listViewFoodItems);
        textViewTotalCalories = findViewById(R.id.textView11);
        email = findViewById(R.id.textView12);
        textViewTotalCalories.setText("");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("Users");

        foodItemList = new ArrayList<>();
        adapter = new FoodItemAdapter(this, R.layout.activity_foodviewinlist, foodItemList);
        listViewFoodItems.setAdapter(adapter);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            email.setText(user.getEmail());
        }

        buttonAdd.setOnClickListener(view -> {
            String foodName = editTextFoodName.getText().toString().trim();
            String calories = editTextCalories.getText().toString().trim();

            if (foodName.isEmpty() || calories.isEmpty()) {
                Toast.makeText(AddFoodActivity1.this, "Please enter food name and calories", Toast.LENGTH_SHORT).show();
                return;
            }

            int caloriesValue = Integer.parseInt(calories);

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                String userEmail = currentUser.getEmail();

                DatabaseReference userRef = db.child(userId);

                // Save the food item under FoodItems node
                String key = userRef.child("FoodItems").push().getKey();
                userRef.child("FoodItems").child(key).setValue(new FoodItem(foodName, caloriesValue));

                // Update total calories for the user
                userRef.child("foodCalories").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int currentTotalCalories = 0;
                        if (dataSnapshot.exists()) {
                            currentTotalCalories = dataSnapshot.getValue(Integer.class);
                        }

                        currentTotalCalories += caloriesValue;

                        // Update total calories TextView
                        textViewTotalCalories.setText("Total Calories: " + currentTotalCalories);

                        // Update total calories in the database
                        userRef.child("foodCalories").setValue(currentTotalCalories)
                                .addOnSuccessListener(aVoid -> {
                                    // Update ListView
                                    foodItemList.add(new FoodItem(foodName, caloriesValue));
                                    adapter.notifyDataSetChanged();

                                    // Show success message
                                    Toast.makeText(AddFoodActivity1.this, "Food item added successfully", Toast.LENGTH_SHORT).show();

                                    // Clear input fields
                                    editTextFoodName.setText("");
                                    editTextCalories.setText("");
                                })
                                .addOnFailureListener(e -> Toast.makeText(AddFoodActivity1.this, "Failed to add food item", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AddFoodActivity1.this, "Failed to add food item", Toast.LENGTH_SHORT).show();
                    }
                });

                // Store email and total calories in a separate table
                userRef.child("foodCaloriesTable").child(userEmail.replace(".", "_")).setValue(currentTotalCalories + caloriesValue);
            } else {
                Toast.makeText(AddFoodActivity1.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodItemList.clear();
                int[] totalCalories = {0};

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userEmail = currentUser.getEmail();
                    String userId = currentUser.getUid();

                    // Fetch food items for the current user's email
                    DatabaseReference userRef = db.child(userId).child("FoodItems");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                FoodItem foodItem = snapshot.getValue(FoodItem.class);
                                foodItemList.add(foodItem);
                                totalCalories[0] += foodItem.getCalories();
                            }
                            adapter.notifyDataSetChanged();
                            textViewTotalCalories.setText("Total Calories: " + totalCalories);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(AddFoodActivity1.this, "Failed to load food items", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddFoodActivity1.this, "Failed to load food items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class FoodItemAdapter extends ArrayAdapter<FoodItem> {

        private List<FoodItem> foodItemList;
        private Context context;

        public FoodItemAdapter(Context context, int resource, List<FoodItem> foodItemList) {
            super(context, resource, foodItemList);
            this.context = context;
            this.foodItemList = foodItemList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.activity_foodviewinlist, parent, false);
            }

            FoodItem foodItem = foodItemList.get(position);

            TextView foodNameTextView = convertView.findViewById(R.id.LIST_MACHINE_NAME);
            TextView caloriesTextView = convertView.findViewById(R.id.LIST_MACHINE_SHORT_DESCRIPTION);

            // Set data to your TextViews
            foodNameTextView.setText(foodItem.getFoodName());
            caloriesTextView.setText(String.valueOf(foodItem.getCalories()));

            return convertView;
        }
    }
}
