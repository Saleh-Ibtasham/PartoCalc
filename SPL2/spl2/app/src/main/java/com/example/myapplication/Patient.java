package com.example.myapplication;

public class Patient extends PatientId {

    private String name;
    private String gravida;
    private String para;
    private String hospitalNumber;
    private String hours;
    private String membrane;
    private String admissionDate;
    private String admissionTime;
    private String id;

    public Patient(){}

    public Patient(String name, String gravida, String para, String hospitalNumber, String hours, String membrane, String admissionDate, String admissionTime, String id) {
        this.name = name;
        this.gravida = gravida;
        this.para = para;
        this.hospitalNumber = hospitalNumber;
        this.hours = hours;
        this.membrane = membrane;
        this.admissionDate = admissionDate;
        this.admissionTime = admissionTime;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGravida() {
        return gravida;
    }

    public void setGravida(String gravida) {
        this.gravida = gravida;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getHospitalNumber() {
        return hospitalNumber;
    }

    public void setHospitalNumber(String hospitalNumber) {
        this.hospitalNumber = hospitalNumber;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMembrane() {
        return membrane;
    }

    public void setMembrane(String membrane) {
        this.membrane = membrane;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getAdmissionTime() {
        return admissionTime;
    }

    public void setAdmissionTime(String admissionTime) {
        this.admissionTime = admissionTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
