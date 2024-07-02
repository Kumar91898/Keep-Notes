package com.kumar.keepnotes.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kumar.keepnotes.R;
import com.kumar.keepnotes.databinding.ActivityNoteViewBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class noteView extends AppCompatActivity {
    ActivityNoteViewBinding binding;
    private String subject, note, date, category, documentId, selectedColor;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        subject = getIntent().getStringExtra("subject");
        note = getIntent().getStringExtra("note");
        date = getIntent().getStringExtra("date");
        category = getIntent().getStringExtra("category");
        documentId = getIntent().getStringExtra("documentId");

        binding.subjectText.setText(subject);
        binding.noteText.setText(note);
        binding.dateText.setText(date);

        selectedColor = "#FFFFFF";

        //data for the spinner
        String[] items = {"Personal", "Home", "Love", "School", "Study"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.selected_spinner_item,
                items
        );

        adapter.setDropDownViewResource(R.layout.spinner_item);
        binding.categorySpinner.setAdapter(adapter);

        //position of the category
        int position = adapter.getPosition(category);

        // Setting the selection
        if (position >= 0) {
            binding.categorySpinner.setSelection(position);
        }

        // Fetching note details from Firestore if documentId is not null
        if (documentId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("notes").document(documentId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            isFavorite = Boolean.TRUE.equals(documentSnapshot.getBoolean("isFavorite"));
                            selectedColor = documentSnapshot.getString("color");
                            updateFavoriteButtonState(); // Updating button state without adding to favorites
                            updateNoteColor();
                        }
                    });
        }

        binding.colorButton.setOnClickListener(v -> showDialog());

        binding.backButton.setOnClickListener(v -> onBackPressed());

        binding.saveButton.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            binding.loading.playAnimation();

            binding.checkImage.setVisibility(View.GONE);
            binding.saveButton.setEnabled(false);

            addOrUpdateNote();
        });

        binding.favoriteButton.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            updateFavoriteButton(); // Updating button and handling adding/removing from favorites
            addOrUpdateNote();
        });

        binding.deleteButton.setOnClickListener(v -> {
            addToDelete();
            removeNote();
            removeFromFavorites();
            onBackPressed();
        });
    }

    private void showDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        View yellow = dialog.findViewById(R.id.yellow);
        View white = dialog.findViewById(R.id.white);
        View blue = dialog.findViewById(R.id.blue);
        View gold = dialog.findViewById(R.id.gold);
        View amethyst = dialog.findViewById(R.id.amethyst);
        View cyan = dialog.findViewById(R.id.cyan);
        View teal = dialog.findViewById(R.id.teal);
        View red = dialog.findViewById(R.id.red);
        View peach = dialog.findViewById(R.id.peach);
        View lemon = dialog.findViewById(R.id.lemon);
        View peacock = dialog.findViewById(R.id.peacock);
        View magenta = dialog.findViewById(R.id.magenta);

        yellow.setOnClickListener(v -> {
            selectedColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.light_yellow)));
            updateNoteColor();
        });

        white.setOnClickListener(v -> {
            selectedColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.white)));
            updateNoteColor();
        });

        blue.setOnClickListener(v -> {
            selectedColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.light_blue)));
            updateNoteColor();
        });

        gold.setOnClickListener(v -> {
            selectedColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.light_gold)));
            updateNoteColor();
        });

        amethyst.setOnClickListener(v -> {
            selectedColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.light_amethyst)));
            updateNoteColor();
        });

        cyan.setOnClickListener(v -> {
            selectedColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.light_cyan)));
            updateNoteColor();
        });

        teal.setOnClickListener(v -> {
            selectedColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.light_teal)));
            updateNoteColor();
        });

        red.setOnClickListener(v -> {
            selectedColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.light_red)));
            updateNoteColor();
        });

        peach.setOnClickListener(v -> {
            selectedColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.light_peach)));
            updateNoteColor();
        });

        lemon.setOnClickListener(v -> {
            selectedColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.light_lemon)));
            updateNoteColor();
        });

        peacock.setOnClickListener(v -> {
            selectedColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.light_peacock)));
            updateNoteColor();
        });

        magenta.setOnClickListener(v -> {
            selectedColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.light_magenta)));
            updateNoteColor();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void updateNoteColor() {
        int color = Color.parseColor(selectedColor);
        binding.subjectText.setTextColor(color);
        binding.noteText.setTextColor(color);
    }

    private void updateFavoriteButtonState() {
        if (isFavorite) {
            binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24);
        }
    }

    private void updateFavoriteButton() {
        updateFavoriteButtonState(); // Updating the button state

        if (isFavorite) {
            addToFavorites();
        } else {
            removeFromFavorites();
        }
    }

    private void addToFavorites() {
        String getSubject = binding.subjectText.getText().toString();
        String getNote = binding.noteText.getText().toString();
        String getDate = binding.dateText.getText().toString();
        String getCategory = binding.categorySpinner.getSelectedItem().toString();

        HashMap<String, Object> favorites = new HashMap<>();
        favorites.put("subject", getSubject);
        favorites.put("note", getNote);
        favorites.put("date", getDate);
        favorites.put("category", getCategory);
        favorites.put("documentId", documentId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        updateFavoritesCollection(documentId, favorites); // Updating the favorites collection
    }

    private void removeFromFavorites() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("favorites").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(noteView.this, "Removed from Favorites", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(noteView.this, "Failed to remove from Favorites", Toast.LENGTH_SHORT).show());
    }

    private void addOrUpdateNote() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String getSubject = binding.subjectText.getText().toString();
        String getNote = binding.noteText.getText().toString();
        String getDate = binding.dateText.getText().toString();
        String getCategory = binding.categorySpinner.getSelectedItem().toString();

        HashMap<String, Object> newNote = new HashMap<>();
        newNote.put("subject", getSubject);
        newNote.put("note", getNote);
        newNote.put("date", getDate);
        newNote.put("category", getCategory);
        newNote.put("isFavorite", isFavorite);
        newNote.put("color", selectedColor);

        if (documentId != null) {
            // Updating existing note
            updateData(documentId, newNote);
        } else {
            // Checking if the note already exists in the notes
            db.collection("notes")
                    .whereEqualTo("subject", getSubject)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    // Note not in notes, adding it
                                    db.collection("notes").add(newNote)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(noteView.this, "Note Saved!", Toast.LENGTH_SHORT).show();
                                                    resetButton();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(noteView.this, "Note failed to save!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // Note exists, updating it
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        updateData(document.getId(), newNote);
                                    }
                                }
                            } else {
                                Toast.makeText(noteView.this, "Failed to check notes!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void updateData(String documentId, Map<String, Object> updatedNote) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("notes").document(documentId)
                .set(updatedNote)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(noteView.this, "Note Updated!", Toast.LENGTH_SHORT).show();
                        resetButton();
                        if (isFavorite) {
                            updateFavoritesCollection(documentId, updatedNote);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(noteView.this, "Note failed to update!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateFavoritesCollection(String documentId, Map<String, Object> noteData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("favorites").document(documentId)
                .set(noteData)
                .addOnSuccessListener(aVoid -> Toast.makeText(noteView.this, "Favorites Updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(noteView.this, "Failed to update Favorites", Toast.LENGTH_SHORT).show());
    }

    private void removeNote() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notes").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(noteView.this, "Note removed!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(noteView.this, "Failed to remove note!", Toast.LENGTH_SHORT).show());
    }

    private void addToDelete() {
        String getSubject = binding.subjectText.getText().toString();
        String getNote = binding.noteText.getText().toString();
        String getDate = binding.dateText.getText().toString();
        String getCategory = binding.categorySpinner.getSelectedItem().toString();

        HashMap<String, Object> deleted = new HashMap<>();
        deleted.put("subject", getSubject);
        deleted.put("note", getNote);
        deleted.put("date", getDate);
        deleted.put("category", getCategory);
        deleted.put("isFalse", false);
        deleted.put("color", selectedColor);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("deleted")
                .add(deleted);
    }

    private void resetButton() {
        binding.loading.pauseAnimation();
        binding.loading.setVisibility(View.GONE);

        binding.checkImage.setVisibility(View.VISIBLE);
        binding.saveButton.setEnabled(true); // Re-enabling the edit button
    }
}
