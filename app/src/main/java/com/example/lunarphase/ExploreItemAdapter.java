package com.example.lunarphase;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExploreItemAdapter extends RecyclerView.Adapter<ExploreItemAdapter.ExploreViewHolder> {

    private final List<ExploreModel> items;
    private final Set<Integer> animatedPositions = new HashSet<>(); // To track shown positions

    public ExploreItemAdapter(List<ExploreModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore_page, parent, false);
        return new ExploreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
        ExploreModel item = items.get(position);

        holder.imageCelestial.setImageResource(item.getImageResId());
        holder.tvCelestialName.setText(item.getName());

        // Clear any previous animation and start fresh rotation
        holder.imageCelestial.clearAnimation();
        holder.imageCelestial.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.rotate_planet));

        // Apply typewriter only once
        if (animatedPositions.contains(position)) {
            holder.tvCelestialDescription.setText(item.getDescription()); // Show full text directly
        } else {
            holder.tvCelestialDescription.setText("");
            setTypewriterEffect(holder.tvCelestialDescription, item.getDescription(), 35);
            animatedPositions.add(position); // Mark this position as animated
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ExploreViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCelestial;
        TextView tvCelestialName, tvCelestialDescription;

        public ExploreViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCelestial = itemView.findViewById(R.id.imageCelestial);
            tvCelestialName = itemView.findViewById(R.id.tvCelestialName);
            tvCelestialDescription = itemView.findViewById(R.id.tvCelestialDescription);
        }
    }

    private void setTypewriterEffect(final TextView textView, final String fullText, final long delayPerChar) {
        textView.setText("");
        final Handler handler = new Handler();
        final int[] index = {0};

        Runnable characterAdder = new Runnable() {
            @Override
            public void run() {
                if (index[0] < fullText.length()) {
                    textView.append(String.valueOf(fullText.charAt(index[0])));
                    index[0]++;
                    handler.postDelayed(this, delayPerChar);
                }
            }
        };

        handler.postDelayed(characterAdder, delayPerChar);
    }
}
