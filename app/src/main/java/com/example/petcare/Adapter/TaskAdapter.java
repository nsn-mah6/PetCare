package com.example.petcare.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.petcare.EditTaskActivity;
import com.example.petcare.R;
import com.example.petcare.data.entity.CareTask;
import com.example.petcare.data.repository.PetCareRepository;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final List<CareTask> taskList;
    private final Context context;
    private final PetCareRepository repository;
    private final Runnable onRefreshNeeded;

    public void attachToRecyclerView(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CareTask task = taskList.get(position);

                if (direction == ItemTouchHelper.RIGHT) {
                    boolean newStatus = !task.isCompleted();
                    task.setCompleted(newStatus);
                    repository.updateTask(task, () -> {
                        if (onRefreshNeeded != null) onRefreshNeeded.run();
                        if (context instanceof Activity) {
                            ((Activity)context).runOnUiThread(() ->
                                Toast.makeText(context, newStatus ? "Task Completed!" : "Task Unchecked", Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
                    notifyItemChanged(position);
                } else if (direction == ItemTouchHelper.LEFT) {
                    // Delete task
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Task")
                            .setMessage("Are you sure you want to delete " + task.getTaskName() + "?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                repository.deleteTask(task, () -> {
                                    if (onRefreshNeeded != null) onRefreshNeeded.run();
                                });
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> notifyItemChanged(position))
                            .show();
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public TaskAdapter(List<CareTask> taskList, Context context, Runnable onRefreshNeeded) {
        this.taskList = taskList;
        this.context = context;
        this.onRefreshNeeded = onRefreshNeeded;
        this.repository = new PetCareRepository(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        CareTask task = taskList.get(position);
        holder.tvName.setText(task.getTaskName());
        holder.tvTime.setText(task.getTime() + " - " + task.getFrequency() + " [" + task.getCategory() + "]");

        // Temporarily clear listener to prevent loop
        holder.cbCompleted.setOnCheckedChangeListener(null);
        holder.cbCompleted.setChecked(task.isCompleted());

        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            repository.updateTask(task, () -> {
                if (onRefreshNeeded != null) onRefreshNeeded.run();
            });
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditTaskActivity.class);
            intent.putExtra("TASK_ID", task.getTaskId());
            context.startActivity(intent);
        });

        holder.ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete " + task.getTaskName() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        repository.deleteTask(task, () -> {
                            Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show();
                            if (onRefreshNeeded != null) onRefreshNeeded.run();
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbCompleted;
        TextView tvName, tvTime;
        ImageView ivDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbCompleted = itemView.findViewById(R.id.cb_task_completed);
            tvName = itemView.findViewById(R.id.tv_task_name);
            tvTime = itemView.findViewById(R.id.tv_task_time);
            ivDelete = itemView.findViewById(R.id.iv_delete_task);
        }
    }
}