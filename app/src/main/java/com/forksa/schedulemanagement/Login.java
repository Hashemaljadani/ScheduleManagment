package com.forksa.schedulemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class Login extends AppCompatActivity {

    private EditText emailPhoneEditText, passwordEditText;
    private ImageView passwordToggle;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;
    private TextView forgotPasswordTextView, createAccountTextView;
    private boolean isPasswordVisible = false;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Make sure your XML file name matches this

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailPhoneEditText = findViewById(R.id.emailPhoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordToggle = findViewById(R.id.passwordToggle);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        createAccountTextView = findViewById(R.id.createAccountTextView);

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

        // Login button click event
        loginButton.setOnClickListener(v -> {
            String emailOrPhone = emailPhoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate inputs
            if (validateInputs(emailOrPhone, password)) {
                loginUser(emailOrPhone, password);
            }
        });

        // Create Account text click event
        createAccountTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, sign_up.class);
            startActivity(intent);
        });

        // Forgot Password text click event
        forgotPasswordTextView.setOnClickListener(v -> {
            // Handle forgot password logic here
            Toast.makeText(Login.this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show();
        });
    }

    // Method to validate user inputs
    private boolean validateInputs(String emailOrPhone, String password) {
        if (TextUtils.isEmpty(emailOrPhone)) {
            emailPhoneEditText.setError("Enter your email or phone.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Enter your password.");
            return false;
        }
        return true;
    }

    // Method to log in the user
    private void loginUser(String emailOrPhone, String password) {
        if (Patterns.EMAIL_ADDRESS.matcher(emailOrPhone).matches()) {
            // Login with email and password
            auth.signInWithEmailAndPassword(emailOrPhone, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Login success, navigate to Home or Main Activity
                                Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, Home.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Login failed
                                Toast.makeText(Login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            emailPhoneEditText.setError("Please enter a valid email.");
            emailPhoneEditText.requestFocus();
        }
    }
}
