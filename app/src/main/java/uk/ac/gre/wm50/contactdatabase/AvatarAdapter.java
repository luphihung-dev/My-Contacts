package uk.ac.gre.wm50.contactdatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//adapter for the avatar grid inside the "Choose an avatar" dialog
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
