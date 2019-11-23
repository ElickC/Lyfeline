package com.example.lyfeline;

import android.app.Application;

public class UserClient extends Application {

    private User User = null;

    public User getUser() { return User;}

    public void setUser(User User) {this.User = User;}
}
