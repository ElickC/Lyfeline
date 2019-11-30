package com.example.lyfeline;

public class HelpVics {
    VicLocation vicLocation;
    boolean buttonPressed;
    int priority;
    String message;
    boolean emtOnTheWay;
    boolean emtHasArrived;
    String emtAssigned;

    public HelpVics() {
        this.emtHasArrived = false;
        this.emtOnTheWay = false;
        this.buttonPressed = true;
    }

    public HelpVics(VicLocation vicLocation, int priority, String message, boolean emtOnTheWay, boolean emtHasArrived) {
        this.vicLocation = vicLocation;
        this.priority = priority;
        this.message = message;
        this.emtOnTheWay = emtOnTheWay;
        this.emtHasArrived = emtHasArrived;
    }

    public String getEmtAssigned() { return emtAssigned; }

    public void setEmtAssigned(String emtAssigned) { this.emtAssigned = emtAssigned; }

    public boolean isButtonPressed() {
        return buttonPressed;
    }

    public void setButtonPressed(boolean buttonPressed) {
        this.buttonPressed = buttonPressed;
    }

    public VicLocation getVicLocation() {
        return vicLocation;
    }

    public void setVicLocation(VicLocation vicLocation) {
        this.vicLocation = vicLocation;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEmtOnTheWay() {
        return emtOnTheWay;
    }

    public void setEmtOnTheWay(boolean emtOnTheWay) {
        this.emtOnTheWay = emtOnTheWay;
    }

    public boolean isEmtHasArrived() {
        return emtHasArrived;
    }

    public void setEmtHasArrived(boolean emtHasArrived) {
        this.emtHasArrived = emtHasArrived;
    }
}
