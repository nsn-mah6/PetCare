package com.example.petcare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petcare.R;
import com.example.petcare.data.entity.Location;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private final List<Location> locationList;
    private final Context context;

    public LocationAdapter(List<Location> locationList, Context context) {
        this.locationList = locationList;
        this.context = context;
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