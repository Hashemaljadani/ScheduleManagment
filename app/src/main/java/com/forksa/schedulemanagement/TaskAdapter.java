package com.forksa.schedulemanagement;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> taskList;
    private Context context;
    private DatabaseReference databaseReference;

    public TaskAdapter(ArrayList<Task> taskList, Context context, String userEmail) {
        this.taskList = taskList;
        this.context = context;
        String sanitizedEmail = sanitizeEmail(userEmail);
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(sanitizedEmail)
                .child("Tasks");
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskName.setText(task.getName());

        if (task.getReminderTime() > 0) {
            Date date = new Date(task.getReminderTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.taskDateTime.setText(dateFormat.format(date));
        } else {
            holder.taskDateTime.setText("No Reminder Set");
        }

        holder.btnEdit.setOnClickListener(v -> showEditDialog(task, position));

        holder.btnDelete.setOnClickListener(v -> deleteTaskFromFirebase(task.getId(), position));
    }

    private void showEditDialog(Task task, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Task");

        final EditText input = new EditText(context);
        input.setText(task.getName());
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedName = input.getText().toString().trim();
            if (!updatedName.isEmpty()) {
                task.setName(updatedName);
                updateTaskInFirebase(task, position);
            } else {
                Toast.makeText(context, "Task name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateTaskInFirebase(Task task, int position) {
        databaseReference.child(task.getId()).setValue(task)
                .addOnSuccessListener(aVoid -> {
                    taskList.set(position, task);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update task", Toast.LENGTH_SHORT).show());
    }

    private void deleteTaskFromFirebase(String taskId, int position) {
        databaseReference.child(taskId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    taskList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete task", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    private String sanitizeEmail(String email) {
        return email.replace(".", "_").replace("@", "_");
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskDateTime;
        Button btnEdit, btnDelete;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDateTime = itemView.findViewById(R.id.taskDateTime);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
