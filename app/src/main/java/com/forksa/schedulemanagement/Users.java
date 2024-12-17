package com.forksa.schedulemanagement;

public class Users {
    public String fullName, email;

    // Default constructor for Firebase
    public Users() {
    }

    public Users(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }
}
