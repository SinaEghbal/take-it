/*
 * 2016.
 *  The content of this site is licensed under the Creative Commons Apache License 2.0.
 */

package cs.comp2100.edu.au.takeit.app.Model;

import android.provider.BaseColumns;

/**
 * Created by Sina Eghbal on 16/03/2016.
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
