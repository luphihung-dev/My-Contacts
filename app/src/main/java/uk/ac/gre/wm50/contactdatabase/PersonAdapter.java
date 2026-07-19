package uk.ac.gre.wm50.contactdatabase;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {
    private final ArrayList<Person> people;
    private final Context context;
    private final DatabaseHelper dbHelper;

    public PersonAdapter(Context context, ArrayList<Person> people) {
        this.context = context;
        this.people = people;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false);

        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        Person person = people.get(position);

        holder.tvName.setText(person.getName());
        holder.tvPhone.setText(person.getPhone());
        holder.tvDoB.setText(person.getBirthDate());
        holder.tvEmail.setText(person.getEmail());

        // the avatar is stored in the database as a drawable resource NAME
        // (e.g. "avatar_3"), so look up its id at runtime; fall back to the
        // first avatar for rows saved before avatars existed
        String avatarName = person.getAvatar();
        int resId = 0;
        if (avatarName != null) {
            resId = context.getResources().getIdentifier(
                    avatarName, "drawable", context.getPackageName());
        }
        holder.imgAvatar.setImageResource(resId != 0 ? resId : R.drawable.avatar_1);

        // tap a contact to edit it in the form screen
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_ID, person.getId());
            intent.putExtra(MainActivity.EXTRA_NAME, person.getName());
            intent.putExtra(MainActivity.EXTRA_PHONE, person.getPhone());
            intent.putExtra(MainActivity.EXTRA_DOB, person.getBirthDate());
            intent.putExtra(MainActivity.EXTRA_EMAIL, person.getEmail());
            intent.putExtra(MainActivity.EXTRA_AVATAR, person.getAvatar());
            context.startActivity(intent);
        });

        // delete via the bin icon, with a confirmation dialog
        holder.btnDelete.setOnClickListener(view ->
                confirmDelete(holder.getAdapterPosition()));
    }

    private void confirmDelete(int position) {
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        Person person = people.get(position);

        new AlertDialog.Builder(context)
                .setTitle(R.string.delete_title)
                .setMessage(context.getString(R.string.delete_message, person.getName()))
                .setPositiveButton(R.string.delete_yes, (dialog, which) -> {
                    dbHelper.deleteDetails(person.getId());
                    people.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, R.string.msg_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    //create ViewHolder for each item in RecyclerView
    public class PersonViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvPhone, tvDoB, tvEmail;
        public ImageView imgAvatar;
        public ImageButton btnDelete;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvDoB = itemView.findViewById(R.id.tvDoB);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            imgAvatar = itemView.findViewById(R.id.imgPersonAvatar);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
