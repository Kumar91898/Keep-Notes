package com.kumar.keepnotes.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kumar.keepnotes.R;
import com.kumar.keepnotes.activities.noteView;
import com.kumar.keepnotes.adapters.deleteAdapter;
import com.kumar.keepnotes.adapters.favAdapter;
import com.kumar.keepnotes.dataModels.data;
import com.kumar.keepnotes.databinding.FragmentTrashBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class trashFragment extends Fragment {

    FragmentTrashBinding binding;
    private deleteAdapter delete_Adapter;
    private List<data> dataList;
    private String color;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTrashBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.recyclerViewDelete.setLayoutManager(layoutManager);

        dataList = new ArrayList<>();
        delete_Adapter = new deleteAdapter(dataList, new deleteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(data dataClass, int position) {
                // Handle click event
            }

            @Override
            public void onItemLongClick(data dataClass, int position) {
                showOptionsDialog(dataClass);
            }
        });

        binding.recyclerViewDelete.setAdapter(delete_Adapter);

        fetchData();
        refreshFragment();
    }

    private void updateUI() {
        if (dataList.isEmpty()) {
            binding.emptyLayout.setVisibility(View.VISIBLE);
            binding.dataLayout.setVisibility(View.GONE);
        } else {
            binding.emptyLayout.setVisibility(View.GONE);
            binding.dataLayout.setVisibility(View.VISIBLE);
        }
    }

    private void refreshFragment() {
        binding.swipeToRefreshDelete.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
                Toast.makeText(getContext(), "Page Refreshed!", Toast.LENGTH_SHORT).show();
                binding.swipeToRefreshDelete.setRefreshing(false);
            }
        });
    }

    private void fetchData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("deleted")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        dataList.clear();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String subject = document.getString("subject");
                                String note = document.getString("note");
                                String date = document.getString("date");
                                String category = document.getString("category");
                                color = document.getString("color");
                                String documentId = document.getId();

                                dataList.add(new data(subject, note, date, documentId, category));
                            }
                            delete_Adapter.notifyDataSetChanged();

                            updateUI();
                        }
                    }
                });
    }

    private void showOptionsDialog(data DataClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Action")
                .setItems(new CharSequence[]{"Delete", "Restore"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Delete action
                                deleteNote(DataClass);
                                break;
                            case 1:
                                // Restore action
                                restoreNote(DataClass);
                                break;
                        }
                    }
                })
                .setCancelable(true)
                .show();
    }

    private void deleteNote(data DataClass){
        String documentId = DataClass.getDocumentId();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("deleted").document(documentId)
                .delete()
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Note deleted!", Toast.LENGTH_SHORT).show();

                    fetchData();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to remove note!", Toast.LENGTH_SHORT).show());
    }

    private void restoreNote(data dataClass) {
        String subject = dataClass.getSubject();
        String note = dataClass.getNote();
        String date = dataClass.getDate();
        String category = dataClass.getCategory();

        // Creating a new data object
        HashMap<String, Object> restoredData = new HashMap<>();
        restoredData.put("subject", subject);
        restoredData.put("note", note);
        restoredData.put("date", date);
        restoredData.put("category", category);
        restoredData.put("isFavorite", false);
        restoredData.put("color", color);

        // Adding the restored data to the "notes" collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notes").add(restoredData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Note restored!", Toast.LENGTH_SHORT).show();

                    // Optionally refreshing the UI after restoration
                    deleteNote(dataClass);
                    fetchData();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to restore note!", Toast.LENGTH_SHORT).show());
    }

}