package com.example.zaprojektni;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView createAccountLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicijalizacija FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Provjera da li je korisnik već prijavljen za IPI APP CHALLENGE
        /*
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Ako je korisnik već prijavljen, odmah idi na homePage
            startActivity(new Intent(LoginActivity.this, homePage.class));
            finish();
        }
        */


        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        createAccountLink = findViewById(R.id.createAccountLink);

        loginButton.setOnClickListener(v -> loginUser());

        // Link "Kreiraj novi račun"
        createAccountLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccount.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Unesite email i lozinku.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prijava putem Firebase Authentication
        loginButton.setEnabled(false);  // Onemogućavanje klika na dugme dok traje prijava
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Intent intent = new Intent(LoginActivity.this, homePage.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // Poruka o grešci
                        Toast.makeText(LoginActivity.this, "Greška: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
