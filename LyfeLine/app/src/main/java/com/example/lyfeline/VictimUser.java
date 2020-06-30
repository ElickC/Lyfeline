package com.example.lyfeline;

public class VictimUser extends User {

    String address;
    String city;
    String state;

    private int height, weight, age;
    private boolean heartCondition, elderly, asthma, neurologicalCondition,
    highBloodPreasure, diabetes, physicallyDisabled, visuallyImpaired;

    private enum medicalCondition {
        NONE, MILD, MODERATE, CRITICAL;
    }

    medicalCondition condition;

    public VictimUser() {
    }

    public VictimUser(String user_id, String email, String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.user_id = user_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getUser_id(){
        return user_id;
    }

    public void setUser_id(String user_id){
        this.user_id = user_id;
    }

    public void setState(String state) { this.state = state; }

    public boolean getHeartCondition() {
        return this.heartCondition;
    }

    public void setHeartCondition(boolean hc) {
        this.heartCondition = hc;
    }

    public boolean getElderly() {
        return this.elderly;
    }

    public void setElderly(boolean elderly) {
        this.elderly = elderly;
    }

    public boolean getAsthma() {
        return this.asthma;
    }

    public void setAsthma(boolean asthma) {
        this.asthma = asthma;
    }

    public boolean getNeurologicalCondition() {
        return this.neurologicalCondition;
    }

    public void setNeurologicalCondition(boolean nc) {
        this.neurologicalCondition = nc;
    }

    public boolean getHighBloodPreasure() {
        return this.highBloodPreasure;
    }

    public void setHighBloodPreasure(boolean hbp) {
        this.highBloodPreasure = hbp;
    }

    public boolean getDiabetes() {
        return this.diabetes;
    }

    public void setDiabetes(boolean diabetes) {
        this.diabetes = diabetes;
    }

    public boolean getPhysicallyDisabled() {
        return this.physicallyDisabled;
    }

    public void setPhysicallyDisabled(boolean pd) {
        this.physicallyDisabled = pd;
    }

    public boolean getVisuallyImpaired() {
        return this.visuallyImpaired;
    }

    public void setVisuallyImpaired(boolean vi) {
        this.visuallyImpaired = vi;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int ht) {
        this.height = height;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int wt) {
        this.weight = wt;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public medicalCondition getMedicalCondition() {
        return this.condition;
    }

    public void setMedicalCondition() {

        if (this.diabetes || this.heartCondition || this.physicallyDisabled)
            this.condition = condition.CRITICAL;
        else if (this.visuallyImpaired || this.highBloodPreasure || this.neurologicalCondition)
            this.condition = condition.MODERATE;
        else if (this.asthma || this.elderly)
            this.condition = condition.MILD;
        else
            this.condition = condition.NONE;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }

}
