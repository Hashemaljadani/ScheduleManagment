package com.forksa.schedulemanagement;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;
    private Button btnAddTask;
    private DatabaseReference databaseReference;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userEmail = currentUser.getEmail(); // Get logged-in user's email
            String userKey = sanitizeEmail(userEmail);
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userKey)
                    .child("Tasks");

            recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
            btnAddTask = findViewById(R.id.btnAddTask);

            taskList = new ArrayList<>();
            taskAdapter = new TaskAdapter(taskList, this, userEmail);

            recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewTasks.setAdapter(taskAdapter);

            loadTasksFromFirebase();

            btnAddTask.setOnClickListener(v -> showAddTaskDialog());
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish(); // End activity if no user is logged in
        }
    }

    private String sanitizeEmail(String email) {
        return email.replace(".", "_").replace("@", "_");
    }

    private void showAddTaskDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        final EditText inputTaskName = dialogView.findViewById(R.id.inputTaskName);
        final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        final TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");
        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskName = inputTaskName.getText().toString().trim();
            if (!taskName.isEmpty()) {
                long reminderTimeInMillis = getReminderTimeInMillis(datePicker, timePicker);
                saveTaskToFirebase(taskName, reminderTimeInMillis);
            } else {
                Toast.makeText(Home.this, "Task name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private long getReminderTimeInMillis(DatePicker datePicker, TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                timePicker.getHour(), timePicker.getMinute(), 0);
        return calendar.getTimeInMillis();
    }

    private void saveTaskToFirebase(String taskName, long reminderTimeInMillis) {
        String taskId = databaseReference.push().getKey();

        if (taskId != null) {
            Task task = new Task(taskId, taskName, reminderTimeInMillis);

            databaseReference.child(taskId).setValue(task)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Home.this, "Task saved to Firebase", Toast.LENGTH_SHORT).show();
                        if (reminderTimeInMillis > 0) {
                            setTaskReminder(task, reminderTimeInMillis);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Home.this, "Failed to save task to Firebase", Toast.LENGTH_SHORT).show();
                        Log.e("Home", "Error saving task to Firebase", e);
                    });
        } else {
            Toast.makeText(Home.this, "Failed to generate task ID", Toast.LENGTH_SHORT).show();
        }
    }


    private void setTaskReminder(Task task, long reminderTimeInMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("task_name", task.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                task.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Check for API level and permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTimeInMillis, pendingIntent);
            } else {
                Toast.makeText(this, "Permission required to set exact alarms", Toast.LENGTH_LONG).show();
            }
        } else {
            // For devices below Android 12
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTimeInMillis, pendingIntent);
        }
    }

    private void loadTasksFromFirebase() {
        // Clear list before loading tasks
        taskList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Task task = snapshot.getValue(Task.class);
                if (task != null && !taskAlreadyExists(task)) {
                    taskList.add(task);
                    taskAdapter.notifyItemInserted(taskList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                Task updatedTask = snapshot.getValue(Task.class);
                if (updatedTask != null) {
                    for (int i = 0; i < taskList.size(); i++) {
                        if (taskList.get(i).getId().equals(updatedTask.getId())) {
                            taskList.set(i, updatedTask);
                            taskAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Task removedTask = snapshot.getValue(Task.class);
                if (removedTask != null) {
                    for (int i = 0; i < taskList.size(); i++) {
                        if (taskList.get(i).getId().equals(removedTask.getId())) {
                            taskList.remove(i);
                            taskAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }
            }
            private boolean taskAlreadyExists(Task task) {
                for (Task t : taskList) {
                    if (t.getId().equals(task.getId())) {
                        return true;
                    }
                }
                return false;
            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Not needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


