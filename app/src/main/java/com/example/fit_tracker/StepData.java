package com.example.fit_tracker;
public class StepData {
    private int steps;
    private int goal;

    public StepData() {} // Firestore needs no-arg constructor

    public StepData(int steps, int goal) {
        this.steps = steps;
        this.goal = goal;
    }

    public int getSteps() { return steps; }
    public int getGoal() { return goal; }
}
