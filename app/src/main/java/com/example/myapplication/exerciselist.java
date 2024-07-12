package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import android.widget.Filter;
import android.widget.Filterable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class exerciselist extends AppCompatActivity {
    DatabaseReference db;
    ListView listView;
    CustomAdapter customAdapter;
    EditText searchEditText; // EditText for search bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exerciselist);
        Toolbar toolbar = findViewById(R.id.actionToolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.listview);
        customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);

        db = FirebaseDatabase.getInstance().getReference().child("Exercisevid");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> exerciseNames = new ArrayList<>();
                List<DataSnapshot> exerciseSnapshots = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String exerciseName = snapshot.child("ExerciseName").getValue(String.class);
                    exerciseNames.add(exerciseName);
                    exerciseSnapshots.add(snapshot);
                }
                customAdapter.setExerciseNames(exerciseNames);
                customAdapter.setExerciseSnapshots(exerciseSnapshots);
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // Set item click listener for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataSnapshot exerciseSnapshot = customAdapter.getItem(position);
                String url = exerciseSnapshot.child("Link").getValue(String.class);

                if (url != null && !url.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            }
        });

        // Initialize and set TextChangedListener to the search EditText
        searchEditText = findViewById(R.id.search_bar);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                customAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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
    // CustomAdapter class remains the same


    public class CustomAdapter extends BaseAdapter implements Filterable {
        private List<String> exerciseNames = new ArrayList<>();
        private List<DataSnapshot> exerciseSnapshots = new ArrayList<>();
        private List<String> filteredExerciseNames = new ArrayList<>();
        private List<DataSnapshot> filteredExerciseSnapshots = new ArrayList<>();

        public void setExerciseNames(List<String> exerciseNames) {
            this.exerciseNames = exerciseNames;
            this.filteredExerciseNames = new ArrayList<>(exerciseNames);
        }

        public void setExerciseSnapshots(List<DataSnapshot> exerciseSnapshots) {
            this.exerciseSnapshots = exerciseSnapshots;
            this.filteredExerciseSnapshots = new ArrayList<>(exerciseSnapshots);
        }

        @Override
        public int getCount() {
            return filteredExerciseNames.size();
        }

        @Override
        public DataSnapshot getItem(int position) {
            return filteredExerciseSnapshots.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Your existing getView implementation
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_exerciselistview, parent, false);
            }
            TextView t1 = convertView.findViewById(R.id.LIST_MACHINE_NAME);
            TextView t2 = convertView.findViewById(R.id.LIST_MACHINE_SHORT_DESCRIPTION);

            String exerciseName = exerciseNames.get(position);
            // DataSnapshot exerciseSnapshot = exerciseSnapshots.get(position); // Not required here

            // Set text to TextViews
            t1.setText(exerciseName);
            t2.setText("Description");

            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<String> filteredList = new ArrayList<>();
                    List<DataSnapshot> filteredSnapshots = new ArrayList<>();

                    if (constraint == null || constraint.length() == 0) {
                        // No filter applied, return the original list
                        filteredList.addAll(exerciseNames);
                        filteredSnapshots.addAll(exerciseSnapshots);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();

                        // Filter the list based on the constraint
                        for (int i = 0; i < exerciseNames.size(); i++) {
                            if (exerciseNames.get(i).toLowerCase().contains(filterPattern)) {
                                filteredList.add(exerciseNames.get(i));
                                filteredSnapshots.add(exerciseSnapshots.get(i));
                            }
                        }
                    }

                    results.values = filteredList;
                    results.count = filteredList.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredExerciseNames.clear();
                    filteredExerciseSnapshots.clear();
                    filteredExerciseNames.addAll((List<String>) results.values);
                    for (String name : filteredExerciseNames) {
                        int index = exerciseNames.indexOf(name);
                        if (index != -1) {
                            filteredExerciseSnapshots.add(exerciseSnapshots.get(index));
                        }
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }
}