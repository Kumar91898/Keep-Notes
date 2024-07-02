package com.kumar.keepnotes.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kumar.keepnotes.R;
import com.kumar.keepnotes.databinding.ActivityProfileUpdateBinding;

import java.util.HashMap;
import java.util.Map;

public class profileUpdate extends AppCompatActivity {
    ActivityProfileUpdateBinding binding;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchProfile();

        binding.backEdit.setOnClickListener(v -> onBackPressed());

        binding.changeImage.setOnClickListener(v -> selectImage());

        binding.editButton.setOnClickListener(v -> {
            binding.buttonAnimationEdit.setVisibility(View.VISIBLE);
            binding.buttonAnimationEdit.playAnimation();

            binding.buttonTextEdit.setVisibility(View.GONE);
            binding.editButton.setEnabled(false);

            uploadImageToStorage();
        });
    }

    private void fetchProfile() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String getName = document.getString("name");
                                String imageURL = document.getString("imageURL");

                                binding.nameFieldEdit.setText(getName);
                                loadImage(imageURL);
                            }
                        } else {
                            Toast.makeText(profileUpdate.this, "Failed to fetch profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            binding.profileEdit.setImageURI(imageUri);
        }
    }

    private void uploadImageToStorage() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            resetButton();
            return;
        }

        String fileName = binding.nameFieldEdit.getText().toString().trim();
        if (fileName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            resetButton();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profiles/" + fileName);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    resetButton();
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageURL = uri.toString();
                        addDataToFirestore(fileName, imageURL);

                        loadImage(imageURL);
                    });

                    Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed To Update!", Toast.LENGTH_SHORT).show();
                    resetButton();
                });
    }

    private void addDataToFirestore(String name, String imageURL) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("imageURL", imageURL);

        String documentId = "rcTNMRLqCkhNmsTHi4Fp";

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(documentId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(profileUpdate.this, "Data Updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profileUpdate.this, "Data failed to update!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.profile) // Placeholder image while loading
                .error(R.drawable.profile) // Error image if loading fails
                .into(binding.profileEdit);
    }

    private void resetButton() {
        binding.buttonAnimationEdit.pauseAnimation();
        binding.buttonAnimationEdit.setVisibility(View.GONE);

        binding.buttonTextEdit.setVisibility(View.VISIBLE);
        binding.editButton.setEnabled(true); // Re-enabling the edit button
    }
}
