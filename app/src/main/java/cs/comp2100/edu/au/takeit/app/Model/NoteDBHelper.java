/*
 * 2016.
 *  The content of this site is licensed under the Creative Commons Apache License 2.0.
 */

package cs.comp2100.edu.au.takeit.app.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;
import java.util.Random;

/**
 * Created by Sina Eghbal on 16/03/2016.
 */
public class NoteDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Notes.db";
    public static final Random idGenerator = new Random();

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = "INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NoteDB.NoteEntry.TABLE_NAME + "(" +
                    NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID + " "+ INT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE + TEXT_TYPE + COMMA_SEP +
                    NoteDB.NoteEntry.COLUMN_NAME_NOTE + TEXT_TYPE + COMMA_SEP +
                    NoteDB.NoteEntry.COLUMN_NAME_TIMESTAMP + TEXT_TYPE + ")";

    private static  final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NoteDB.NoteEntry.TABLE_NAME;

    public NoteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /*Inserts notes into our database.*/
    public int insertNote(String title, String note, Date date) {
        int id = idGenerator.nextInt(100000);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID, id);
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_NOTE, note);
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE, title);
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_TIMESTAMP, date.toString());
        db.insert(NoteDB.NoteEntry.TABLE_NAME, null, contentValues);
        return id;
    }

    /*
    *updates the notes using their id.*/
    public boolean updateNote(int id, String title, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_NOTE, note);
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE, title);
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_TIMESTAMP, new Date().toString());
        db.update(NoteDB.NoteEntry.TABLE_NAME, contentValues, NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID + " = ? ", new String[] {Integer.toString(id)});
        return true;
    }

    /*gets the notes.*/
    public Cursor getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + NoteDB.NoteEntry.TABLE_NAME + " WHERE " +
                NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID + "= ?", new String[] {Integer.toString(id)});
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }

    /*Fetches all the notes from the database.*/
    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + NoteDB.NoteEntry.TABLE_NAME + " ORDER BY " + NoteDB.NoteEntry.COLUMN_NAME_TIMESTAMP + " DESC", null);
        return res;
    }

    /*Given a keyboard, this method will return all the notes contataining it it their title or body.*/
    public Cursor search(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + NoteDB.NoteEntry.TABLE_NAME + " WHERE " +
                NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE + " LIKE '%" + keyword + "%' UNION SELECT * FROM " +
                NoteDB.NoteEntry.TABLE_NAME + " WHERE " + NoteDB.NoteEntry.COLUMN_NAME_NOTE +
                " LIKE '%" + keyword + "%'", null);
        return res;
    }

    /*Deletes the node with the given id from our database.*/
    public Integer deleteNote(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(NoteDB.NoteEntry.TABLE_NAME, NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID + " = ? ",
                new String[] {Integer.toString(id)});
    }
}