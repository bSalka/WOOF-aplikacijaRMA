package com.example.zaprojektni;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPasswordInput, newPasswordInput, confirmNewPasswordInput;
    private Button changePasswordBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmNewPasswordInput = findViewById(R.id.confirmNewPasswordInput);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);

        mAuth = FirebaseAuth.getInstance();

        changePasswordBtn.setOnClickListener(v -> {
            String currentPassword = currentPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmNewPasswordInput.getText().toString().trim();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Popuni sva polja", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(this, "Nova šifra mora imati najmanje 6 karaktera", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Nova šifra i potvrda se ne poklapaju", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null && user.getEmail() != null) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

                // Re-autentifikacija
                user.reauthenticate(credential)
                        .addOnSuccessListener(unused -> {
                            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Šifra uspješno promijenjena", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(this, "Greška pri promjeni šifre", Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Pogrešna trenutna šifra", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
