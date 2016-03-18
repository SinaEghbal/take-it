package com.comp2100.MyDiary;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.comp2100.MyDiary.db.NoteDB;
import com.comp2100.MyDiary.db.NoteDBHelper;

public class Main extends Activity {
    /**
     * Called when the activity is first created.
     */
    NoteDBHelper dbHelper;
//    Button btn_submit = (Button) findViewById(R.id.btnSubmit);
    ListView notes;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dbHelper = new NoteDBHelper(this);
        final Cursor cursor = dbHelper.getAllNotes();
        String [] columns = new String[] {
                NoteDB.NoteEntry.COLUMN_NAME_NOTE_ID,
                NoteDB.NoteEntry.COLUMN_NAME_NOTE_TITLE,
                NoteDB.NoteEntry.COLUMN_NAME_NOTE,
                NoteDB.NoteEntry.COLUMN_NAME_TIMESTAMP
        };
        int [] widgets = new int[] {
                R.id.txt_note_id,
                R.id.txt_note
        };
        CursorAdapter adaptor = new SimpleCursorAdapter(this, R.layout.main, cursor, columns, widgets, 0);
        notes = (ListView)findViewById(R.id.note_list);
        notes.setAdapter(adaptor);
        notes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor noteCursor = (Cursor) Main.this.notes.getItemAtPosition(position);
                int noteID = noteCursor.getInt(noteCursor.getColumnIndex(String.valueOf(NoteDBHelper._id)));

            }
            {

        }});

    }
}
