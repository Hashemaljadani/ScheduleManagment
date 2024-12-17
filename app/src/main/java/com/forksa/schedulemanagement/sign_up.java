package com.forksa.schedulemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sign_up extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText;
    private ImageView passwordToggle;
    private Button signUpButton;
    private TextView loginTextView;
    private boolean isPasswordVisible = false;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI elements
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordToggle = findViewById(R.id.passwordToggle);
        signUpButton = findViewById(R.id.signUpButton);
        loginTextView = findViewById(R.id.loginTextView);

        // Password toggle visibility
        passwordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordToggle.setImageResource(R.drawable.baseline_visibility_off_24);
            } else {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordToggle.setImageResource(R.drawable.baseline_visibility_24);
            }
            isPasswordVisible = !isPasswordVisible;
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Sign Up button click event
        signUpButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate inputs
            if (validateInputs(name, email, phone, password)) {
                registerUser(name, email, phone, password);
            }
        });

        // Log In text click event
        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(sign_up.this, Login.class);
            startActivity(intent);
        });
    }

    // Method to validate user inputs
    private boolean validateInputs(String name, String email, String phone, String password) {
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required.");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required.");
            return false;
        }
        if (TextUtils.isEmpty(phone) || phone.length() != 10 || !TextUtils.isDigitsOnly(phone)) {
            phoneEditText.setError("Enter a valid 10-digit phone number.");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters.");
            return false;
        }
        return true;
    }

    // Method to register a new user
    private void registerUser(String name, String email, String phone, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveUserToDatabase(name, email, phone);
                            Toast.makeText(sign_up.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(sign_up.this, Home.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(sign_up.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Method to save user data to Firebase Realtime Database
    private void saveUserToDatabase(String name, String email, String phone) {
        String sanitizedEmail = sanitizeEmail(email); // Sanitize email to use as a key

        // Create a Users object
        Users user = new Users(name, email);

        // Save user data under "Users > sanitized_email > user_info"
        databaseReference.child(sanitizedEmail).child("user_info").setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(sign_up.this, "User data saved successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(sign_up.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Helper method to sanitize email
    private String sanitizeEmail(String email) {
        return email.replace(".", "_").replace("@", "_");
    }
}
