package com.kumar.keepnotes.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kumar.keepnotes.R;
import com.kumar.keepnotes.databinding.ActivityMainBinding;
import com.kumar.keepnotes.fragments.favFragment;
import com.kumar.keepnotes.fragments.homeFragment;
import com.kumar.keepnotes.fragments.infoFragment;
import com.kumar.keepnotes.fragments.trashFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragments(new homeFragment());

        binding.home.setOnClickListener(v -> {
            binding.home.setBackgroundResource(R.drawable.nav_background);
            binding.fav.setBackgroundResource(0);
            binding.trash.setBackgroundResource(0);
            binding.info.setBackgroundResource(0);
            replaceFragments(new homeFragment());
        });

        binding.fav.setOnClickListener(v -> {
            binding.home.setBackgroundResource(0);
            binding.fav.setBackgroundResource(R.drawable.nav_background);
            binding.trash.setBackgroundResource(0);
            binding.info.setBackgroundResource(0);
            replaceFragments(new favFragment());
        });

        binding.trash.setOnClickListener(v -> {
            binding.home.setBackgroundResource(0);
            binding.fav.setBackgroundResource(0);
            binding.info.setBackgroundResource(0);
            binding.trash.setBackgroundResource(R.drawable.nav_background);
            replaceFragments(new trashFragment());
        });

        binding.info.setOnClickListener(v -> {
            binding.home.setBackgroundResource(0);
            binding.fav.setBackgroundResource(0);
            binding.info.setBackgroundResource(R.drawable.nav_background);
            binding.trash.setBackgroundResource(0);
            replaceFragments(new infoFragment());
        });
    }

    public void replaceFragments(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}