package com.comp2100.MyDiary;

import android.provider.BaseColumns;

import java.util.Date;

/**
 * Created by Sina on 16/03/2016.
 */
public final class NoteDB {
    public NoteDB() {

    }

    public static abstract class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_NAME_NOTE_ID = "_id";
        public static final String COLUMN_NAME_NOTE_TITLE = "title";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }

}
