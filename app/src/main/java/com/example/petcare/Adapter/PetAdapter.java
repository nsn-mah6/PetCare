package com.example.petcare.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
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
    private final Runnable onRefreshNeeded;

    public PetAdapter(List<Pet> petList, Context context, Runnable onRefreshNeeded) {
        this.petList = petList;
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
                Pet pet = petList.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Pet")
                            .setMessage("Are you sure you want to delete " + pet.getName() + "? This will also remove all their care routines.")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                repository.deletePet(pet, () -> {
                                    if (context instanceof Activity) {
                                        ((Activity) context).runOnUiThread(() -> {
                                            Toast.makeText(context, "Pet Removed", Toast.LENGTH_SHORT).show();
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
                holder.ivProfile.setImageResource(R.drawable.banner);
            }
        } else {
            holder.ivProfile.setImageResource(R.drawable.banner);
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