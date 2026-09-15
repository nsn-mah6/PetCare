package com.example.petcare.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petcare.Pet_Details;
import com.example.petcare.R;
import com.example.petcare.data.entity.Pet;
import com.example.petcare.data.repository.PetCareRepository;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private final List<Pet> petList;
    private final Context context;
    private final PetCareRepository repository;

    public PetAdapter(List<Pet> petList, Context context) {
        this.petList = petList;
        this.context = context;
        this.repository = new PetCareRepository(context);
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pets, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.tvName.setText(pet.getName());
        holder.tvBreedSpecies.setText(pet.getBreed() + " • " + pet.getSpecies());
        holder.tvAge.setText(pet.getAge() + " yrs old");
        holder.tvWeight.setText(pet.getWeight() + " kg");
        holder.tvDescription.setText("Diet: " + pet.getDietaryPreferences());

        // Dynamic Pending Tasks Count
        repository.getPendingTaskCountForPet(pet.getPetId(), count -> {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(() -> {
                    if (count > 0) {
                        holder.tvPending.setText(count + (count == 1 ? " task" : " tasks"));
                        holder.tvPending.setVisibility(View.VISIBLE);
                    } else {
                        holder.tvPending.setText("All Done");
                        holder.tvPending.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        if (pet.getImageUri() != null && !pet.getImageUri().isEmpty()) {
            try {
                holder.ivProfile.setImageURI(Uri.parse(pet.getImageUri()));
            } catch (Exception e) {
                holder.ivProfile.setImageResource(R.drawable.cat);
            }
        } else {
            holder.ivProfile.setImageResource(R.drawable.cat);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Pet_Details.class);
            intent.putExtra("PET_ID", pet.getPetId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBreedSpecies, tvAge, tvWeight, tvDescription, tvPending;
        ImageView ivProfile;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_pet_name);
            tvBreedSpecies = itemView.findViewById(R.id.tv_breed_species_combined);
            tvAge = itemView.findViewById(R.id.tv_pet_age);
            tvWeight = itemView.findViewById(R.id.tv_pet_weight_value);
            tvDescription = itemView.findViewById(R.id.tv_pet_description);
            tvPending = itemView.findViewById(R.id.tv_pending_tasks_count);
            ivProfile = itemView.findViewById(R.id.iv_pet_profile_icon);
        }
    }
}