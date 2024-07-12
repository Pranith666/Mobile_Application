package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import android.os.CountDownTimer;

public class Page1 extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button startTimerButton, resetTimerButton, stopTimerButton;
    TextView timerTextView;
    CountDownTimer countDownTimer;
    Button button;
    TextView textView,t2,btn;
    FirebaseUser user;
    ImageView imageProfile,i1;
    Toolbar toolbar;
    Spinner spinner;
    DatabaseReference db;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1);
        toolbar = findViewById(R.id.actionToolbar);
        setSupportActionBar(toolbar);
        spinner = findViewById(R.id.spinner);
        mAuth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        startTimerButton = findViewById(R.id.startTimerButton);
        resetTimerButton = findViewById(R.id.resetTimerButton);
        stopTimerButton = findViewById(R.id.stopTimerButton);
        timerTextView = findViewById(R.id.timerTextView);
        btn = findViewById(R.id.textView5);
        i1=findViewById(R.id.imageView);
        i1.setImageResource(R.drawable.ic_gym_bench_50dp);

        db = FirebaseDatabase.getInstance().getReference().child("Exe");
        //t2=findViewById(R.id.textVieww);

        customAdapter = new CustomAdapter();
        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        resetTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        stopTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve values from EditText and Spinner
                EditText e1 = findViewById(R.id.editTextText1);
                EditText e2 = findViewById(R.id.editTextText2);
                EditText e3 = findViewById(R.id.editTextText3);

                String exerciseName = spinner.getSelectedItem().toString();
                int sets = Integer.parseInt(e1.getText().toString());
                int reps = Integer.parseInt(e2.getText().toString());
                int weight = Integer.parseInt(e3.getText().toString());

                // Create Exercise object
                Exercise exercise = new Exercise(exerciseName, sets, reps, weight);

                // Push Exercise object to Firebase Realtime Database
                db.push().setValue(exercise)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data successfully saved
                                Toast.makeText(Page1.this, "Exercise added successfully!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to save data
                                Toast.makeText(Page1.this, "Failed to add exercise!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        ListView listView = findViewById(R.id.listview);
        imageProfile = findViewById(R.id.imageProfile);
        listView.setAdapter(customAdapter);
        user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            textView.setText(user.getEmail());
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener to the ImageView
        // Set OnClickListener to the ImageView
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a PopupMenu
                PopupMenu popupMenu = new PopupMenu(Page1.this, imageProfile);

                // Inflate the popup_menu.xml
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                // Set a click listener for menu items
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Handle menu item clicks here
                        if(menuItem.getItemId()==R.id.menu_item1)
                        {
                            Intent intent=new Intent(getApplicationContext(), exerciselist.class);
                            startActivity(intent);
                        } else if (menuItem.getItemId()==R.id.menu_item2) {
                            //something
                            Intent intent=new Intent(getApplicationContext(), AddFoodActivity1.class);
                            startActivity(intent);

                        }
                        return true;
                    }
                });

                // Show the popup menu
                popupMenu.show();
            }
        });


        // Add exercises to the spinner
        List<String> exercises = new ArrayList<>();
        exercises.add("Exercise 1");
        exercises.add("Exercise 2");
        exercises.add("Exercise 3");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exercises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set an item click listener for the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Handle item selection
                String selectedExercise = exercises.get(position);
                //Toast.makeText(Page1.this, "Selected Exercise: " + selectedExercise, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        // Fetch data from Firebase and update the ListView
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Exercise> exerciseList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Exercise exercise = snapshot.getValue(Exercise.class);
                    exerciseList.add(exercise);
                }
                customAdapter.setExerciseList(exerciseList);
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    private void startTimer() {
        // Start a 10-minute countdown timer
        countDownTimer = new CountDownTimer(10 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the timer text view with the remaining time
                long minutes = millisUntilFinished / (60 * 1000);
                long seconds = (millisUntilFinished % (60 * 1000)) / 1000;
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                // Timer finished, do something if needed
                timerTextView.setText("00:00");
            }
        }.start();
    }

    // Method to reset the timer
    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            timerTextView.setText("00:00");
        }
    }

    // Method to stop the timer
    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class CustomAdapter extends BaseAdapter {

        private List<Exercise> exerciseList = new ArrayList<>();

        public void setExerciseList(List<Exercise> exerciseList) {
            this.exerciseList = exerciseList;
        }

        @Override
        public int getCount() {
            return exerciseList.size();
        }

        @Override
        public Object getItem(int i) {
            return exerciseList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.activity_listview_view, viewGroup, false);
            }
            TextView t1 = view.findViewById(R.id.textView1);
            TextView t2 = view.findViewById(R.id.textView2);
            TextView t3 = view.findViewById(R.id.textView3);
            TextView t4 = view.findViewById(R.id.textView4);

            Exercise exercise = exerciseList.get(i);

            // Set text to TextViews
            t1.setText(exercise.getName());
            t2.setText(String.valueOf(exercise.getSets()));
            t3.setText(String.valueOf(exercise.getReps()));
            t4.setText(String.valueOf(exercise.getDuration()));

            return view;
        }
    }
}

