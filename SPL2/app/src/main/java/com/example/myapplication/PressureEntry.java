package com.example.myapplication;

public class PressureEntry {
    public double xInput = 0;
    public int sysTol = 65;
    public int dysTol = 120;

    public PressureEntry(double xInput, int sysTol, int dysTol) {
        this.xInput = xInput;
        this.sysTol = sysTol;
        this.dysTol = dysTol;
    }
}
