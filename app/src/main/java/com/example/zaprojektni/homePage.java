package com.example.zaprojektni;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class homePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView welcomeTextView, userNameTextView, dogBreedTextView, dogNameTextView, funFactTextView;
    private ImageView dogImageView, profileImageView;
    private String breed;  // Čuvamo pasminu za slanje u carePage i foodPage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Inicijalizacija UI elemenata
        profileImageView = findViewById(R.id.profileImageView);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        userNameTextView = findViewById(R.id.userNameTextView);
        dogBreedTextView = findViewById(R.id.dogBreedTextView);
        dogNameTextView = findViewById(R.id.dogNameTextView);
        dogImageView = findViewById(R.id.dogImageView);
        funFactTextView = findViewById(R.id.funFactTextView);

        Button buttonCare = findViewById(R.id.buttonCare);
        Button buttonFood = findViewById(R.id.buttonFood);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Profilna slika
        Glide.with(this)
                .load(R.drawable.profile)
                .circleCrop()
                .into(profileImageView);

        //Provjera korisnika u bazi i prikupljanje podataka iz baze
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            String dogName = documentSnapshot.getString("dogName");
                            breed = documentSnapshot.getString("breed");

                            userNameTextView.setText(fullName != null ? fullName : "Korisnik");
                            dogNameTextView.setText("Ime psa: " + (dogName != null ? dogName : "nije unijeto"));
                            dogBreedTextView.setText("Pasmina: " + (breed != null ? breed : "nije unijeta"));

                            if (breed != null && !breed.isEmpty()) {
                                db.collection("breeds").document(breed.toLowerCase()).get()
                                        .addOnSuccessListener(breedSnapshot -> {
                                            if (breedSnapshot.exists()) {
                                                String imageUrl = breedSnapshot.getString("imageURL");
                                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                                    Glide.with(homePage.this)
                                                            .load(imageUrl)
                                                            .centerCrop()
                                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                            .error(R.drawable.profile)
                                                            .into(dogImageView);
                                                } else {
                                                    Toast.makeText(homePage.this, "URL slike nije pronađen.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(homePage.this, "Greška pri dohvaćanju slike pasmine", Toast.LENGTH_SHORT).show());
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(homePage.this, "Greška pri dohvaćanju korisničkih podataka", Toast.LENGTH_SHORT).show());
        }

        // Fun Facts
        Random random = new Random();
        int randomFactIndex = random.nextInt(10) + 1;
        String factKey = "f" + randomFactIndex;

        db.collection("funFact").document("funfacts")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String funFact = documentSnapshot.getString(factKey);
                        funFactTextView.setText(funFact != null ? funFact : "Zanimljivost nije pronađena.");
                    } else {
                        funFactTextView.setText("Dokument 'funfacts' ne postoji.");
                    }
                })
                .addOnFailureListener(e -> funFactTextView.setText("Greška pri učitavanju zanimljivosti."));

        // Dugme za brigu šalje pasminu u carePage
        buttonCare.setOnClickListener(v -> {
            if (breed != null && !breed.isEmpty()) {
                Intent intent = new Intent(homePage.this, carePage.class);
                intent.putExtra("breedName", breed);
                startActivity(intent);
            } else {
                Toast.makeText(homePage.this, "Pasmina nije određena.", Toast.LENGTH_SHORT).show();
            }
        });

        // Dugme za hranu šalje pasminu u foodPage
        buttonFood.setOnClickListener(v -> {
            if (breed != null && !breed.isEmpty()) {
                Intent intent = new Intent(homePage.this, foodPage.class);
                intent.putExtra("breedName", breed);
                startActivity(intent);
            } else {
                Toast.makeText(homePage.this, "Pasmina nije određena.", Toast.LENGTH_SHORT).show();
            }
        });

        // Klik na profilnu sliku da se prikaže meni
        profileImageView.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(homePage.this, view);
            popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                //Log out korisnka
                if (itemId == R.id.action_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(homePage.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;

                //Aktivacija Dark mode
                } else if (itemId == R.id.action_dark_mode) {
                    int nightMode = AppCompatDelegate.getDefaultNightMode();
                    if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        Toast.makeText(homePage.this, "Light mode uključen", Toast.LENGTH_SHORT).show();
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        Toast.makeText(homePage.this, "Dark mode uključen", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                //Pozivanje klase za promjenu šifre
                } else if (itemId == R.id.action_change_password) {
                    Intent intent = new Intent(homePage.this, ChangePasswordActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false;
            });

            popup.show();
        });
    }
}
