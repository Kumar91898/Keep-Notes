package com.kumar.keepnotes.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kumar.keepnotes.R;
import com.kumar.keepnotes.activities.noteView;
import com.kumar.keepnotes.activities.profileUpdate;
import com.kumar.keepnotes.adapters.homeAdapter;
import com.kumar.keepnotes.dataModels.data;
import com.kumar.keepnotes.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class homeFragment extends Fragment {
    FragmentHomeBinding binding;
    private TextView[] categoryTextViews;
    private homeAdapter home_Adapter;
    private List<data> dataList;

     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing the RecyclerView with a Grid Layout Manager
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.recyclerView.setLayoutManager(layoutManager);

        dataList = new ArrayList<>();
        home_Adapter = new homeAdapter(dataList, new homeAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(data data, int position) {
                Intent intent = new Intent(getContext(), noteView.class);
                intent.putExtra("subject", data.getSubject());
                intent.putExtra("note", data.getNote());
                intent.putExtra("date", data.getDate());
                intent.putExtra("category", data.getCategory());
                intent.putExtra("documentId", data.getDocumentId());
                startActivity(intent);
            }
        });

        binding.recyclerView.setAdapter(home_Adapter);

        fetchData();
        fetchProfile();

        binding.profilePicture.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), profileUpdate.class));
        });

        binding.add.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), noteView.class);
            intent.putExtra("", "Title");
            intent.putExtra("", "note");
            intent.putExtra("date", getTodayDate());
            startActivity(intent);
        });

        // Initializing the array with all category TextViews
        categoryTextViews = new TextView[] {
                binding.All, binding.home, binding.love,
                binding.school, binding.personal, binding.study
        };

        manageCategories();
        int selectedColor = ContextCompat.getColor(requireContext(), R.color.silver);
        binding.All.setTextColor(selectedColor);
        binding.All.setBackgroundResource(R.drawable.category_bar_shape_white);

        refreshFragment();

        SearchAction();
    }

    private void SearchAction() {
        //Searching by pressing the enter key in keyboard
        binding.searchField.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            boolean isEnterPressed = (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            boolean isSearchAction = actionId == EditorInfo.IME_ACTION_SEARCH;

            if (isSearchAction || isEnterPressed) {
                if (event == null || !event.isShiftPressed()) {
                    // The user pressed the enter key or search action button
                    String query = binding.searchField.getText().toString().trim();
                    if (!query.isEmpty()) {
                        // Assuming query is either a name or a category
                        fetchNotes(query);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private void fetchNotes(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // First, query by category
        db.collection("notes")
                .whereEqualTo("category", query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String subject = document.getString("subject");
                            String note = document.getString("note");
                            String date = document.getString("date");
                            String category = document.getString("category");
                            String documentId = document.getId();

                            dataList.add(new data(subject, note, date, documentId, category));
                        }
                        home_Adapter.notifyDataSetChanged();
                    } else {
                        // If no results by category, trying querying by name
                        fetchNotesBySubject(query);
                    }
                });
    }

    private void fetchNotesBySubject(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Second, query by name
        db.collection("notes")
                .whereEqualTo("subject", query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String subject = document.getString("subject");
                            String note = document.getString("note");
                            String date = document.getString("date");
                            String category = document.getString("category");
                            String documentId = document.getId();

                            dataList.add(new data(subject, note, date, documentId, category));
                        }
                        home_Adapter.notifyDataSetChanged();
                    } else {
                        // If no results by name either
                        Toast.makeText(getContext(), "No Data Found!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void refreshFragment() {
        binding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
                fetchProfile();
                Toast.makeText(getContext(), "Page Refreshed!", Toast.LENGTH_SHORT).show();
                binding.swipeToRefresh.setRefreshing(false);
            }
        });
    }

    private void fetchData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("notes")
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
                                String documentId = document.getId();

                                dataList.add(new data(subject, note, date, documentId, category));
                            }
                            home_Adapter.notifyDataSetChanged();
                        }
                    }
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

                                binding.name.setText("Welcome " + getName + "!");
                                loadImage(imageURL);
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void loadImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.profile) // Placeholder image while loading
                .error(R.drawable.profile) // Error image if loading fails
                .into(binding.profilePicture);
    }

    private void manageCategories() {
        for (TextView textView : categoryTextViews) {
            textView.setOnClickListener(v -> {
                binding.searchField.setText("");

                // Reseting background and text color for all TextViews
                for (TextView tv : categoryTextViews) {
                    tv.setBackgroundResource(R.drawable.category_bar_shape);
                    int color = ContextCompat.getColor(requireContext(), R.color.white);
                    tv.setTextColor(color);
                }

                // Setting background and text color for the clicked TextView
                textView.setBackgroundResource(R.drawable.category_bar_shape_white);
                int selectedColor = ContextCompat.getColor(requireContext(), R.color.silver);
                textView.setTextColor(selectedColor);

                int idAll = binding.All.getId();
                String categoryText = textView.getText().toString();

                fetchDataCategory(categoryText);

                if (textView.getId() == idAll){
                    fetchData();
                }
            });
        }
    }

    private void fetchDataCategory(String category){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("notes")
                .whereEqualTo("category", category)
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
                                String documentId = document.getId();

                                dataList.add(new data(subject, note, date, documentId, category));
                            }
                            home_Adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public static String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(calendar.getTime());
    }
}