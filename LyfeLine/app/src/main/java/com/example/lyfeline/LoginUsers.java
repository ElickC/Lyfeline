package com.example.lyfeline;

public class LoginUsers {
    private boolean isVictim;
    private String user_id;

    public LoginUsers() {
    }

    public LoginUsers(boolean isVictim, String user_id) {
        this.isVictim = isVictim;
        this.user_id = user_id;
    }

    public boolean isVictim() {
        return isVictim;
    }

    public void setVictim(boolean victim) {
        isVictim = victim;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
