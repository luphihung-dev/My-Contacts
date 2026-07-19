package uk.ac.gre.wm50.contactdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "details";
    // version 2 added the avatar column, version 3 added the phone column
    private static final int DATABASE_VERSION = 3;

    public static final String ID_COLUMN = "person_id";
    public static final String NAME_COLUMN = "name";
    public static final String PHONE_COLUMN = "phone";
    public static final String DOB_COLUMN = "dob";
    public static final String EMAIL_COLUMN = "email";
    public static final String AVATAR_COLUMN = "avatar";

    private SQLiteDatabase database;

    private static final String DATABASE_CREATE = String.format(
      "CREATE TABLE %s (" +
      "   %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "   %s TEXT, " +
      "   %s TEXT, " +
      "   %s TEXT, " +
      "   %s TEXT, " +
      "   %s TEXT)",
      DATABASE_NAME, ID_COLUMN, NAME_COLUMN, PHONE_COLUMN, DOB_COLUMN, EMAIL_COLUMN, AVATAR_COLUMN);

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);

        Log.v(this.getClass().getName(), DATABASE_NAME + " database upgrade to version " +
                newVersion + " - old data lost");
        onCreate(db);
    }

    private ContentValues toContentValues(Person p) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(NAME_COLUMN, p.getName());
        rowValues.put(PHONE_COLUMN, p.getPhone());
        rowValues.put(DOB_COLUMN, p.getBirthDate());
        rowValues.put(EMAIL_COLUMN, p.getEmail());
        rowValues.put(AVATAR_COLUMN, p.getAvatar());
        return rowValues;
    }

    public long insertDetails(Person p) {
        return database.insertOrThrow(DATABASE_NAME, null, toContentValues(p));
    }

    public void updateDetails(Person p) {
        database.update(DATABASE_NAME, toContentValues(p),
                ID_COLUMN + " = ?", new String[] {String.valueOf(p.getId())});
    }

    public ArrayList<Person> getDetails() {
        Cursor results = database.query(DATABASE_NAME,
                new String[] {ID_COLUMN, NAME_COLUMN, PHONE_COLUMN, DOB_COLUMN, EMAIL_COLUMN, AVATAR_COLUMN},
                null, null, null, null, NAME_COLUMN);

        ArrayList<Person> listPeople = new ArrayList<>();

        results.moveToFirst();
        while (!results.isAfterLast()) {
            int id = results.getInt(0);
            String name = results.getString(1);
            String phone = results.getString(2);
            String dob = results.getString(3);
            String email = results.getString(4);
            String avatar = results.getString(5);

            listPeople.add(new Person(id, name, phone, dob, email, avatar));

            results.moveToNext();
        }
        results.close();
        return listPeople;
    }

    public void deleteDetails(int personId) {
        database.delete(DATABASE_NAME, ID_COLUMN + " = ?",
                new String[] {String.valueOf(personId)});
    }
}
