package com.kumar.keepnotes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kumar.keepnotes.R;
import com.kumar.keepnotes.dataModels.data;

import java.util.List;
import java.util.Random;

public class favAdapter extends RecyclerView.Adapter<favAdapter.ViewHolder> {
    private List<data> dataList;
    private static OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(data dataClass, int position);
    }

    private static int[] colors = {
            R.color.light_green, R.color.light_blue, R.color.light_lavender, R.color.light_lemon,
            R.color.light_orange, R.color.light_peach, R.color.light_purple, R.color.light_red,
            R.color.light_yellow, R.color.light_teal, R.color.light_cyan, R.color.light_mint, R.color.light_silver,
            R.color.light_indigo, R.color.light_lime, R.color.light_amethyst, R.color.light_salmon,
            R.color.light_peacock, R.color.light_magenta, R.color.light_gold
    };
    private static Random random = new Random();

    public favAdapter(List<data> dataList, OnItemClickListener itemClickListener) {
        this.dataList = dataList;
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public favAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_notes_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull favAdapter.ViewHolder holder, int position) {
        data dataClass = dataList.get(position);
        holder.bind(dataClass);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subject;
        TextView note;
        TextView date;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            subject = itemView.findViewById(R.id.subject_fav);
            note = itemView.findViewById(R.id.note_fav);
            date = itemView.findViewById(R.id.date_fav);
            cardView = itemView.findViewById(R.id.cardView_fav);
        }

        public void bind(final data dataClass){
            subject.setText(dataClass.getSubject());
            note.setText(dataClass.getNote());
            date.setText(dataClass.getDate());

            int randomColor = colors[random.nextInt(colors.length)];
            cardView.setCardBackgroundColor(itemView.getResources().getColor(randomColor));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(dataClass, getAdapterPosition());
                }
            });
        }
    }
}
