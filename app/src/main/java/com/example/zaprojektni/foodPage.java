package com.example.zaprojektni;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class foodPage extends AppCompatActivity {

    private TextView txtPreporucenaHrana, txtZabranjenaHrana, txtObrok;
    private Button btnBack;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_page);

        txtPreporucenaHrana = findViewById(R.id.txtPreporucenaHrana);
        txtZabranjenaHrana = findViewById(R.id.txtZabranjenaHrana);
        txtObrok = findViewById(R.id.txtObrok);
        btnBack = findViewById(R.id.btnBackFood);

        db = FirebaseFirestore.getInstance();

        String breedName = null;
        if (getIntent() != null && getIntent().hasExtra("breedName")) {
            breedName = getIntent().getStringExtra("breedName");
        }

        if (breedName != null && !breedName.isEmpty()) {
            loadBreedFoodData(breedName);
        } else {
            Toast.makeText(this, "Pasmina nije određena.", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadBreedFoodData(String breedName) {
        db.collection("breeds").document(breedName.toLowerCase())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String preporucenaHrana = documentSnapshot.getString("preporucena_hrana");
                        String zabranjenaHrana = documentSnapshot.getString("zabranjena_hrana");
                        String obrok = documentSnapshot.getString("obrok");

                        txtPreporucenaHrana.setText(preporucenaHrana != null ? preporucenaHrana : "Nema podataka o preporučenoj hrani.");
                        txtZabranjenaHrana.setText(zabranjenaHrana != null ? zabranjenaHrana : "Nema podataka o zabranjenoj hrani.");
                        txtObrok.setText(obrok != null ? obrok : "Nema podataka o planu obroka.");
                    } else {
                        Toast.makeText(this, "Podaci o pasmini nisu pronađeni.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Greška pri učitavanju podataka o pasmini.", Toast.LENGTH_SHORT).show());
    }
}
