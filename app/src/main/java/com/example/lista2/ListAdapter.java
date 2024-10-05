package com.example.lista2;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<ListItem> itemList;
    private Context context;
    private OnItemClickListener listener;

    public ListAdapter(Context context, List<ListItem> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem item = itemList.get(position);
        holder.itemNameTextView.setText(item.getName());
        if (item.getImageUri() != null) {
            holder.itemImageView.setImageURI(Uri.parse(item.getImageUri()));
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.deleteButton.setOnClickListener(v -> {
            if (context instanceof MainListsActivity) {
                ((MainListsActivity) context).removeItem(item);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        ImageView itemImageView; // Certifique-se de declarar o ImageView
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemImageView = itemView.findViewById(R.id.itemImageView); // Certifique-se de referenciar o ImageView corretamente
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ListItem item);
    }
}
