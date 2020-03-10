package com.example.myapplication;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

public class Patient extends PatientId {

    private String name;
    private String gravida;
    private String para;
    private String hospitalNumber;
    private String hours;
    private String membrane;
    private String admissionDate;
    private String admissionTime;
    private ArrayList<Entry> fetal = new ArrayList<>();
    private ArrayList<Entry> cervical = new ArrayList<>();
    private ArrayList<Entry> descend = new ArrayList<>();
    private ArrayList<PressureEntry> pressure = new ArrayList<>();
    private ArrayList<Entry> pulse = new ArrayList<>();
    private List<BarEntry> contraction = new ArrayList<>();
    private ArrayList<Integer> contractionRegions = new ArrayList<>();
    private ArrayList <TableEntry> fluid = new ArrayList<>();
    private ArrayList <TableEntry> moulding = new ArrayList<>();
    private ArrayList <TableEntry> timeInputs = new ArrayList<>();
    private ArrayList<TableEntry> drugs   = new ArrayList<TableEntry>();
    private ArrayList <TableEntry>  temperature = new ArrayList<>( );
    private ArrayList<TableEntry> protein = new ArrayList();
    private ArrayList<TableEntry> acetone = new ArrayList<>();
    private ArrayList<TableEntry> amount = new ArrayList<>();

    private String bedNumber = "0";
    private String status = "inactive";

    private String id;

    public Patient(){}

    public ArrayList<Entry> getFetal() {
        return fetal;
    }

    public void setFetal(ArrayList<Entry> fetal) {
        this.fetal = fetal;
    }

    public ArrayList<Entry> getCervical() {
        return cervical;
    }

    public void setCervical(ArrayList<Entry> cervical) {
        this.cervical = cervical;
    }

    public ArrayList<Entry> getDescend() {
        return descend;
    }

    public void setDescend(ArrayList<Entry> descend) {
        this.descend = descend;
    }

    public ArrayList<PressureEntry> getPressure() {
        return pressure;
    }

    public void setPressure(ArrayList<PressureEntry> pressure) {
        this.pressure = pressure;
    }

    public ArrayList<Entry> getPulse() {
        return pulse;
    }

    public void setPulse(ArrayList<Entry> pulse) {
        this.pulse = pulse;
    }

    public List<BarEntry> getContraction() {
        return contraction;
    }

    public void setContraction(List<BarEntry> contraction) {
        this.contraction = contraction;
    }


    public ArrayList<Integer> getContractionRegions() {
        return contractionRegions;
    }

    public void setContractionRegions(ArrayList<Integer> contractionRegions) {
        this.contractionRegions = contractionRegions;
    }

    public ArrayList<TableEntry> getFluid() {
        return fluid;
    }

    public void setFluid(ArrayList<TableEntry> fluid) {
        this.fluid = fluid;
    }

    public ArrayList<TableEntry> getMoulding() {
        return moulding;
    }

    public void setMoulding(ArrayList<TableEntry> moulding) {
        this.moulding = moulding;
    }

    public ArrayList<TableEntry> getDrugs() {
        return drugs;
    }

    public void setDrugs(ArrayList<TableEntry> drugs) {
        this.drugs = drugs;
    }

    public ArrayList<TableEntry> getTemperature() {
        return temperature;
    }

    public void setTemperature(ArrayList<TableEntry> temperature) {
        this.temperature = temperature;
    }

    public ArrayList<TableEntry> getProtein() {
        return protein;
    }

    public void setProtein(ArrayList<TableEntry> protein) {
        this.protein = protein;
    }

    public ArrayList<TableEntry> getAcetone() {
        return acetone;
    }

    public void setAcetone(ArrayList<TableEntry> acetone) {
        this.acetone = acetone;
    }

    public ArrayList<TableEntry> getAmount() {
        return amount;
    }

    public void setAmount(ArrayList<TableEntry> amount) {
        this.amount = amount;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<TableEntry> getTimeInputs() {
        return timeInputs;
    }

    public void setTimeInputs(ArrayList<TableEntry> timeInputs) {
        this.timeInputs = timeInputs;
    }

    public Patient(String name, String gravida, String para, String hospitalNumber, String hours, String membrane, String admissionDate, String admissionTime,
                   String bedNumber, String status) {
        this.name = name;
        this.gravida = gravida;
        this.para = para;
        this.hospitalNumber = hospitalNumber;
        this.hours = hours;
        this.membrane = membrane;
        this.admissionDate = admissionDate;
        this.admissionTime = admissionTime;
        this.bedNumber = bedNumber;
        this.status = status;
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
