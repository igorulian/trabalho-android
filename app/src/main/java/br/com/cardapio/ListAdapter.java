package br.com.cardapio;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<ListItem> itemList;
    private Context context;
    private OnItemClickListener itemClickListener;
    private OnEditClickListener editClickListener;

    public ListAdapter(Context context, List<ListItem> itemList, OnItemClickListener itemClickListener, OnEditClickListener editClickListener) {
        this.context = context;
        this.itemList = itemList;
        this.itemClickListener = itemClickListener;
        this.editClickListener = editClickListener;
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

        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(item));

        holder.deleteButton.setOnClickListener(v -> {
            if (context instanceof MainListsActivity) {
                ((MainListsActivity) context).removeItem(item);
            }
        });

        holder.editButton.setOnClickListener(v -> editClickListener.onEditClick(item));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        ImageView itemImageView;
        ImageButton deleteButton, editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ListItem item);
    }

    public interface OnEditClickListener {
        void onEditClick(ListItem item);
    }
}
