package com.example.zaprojektni;

import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);

        // Firebase inicijalizacija
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI elementi
        EditText fullNameInput = findViewById(R.id.fullNameInput);
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        EditText confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        EditText dogNameInput = findViewById(R.id.dogNameInput);
        AutoCompleteTextView breedDropdown = findViewById(R.id.breedDropdown);
        Button registerButton = findViewById(R.id.registerButton);
        TextView signInLink = findViewById(R.id.SignInLink);

        // Dropdown meni za pasminu pasa
        String[] breeds = {"Akita", "Bulldog", "Husky", "Labrador", "Malinoa", "Pekinezer", "Tornjak",};
        ArrayAdapter<String> breedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, breeds);
        breedDropdown.setAdapter(breedAdapter);
        breedDropdown.setOnClickListener(v -> breedDropdown.showDropDown());

        // Link "Sign in"
        signInLink.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccount.this, LoginActivity.class);
            startActivity(intent);
        });

        // Registracija korisnika
        registerButton.setOnClickListener(v -> {
            String fullName = fullNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            String dogName = dogNameInput.getText().toString().trim();
            String breed = breedDropdown.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {
                Toast.makeText(CreateAccount.this, "Provjerite unos podataka!", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("REG", "Pokušaj registracije korisnika: " + email);
            Toast.makeText(CreateAccount.this, "Pokušaj registracije...", Toast.LENGTH_SHORT).show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("fullName", fullName);
                                userData.put("email", email);
                                userData.put("dogName", dogName);
                                userData.put("breed", breed);

                                // Upisivanje podataka u Firestore
                                db.collection("users").document(uid)
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> {

                                       // Poruka za uspješnu registraciju
                                            Toast.makeText(CreateAccount.this, "Uspješno ste se registrovali!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(CreateAccount.this, homePage.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {

                                            // Poruka o grešci
                                            Toast.makeText(CreateAccount.this, "Greška pri upisu u Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(CreateAccount.this, "Registracija neuspješna: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        // Layouta za edge to edge prikaz
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.createAccountLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
