package com.example.zaprojektni;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class carePage extends AppCompatActivity {

    private TextView txtSan, txtAktivnost, txtBriga;
    private Button btnBack;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_page);

        txtSan = findViewById(R.id.txtSan);
        txtAktivnost = findViewById(R.id.txtAktivnost);
        txtBriga = findViewById(R.id.txtBriga);
        btnBack = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();

        String breedName = null;
        if (getIntent() != null && getIntent().hasExtra("breedName")) {
            breedName = getIntent().getStringExtra("breedName");
        }

        if (breedName != null && !breedName.isEmpty()) {
            loadBreedData(breedName);
        } else {
            Toast.makeText(this, "Pasmina nije određena.", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadBreedData(String breedName) {
        db.collection("breeds").document(breedName.toLowerCase())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String san = documentSnapshot.getString("san");
                        String aktivnost = documentSnapshot.getString("aktivnost");
                        String briga = documentSnapshot.getString("briga");

                        txtSan.setText(san != null ? san : "Nema podataka o snu.");
                        txtAktivnost.setText(aktivnost != null ? aktivnost : "Nema podataka o aktivnosti.");
                        txtBriga.setText(briga != null ? briga : "Nema podataka o brizi.");
                    } else {
                        Toast.makeText(this, "Podaci o pasmini nisu pronađeni.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Greška pri učitavanju podataka o pasmini.", Toast.LENGTH_SHORT).show());
    }
}
