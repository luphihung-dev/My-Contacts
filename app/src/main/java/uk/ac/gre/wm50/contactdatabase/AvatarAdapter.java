package uk.ac.gre.wm50.contactdatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for the grid of selectable avatars shown in the "Choose an avatar" dialog.
 * Avatars are drawable resources; the adapter reports the chosen resource NAME
 * (e.g. "avatar_3") back through the listener so it can be stored in the database.
 */
public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {

    public interface OnAvatarClickListener {
        void onAvatarClick(String avatarName);
    }

    private final String[] avatarNames;
    private final OnAvatarClickListener listener;

    public AvatarAdapter(String[] avatarNames, OnAvatarClickListener listener) {
        this.avatarNames = avatarNames;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_avatar, parent, false);
        return new AvatarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        String name = avatarNames[position];
        int resId = holder.imgAvatar.getResources().getIdentifier(
                name, "drawable", holder.imgAvatar.getContext().getPackageName());
        holder.imgAvatar.setImageResource(resId);
        holder.imgAvatar.setOnClickListener(view -> listener.onAvatarClick(name));
    }

    @Override
    public int getItemCount() {
        return avatarNames.length;
    }

    public static class AvatarViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgAvatar;

        public AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatarOption);
        }
    }
}
