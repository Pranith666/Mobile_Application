package com.example.myapplication;

public class Exercise {
    private String name;
    private int sets;
    private int reps;
    private int duration;
    public Exercise() {
        // Default constructor required for Firebase
    }
    public Exercise(String name, int sets, int reps, int duration) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.duration = duration;
    }
    public void setName() {
        this.name = name;
    }

    public void setSets() {
        this.sets = sets;
    }

    public void setReps() {
        this.reps = reps;
    }

    public void setDuration() {
        this.duration = duration;
    }

    // Getters for exercise details
    public String getName() {
        return name;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public int getDuration() {
        return duration;
    }
}

