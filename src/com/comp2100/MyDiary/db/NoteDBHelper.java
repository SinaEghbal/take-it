package com.comp2100.MyDiary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/**
 * Created by Sina on 16/03/2016.
 */
public class NoteDBHelper extends SQLiteOpenHelper {
    public static int _id;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Notes.db";

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

    public boolean insertNote(String title, String note, Date date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID, _id);
        _id++;
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_NOTE, note);
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE, title);
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_TIMESTAMP, date.toString());
        db.insert(NoteDB.NoteEntry.TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateNote(int id, String title, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_NOTE, note);
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE, title);
        contentValues.put(NoteDB.NoteEntry.COLUMN_NAME_TIMESTAMP, new Date().toString());
        db.update(NoteDB.NoteEntry.TABLE_NAME, contentValues, NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID + " = ? ", new String[] {Integer.toString(_id)});
        return true;
    }

    public Cursor getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + NoteDB.NoteEntry.TABLE_NAME + " WHERE " +
                NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID + "= ?", new String[] {Integer.toString(id)});
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }

    public Cursor getAllNotes() {
        System.out.println("get all notes");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + NoteDB.NoteEntry.TABLE_NAME, null);
        return res;
    }

    public Integer deleteNotes(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(NoteDB.NoteEntry.TABLE_NAME, NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID + " = ? ",
                new String[] {Integer.toString(id)});
    }
}
