package com.example.petcare.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petcare.EditLocationActivity;
import com.example.petcare.R;
import com.example.petcare.data.entity.Location;
import com.example.petcare.data.repository.PetCareRepository;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private final List<Location> locationList;
    private final Context context;
    private final PetCareRepository repository;
    private final Runnable onRefreshNeeded;

    public LocationAdapter(List<Location> locationList, Context context, Runnable onRefreshNeeded) {
        this.locationList = locationList;
        this.context = context;
        this.onRefreshNeeded = onRefreshNeeded;
        this.repository = new PetCareRepository(context);
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Location location = locationList.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Location")
                            .setMessage("Are you sure you want to delete " + location.getName() + "?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                repository.deleteLocation(location, () -> {
                                    if (context instanceof Activity) {
                                        ((Activity) context).runOnUiThread(() -> {
                                            Toast.makeText(context, "Location Deleted", Toast.LENGTH_SHORT).show();
                                            if (onRefreshNeeded != null) onRefreshNeeded.run();
                                        });
                                    }
                                });
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> notifyItemChanged(position))
                            .show();
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locationList.get(position);
        holder.tvName.setText(location.getName());
        holder.tvType.setText(location.getType());
        holder.tvAddress.setText(location.getAddress());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditLocationActivity.class);
            intent.putExtra("LOCATION_ID", location.getLocationId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvAddress;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_location_name);
            tvType = itemView.findViewById(R.id.tv_location_type);
            tvAddress = itemView.findViewById(R.id.tv_location_address);
        }
    }
}