package uk.ac.gre.wm50.contactdatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // extras used when this screen is opened to EDIT an existing contact
    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_DOB = "extra_dob";
    public static final String EXTRA_EMAIL = "extra_email";
    public static final String EXTRA_AVATAR = "extra_avatar";

    // all avatars live in res/drawable; we store the resource NAME in the
    // database because resource ids can change between builds
    private static final String[] AVATAR_NAMES = {
            "avatar_1", "avatar_2", "avatar_3", "avatar_4",
            "avatar_5", "avatar_6", "avatar_7", "avatar_8"
    };

    private String selectedAvatar = AVATAR_NAMES[0];
    private ImageView imgAvatar;
    // -1 means we are creating a new contact, otherwise the id being edited
    private int editingId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClick();
            }
        });

        Button btnViewDetails = findViewById(R.id.btnViewDetails);
        btnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                startActivity(intent);
            }
        });

        imgAvatar = findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(v -> showAvatarPicker());

        // pick the date of birth from a DatePickerDialog instead of free text
        EditText dobTxt = findViewById(R.id.dobText);
        dobTxt.setOnClickListener(v -> showDatePicker(dobTxt));

        // when opened from the contact list, pre-fill the form to edit
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            editingId = intent.getIntExtra(EXTRA_ID, -1);

            EditText nameTxt = findViewById(R.id.nameText);
            EditText phoneTxt = findViewById(R.id.phoneText);
            EditText emailTxt = findViewById(R.id.emailText);

            nameTxt.setText(intent.getStringExtra(EXTRA_NAME));
            phoneTxt.setText(intent.getStringExtra(EXTRA_PHONE));
            dobTxt.setText(intent.getStringExtra(EXTRA_DOB));
            emailTxt.setText(intent.getStringExtra(EXTRA_EMAIL));

            String avatar = intent.getStringExtra(EXTRA_AVATAR);
            if (avatar != null) {
                selectedAvatar = avatar;
                imgAvatar.setImageResource(getAvatarResId(avatar));
            }

            saveBtn.setText(R.string.btn_update);
        }
    }

    private int getAvatarResId(String avatarName) {
        return getResources().getIdentifier(avatarName, "drawable", getPackageName());
    }

    private void showAvatarPicker() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_choose_avatar, null);
        RecyclerView rvAvatars = dialogView.findViewById(R.id.rvAvatars);
        rvAvatars.setLayoutManager(new GridLayoutManager(this, 4));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.choose_avatar_title)
                .setView(dialogView)
                .setNegativeButton(R.string.cancel, null)
                .create();

        rvAvatars.setAdapter(new AvatarAdapter(AVATAR_NAMES, avatarName -> {
            selectedAvatar = avatarName;
            imgAvatar.setImageResource(getAvatarResId(avatarName));
            dialog.dismiss();
        }));

        dialog.show();
    }

    private void showDatePicker(EditText dobTxt) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                dobTxt.setText(String.format(Locale.UK, "%02d/%02d/%04d",
                        dayOfMonth, month + 1, year)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void handleClick() {
        saveDetails();
    }

    private void saveDetails() {
        EditText nameTxt = findViewById(R.id.nameText);
        EditText phoneTxt = findViewById(R.id.phoneText);
        EditText dobTxt = findViewById(R.id.dobText);
        EditText emailTxt = findViewById(R.id.emailText);

        String name = nameTxt.getText().toString().trim();
        String phone = phoneTxt.getText().toString().trim();
        String dob = dobTxt.getText().toString().trim();
        String email = emailTxt.getText().toString().trim();

        // name and phone are required; date of birth and email are optional,
        // but if an email is entered it must at least look like an email
        if (name.isEmpty()) {
            nameTxt.setError(getString(R.string.error_name_required));
            nameTxt.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            phoneTxt.setError(getString(R.string.error_phone_required));
            phoneTxt.requestFocus();
            return;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            phoneTxt.setError(getString(R.string.error_phone_invalid));
            phoneTxt.requestFocus();
            return;
        }
        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError(getString(R.string.error_email_invalid));
            emailTxt.requestFocus();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        Person p = new Person(name, phone, dob, email, selectedAvatar);

        if (editingId >= 0) {
            // editing an existing contact: update the row and go back to the list
            p.setId(editingId);
            dbHelper.updateDetails(p);
            Toast.makeText(this, R.string.msg_updated, Toast.LENGTH_LONG).show();
            finish();
        } else {
            long personId = dbHelper.insertDetails(p);
            Toast.makeText(this, getString(R.string.msg_saved, personId),
                    Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, DetailsActivity.class);
            startActivity(intent);
        }
    }
}
