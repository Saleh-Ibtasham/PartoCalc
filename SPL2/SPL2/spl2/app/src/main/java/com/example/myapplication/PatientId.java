package com.example.myapplication;

import android.support.annotation.NonNull;

public class PatientId {

    public String PatientId;

    public <T extends PatientId> T withId(@NonNull final String id) {
        this.PatientId = id;
        return (T) this;
    }
}
