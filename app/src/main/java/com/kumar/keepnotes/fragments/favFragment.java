package com.kumar.keepnotes.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kumar.keepnotes.R;
import com.kumar.keepnotes.activities.noteView;
import com.kumar.keepnotes.adapters.favAdapter;
import com.kumar.keepnotes.adapters.homeAdapter;
import com.kumar.keepnotes.dataModels.data;
import com.kumar.keepnotes.databinding.FragmentFavBinding;

import java.util.ArrayList;
import java.util.List;

public class favFragment extends Fragment {

    FragmentFavBinding binding;
    private favAdapter fav_Adapter;
    private List<data> dataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the RecyclerView with a StaggeredGridLayoutManager
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.recyclerViewFav.setLayoutManager(layoutManager);

        dataList = new ArrayList<>();
        fav_Adapter = new favAdapter(dataList, new favAdapter.OnItemClickListener(){
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

        binding.recyclerViewFav.setAdapter(fav_Adapter);

        fetchData();
        refreshFragment();
    }

    private void updateUI() {
        if (dataList.isEmpty()) {
            binding.emptyLayoutFav.setVisibility(View.VISIBLE);
            binding.dataLayoutFav.setVisibility(View.GONE);
        } else {
            binding.emptyLayoutFav.setVisibility(View.GONE);
            binding.dataLayoutFav.setVisibility(View.VISIBLE);
        }
    }

    private void refreshFragment() {
        binding.swipeToRefreshFav.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
                Toast.makeText(getContext(), "Page Refreshed!", Toast.LENGTH_SHORT).show();
                binding.swipeToRefreshFav.setRefreshing(false);
            }
        });
    }

    private void fetchData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("favorites")
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
                            fav_Adapter.notifyDataSetChanged();
                            updateUI();
                        }
                    }
                });
    }
}