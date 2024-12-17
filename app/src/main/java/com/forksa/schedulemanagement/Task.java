package com.forksa.schedulemanagement;

public class Task {
    private String id;
    private String name;
    private long reminderTime;

    public Task() {
        // Default constructor required for Firebase
    }

    public Task(String id, String name, long reminderTime) {
        this.id = id;
        this.name = name;
        this.reminderTime = reminderTime;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setName(String name) {
        this.name = name;
    }
}
